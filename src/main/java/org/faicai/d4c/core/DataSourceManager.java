package org.faicai.d4c.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPTZ;
import org.apache.ibatis.datasource.DataSourceException;
import org.faicai.d4c.constant.ResponseCode;
import org.faicai.d4c.core.db.DbSql;
import org.faicai.d4c.core.db.DbSqlFactory;
import org.faicai.d4c.enums.ResourceType;
import org.faicai.d4c.exception.BusinessException;
import org.faicai.d4c.pojo.entity.DataBaseConnectConfig;
import org.faicai.d4c.pojo.entity.Resource;
import org.faicai.d4c.pojo.vo.SelectResult;
import org.faicai.d4c.pojo.vo.TableDetailsVO;
import org.faicai.d4c.service.DataBaseConnectConfigService;
import org.faicai.d4c.enums.DbDialect;
import org.faicai.d4c.utils.sql.SqlInfo;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSourceManager {

    private static final Map<String, DruidDataSource> dataSourceMap = new ConcurrentHashMap<>();

    private final DataBaseConnectConfigService dbConnectConfigService;

    String getKey(SqlInfo sqlInfo) {
        return sqlInfo.getConnectId() + "_" + sqlInfo.getDb();
    }

    /**
     * 注册数据源（带连接验证）
     */
    public synchronized void addDataSource(DataBaseConnectConfig config, SqlInfo sqlInfo) throws DataSourceException {
        String key = getKey(sqlInfo);
        if (dataSourceMap.containsKey(key)) {
            throw new DataSourceException("数据源已存在: " + config.getId());
        }
        try {
            config.setDatabaseName(sqlInfo.getDb());
            config.setSchemaName(sqlInfo.getSchema());
            DruidDataSource ds = getDataSource(config);
            // 验证连接（避免配错）
            // 能获取到连接即说明配置正确
            try {
                ds.getConnection();
            } catch (SQLException e) {
                ds.close();
                throw new DataSourceException("获取数据源失败: " + config.getId(), e);
            }
            dataSourceMap.put(key, ds);
        } catch (Exception e) {
            throw new DataSourceException("创建数据源失败: " + config.getId(), e);
        }
    }

    DruidDataSource getDataSource(DataBaseConnectConfig config) {
        try {
            DruidDataSource ds = new DruidDataSource();
            DbDialect dbDialect = config.getDbType();
            ds.setDriverClassName(dbDialect.getDriverClassName());
            ds.setUrl(dbDialect.formatJdbcUrl(config.getHost(), config.getPort(), config.getDatabaseName(), config.getSchemaName()));
            ds.setUsername(config.getUserName());
            ds.setPassword(config.getPassword());

            // 设置池参数
            ds.setInitialSize(2);
            ds.setMaxActive(config.getMaxConnections() == null ? 5 : config.getMaxConnections());
            ds.setMinIdle(1);
            ds.setMaxWait(30);
            return ds;
        } catch (Exception e) {
            throw new DataSourceException("创建数据源失败: " + config.getId(), e);
        }
    }

    public Connection getConnection(DataBaseConnectConfig config) throws BusinessException {
        DruidDataSource dataSource = getDataSource(config);
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            dataSource.close();
            log.error("getConnection error ",e);
            throw new BusinessException(ResponseCode.GET_CONNECT_ERROR);
        }
    }

    public Connection getConnection(SqlInfo sqlInfo) throws BusinessException {
        DruidDataSource dataSource = getDataSource(sqlInfo);
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            dataSourceMap.remove(getKey(sqlInfo));
            dataSource.close();
            log.error("getConnection error ",e);
            throw new BusinessException(ResponseCode.GET_CONNECT_ERROR);
        }
    }

    /**
     * 获取数据源
     */
    public DruidDataSource getDataSource(SqlInfo sqlInfo) throws DataSourceException {
        String key = getKey(sqlInfo);
        DruidDataSource ds = dataSourceMap.get(key);
        if (ds == null) {
            DataBaseConnectConfig dataBaseConnectConfig = dbConnectConfigService.getById(sqlInfo.getConnectId());
            if (dataBaseConnectConfig == null) throw new BusinessException(ResponseCode.CONNECT_NOT_EXIST);
            addDataSource(dataBaseConnectConfig, sqlInfo);
            ds = dataSourceMap.get(key);
        }
        return ds;
    }


    public Object execute(SqlInfo sqlInfo) throws SQLException {
        DataSource dataSource = getDataSource(sqlInfo);
        try (Connection connection = dataSource.getConnection()) {
            return switch (sqlInfo.getAction()) {
                case SELECT -> query(connection, sqlInfo.getSql());
                case INSERT, UPDATE, DELETE -> update(connection, sqlInfo.getSql());
                default -> execute(connection, sqlInfo.getSql());
            };
        }
    }

    /**
     * 执行 SELECT 查询
     */
    public SelectResult query(Connection conn, String sql) throws SQLException {
//        sql = sql.replace("`", "");
        SelectResult selectResult = new SelectResult();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> result = new ArrayList<>();
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                List<String> heads = getHeaderList(meta);
                selectResult.setHeads(heads);
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        Object object = rs.getObject(i);
                        if (object instanceof TIMESTAMP timestamp) {
                            row.put(meta.getColumnLabel(i), timestamp.timestampValue());
                        }
                        else if (object instanceof TIMESTAMPTZ timestamptz) {
                            row.put(meta.getColumnLabel(i), timestamptz.offsetDateTimeValue());
                        }
                        else {
                            row.put(meta.getColumnLabel(i), object);
                        }
                    }
                    result.add(row);
                }
                selectResult.setRows(result);
            }
        }
        return selectResult;
    }

    private List<String> getHeaderList(ResultSetMetaData resultSetMetaData) throws SQLException {
        List<String> heads = new ArrayList<>();
        int col = resultSetMetaData.getColumnCount();
        for (int i = 1; i <= col; i++) {
            String columnLabel = resultSetMetaData.getColumnLabel(i);
            if (columnLabel != null) {
                heads.add(columnLabel);
            }else {
                heads.add(resultSetMetaData.getColumnName(i));
            }
        }
        return heads;
    }

    /**
     * 执行 INSERT/UPDATE/DELETE
     */
    public int update(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.executeUpdate();
        }
    }

    /**
     * 执行 DDL 语句（CREATE/DROP/ALTER）
     */
    public boolean execute(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            return stmt.execute(sql);
        }
    }

    /**
     * 删除数据源
     */
    public void removeDataSource(String key) {
        DruidDataSource ds = dataSourceMap.remove(key);
        if (ds != null) {
            ds.close();
        }
    }

    /**
     * 列出所有数据源
     */
    public Map<String, DruidDataSource> listDataSources() {
        return dataSourceMap;
    }

    /**
     * 获取连接池状态
     */
    public Map<String, Object> getStatus(String key) {
        DruidDataSource ds = dataSourceMap.get(key);
        if (ds == null) {
            throw new BusinessException(ResponseCode.CONNECT_NOT_EXIST);
        }
        Map<String, Object> status = new ConcurrentHashMap<>();
        status.put("activeCount", ds.getActiveCount());     // 活跃连接数
        status.put("poolingCount", ds.getPoolingCount());   // 空闲连接数
        status.put("maxActive", ds.getMaxActive());         // 最大连接数
        return status;
    }

    /**
     * 应用关闭时清理
     */
    public void shutdown() {
        for (DruidDataSource ds : dataSourceMap.values()) {
            ds.close();
        }
        dataSourceMap.clear();
    }

    public Set<String> getDatabaseList(DataBaseConnectConfig config) throws SQLException {
        // 获取数据库
        try (Connection connection = getConnection(config)) {
            DbSql dbSql = DbSqlFactory.getDbSql(config.getDbType());
            if (dbSql == null) {
                throw new BusinessException("No SQL implementation found for database type: " + config.getDbType());
            }
            String sql = dbSql.databaseListSql();
            SelectResult selectResult = query(connection, sql);
            // 处理列资源
            List<Map<String, Object>> resourceMap = selectResult.getRows();
            // 处理列资源
            Set<String> databaseList = new HashSet<>();
            for (Map<String, Object> resources : resourceMap) {
                resources.forEach((key, value) -> {
                    databaseList.add(String.valueOf(value));
                });
            }
            return databaseList;
        }
    }


    public List<Resource> getResourceInfo(DataBaseConnectConfig config) throws SQLException {
        // 获取数据库
        try (Connection connection = getConnection(config)) {
            DbSql dbSql = DbSqlFactory.getDbSql(config.getDbType());
            if (dbSql == null) {
                throw new BusinessException("No SQL implementation found for database type: " + config.getDbType());
            }
            // 获取
            String sql = dbSql.allTableInfo(config.getDatabaseName(), config.getSchemaName());
            SelectResult selectResult = query(connection, sql);

            List<Resource> resources = processColumnResources(config, selectResult);
            addTableResources(config, resources);
            addSchemaResources(config, resources);
            addDatabaseResources(config, resources);
            return resources;
        }
    }


    public Set<String> getTableColumns(SqlInfo sqlInfo) {
        DataBaseConnectConfig config = dbConnectConfigService.getById(sqlInfo.getConnectId());
        try (Connection conn = getConnection(sqlInfo)) {
            DbSql dbSql = DbSqlFactory.getDbSql(config.getDbType());
            if (dbSql == null) {
                throw new BusinessException("No SQL implementation found for database type: " + config.getDbType());
            }
            String sql = dbSql.columnNamesSql(sqlInfo.getDb(), sqlInfo.getSchema(), sqlInfo.getTable());
            Set<String> result = new HashSet<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String columnName = rs.getString(1);
                        result.add(columnName);
                    }
                }
            }
            return result;

        } catch (SQLException e) {
            throw new BusinessException(ResponseCode.GET_CONNECT_ERROR);
        }

    }

    /**
     * 获取表详情
     * @param connectId 连接id
     * @param tableName 表名称
     * @param currentUserColumns 用户拥有的字段权限
     * @return
     */
    public SelectResult getTableDetails(Long connectId, String schemaName, String tableName, Set<String> currentUserColumns) {
        DataBaseConnectConfig config = dbConnectConfigService.getById(connectId);
        if (config == null) throw new BusinessException(ResponseCode.CONNECT_NOT_EXIST);
        try (Connection conn = getConnection(config)) {
            DbSql dbSql = DbSqlFactory.getDbSql(config.getDbType());
            if (dbSql == null) {
                throw new BusinessException("No SQL implementation found for database type: " + config.getDbType());
            }
            String sql = dbSql.tableDetailsSql(schemaName, tableName, currentUserColumns);
            return query(conn, sql);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(ResponseCode.GET_CONNECT_ERROR);
        }
    }


    /**
     * 处理资源
     */
    private List<Resource> processColumnResources(DataBaseConnectConfig config, SelectResult selectResult) {
        List<Map<String, Object>> resourceMap = selectResult.getRows();
        List<Resource> resources = new ArrayList<>(resourceMap.size() + 10); // 预分配额外空间

        // 处理列资源
        for (Map<String, Object> columnMap : resourceMap) {
            Resource resource = createBaseResource(config);
            resource.setResourceType(ResourceType.COLUMN);
            // idx 序号
            setResourceProperty(columnMap, "idx", value ->
                    resource.setIdx(Integer.parseInt(String.valueOf(value))));
            // 数据库名称
            setResourceProperty(columnMap, "databaseName", value ->
                    resource.setDatabaseName(String.valueOf(value)));
            // schema名称
            setResourceProperty(columnMap, "schemaName", value ->
                    resource.setSchemaName(String.valueOf(value)));
            // 表名称
            setResourceProperty(columnMap, "tableName", value ->
                    resource.setTableName(String.valueOf(value)));
            // 字段名称
            setResourceProperty(columnMap, "columnName", value ->
                    resource.setColumnName(String.valueOf(value)));
            // 字段注释
            setResourceProperty(columnMap, "description", value ->
                    resource.setDescription(String.valueOf(value)));
            // 字段注释-ai
            resource.setDescriptionAi(resource.getDescription());
            // 字段类型
            setResourceProperty(columnMap, "dataType", value ->
                    resource.setDataType(String.valueOf(value)));

            resources.add(resource);
        }

        return resources;
    }


    private void addTableResources(DataBaseConnectConfig config, List<Resource> resources) {
        // 获取所有不重复的表名
        List<Resource> tableResources = resources.stream()
                .filter(Objects::nonNull)
                .filter(v -> v.getTableName() != null)
                .collect(Collectors.toMap(Resource::getTableNameKey, r -> r, (e, r) -> e))
                .values()
                .stream()
                .toList();

        // 为每个表添加表级资源
        for (Resource tableResource : tableResources) {
            Resource table = createBaseResource(config);
            table.setTableName(tableResource.getTableName());
            table.setDatabaseName(tableResource.getDatabaseName());
            table.setSchemaName(tableResource.getSchemaName());
            table.setResourceType(ResourceType.TABLE);
            resources.add(table);

        }

    }

    private void addSchemaResources(DataBaseConnectConfig config, List<Resource> resources) {
        if (!config.getDbType().hasSchema()) {
            return;
        }
        // 获取所有不重复的Schema名
        List<Resource> schemaResources = resources.stream().filter(Objects::nonNull).filter(v -> v.getSchemaName() != null)
                .collect(Collectors.toMap(Resource::getSchemaName, r -> r, (e, r) -> e))
                .values()
                .stream()
                .toList();


        if (!schemaResources.isEmpty()) {
            for (Resource schema : schemaResources) {
                Resource schemaResource = createBaseResource(config);
                schemaResource.setDatabaseName(schema.getDatabaseName());
                schemaResource.setSchemaName(schema.getSchemaName());
                schemaResource.setResourceType(ResourceType.SCHEMA);
                resources.add(schemaResource);
            }

        }
    }

    private void addDatabaseResources(DataBaseConnectConfig config, List<Resource> resources) {
        if (!config.getDbType().hasDatabase()) {
            return;
        }

        // 获取所有不重复的db名
        Set<String> databaseNames = resources.stream()
                .map(Resource::getDatabaseName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!databaseNames.isEmpty()) {
            for (String databaseName : databaseNames) {
                Resource databaseResource = new Resource();
                databaseResource.setDatabaseConnectId(config.getId());
                databaseResource.setDatabaseName(databaseName);
                databaseResource.setResourceType(ResourceType.DATABASE);
                resources.add(databaseResource);
            }
        }
    }

    private Resource createBaseResource(DataBaseConnectConfig config) {
        Resource resource = new Resource();
        resource.setDatabaseConnectId(config.getId());

        return resource;
    }

    private void setResourceProperty(Map<String, Object> map, String key, Consumer<Object> setter) {
        Object value = map.get(key);
        if (value != null && !String.valueOf(value).trim().isEmpty()) {
            setter.accept(value);
        }
    }

    public Long count(SqlInfo sqlInfo) throws SQLException {
        DbSql dbSql = DbSqlFactory.getDbSql(sqlInfo.getDbDialect());
        assert dbSql != null;
        String countSql = dbSql.countSql(sqlInfo.getSql());
        DataSource dataSource = getDataSource(sqlInfo);
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(countSql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                if (rs.getObject(1) instanceof BigDecimal) {
                    return rs.getBigDecimal(1).longValue();
                }
                return rs.getLong(1);
            }
        }
    }

}
