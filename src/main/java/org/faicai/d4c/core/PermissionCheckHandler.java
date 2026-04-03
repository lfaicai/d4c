package org.faicai.d4c.core;
import org.faicai.d4c.constant.ResponseCode;
import org.faicai.d4c.enums.ResourceType;
import org.faicai.d4c.exception.BusinessException;
import org.faicai.d4c.pojo.entity.UserResource;
import org.faicai.d4c.service.ResourceService;
import org.faicai.d4c.utils.sql.SqlInfo;
import org.faicai.d4c.utils.sql.SqlParseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;


/**
 * 权限控制处理器
 */
@Component
@Order(3)
public class PermissionCheckHandler extends AbstractSqlExecutionHandler {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DataSourceManager dataSourceManager;

    @Override
    public void handle(SqlExecutionContext context) {
        hasPermission(context);
        invokeNext(context);
    }

    private void hasPermission(SqlExecutionContext context) {
        SqlInfo sqlInfo = context.getSqlInfo();
        List<UserResource> currentUserResources = resourceService.findCurrentUserResourcesByAction(
                sqlInfo.getConnectId(), sqlInfo.getAction());
        if (currentUserResources.isEmpty()) {
            throw new BusinessException(ResponseCode.INSUFFICIENT_DATABASE_PERMISSIONS);
        }
        // 预构建资源映射表
        Map<ResourceType, Set<String>> resourceKeyMap = buildResourceKeyMap(currentUserResources);
        SqlParseUtils.SqlDefinition sqlDefinition = SqlParseUtils.sqlParse(sqlInfo);
        List<SqlParseUtils.SqlDetails> sqlDetailsList = sqlDefinition.getSqlDetailsList();
        sqlInfo.setAction(sqlDefinition.getAction());

        // key: tableFullName (db.schema.table / db.table / schema.table / table)
        Map<String, Set<String>> tableAllColumnsMap = new HashMap<>();
        for (SqlParseUtils.SqlDetails sqlDetails : sqlDetailsList) {
            if (!StringUtils.hasText(sqlDetails.getDatabaseName())) {
                sqlDetails.setDatabaseName(sqlInfo.getDb());
            }
            if ("*".equals(sqlDetails.getColumnName())) {
                String tableKey = sqlDetails.getTableFullName();
                if (tableAllColumnsMap.containsKey(tableKey)) {
                    continue;
                }
                Set<String> tableAllColumns = dataSourceManager.getTableColumns(new SqlInfo(
                        sqlInfo.getConnectId(),
                        sqlDetails.getDatabaseName(),
                        sqlInfo.getDbType().name(),
                        sqlDetails.getSchemaName(),
                        sqlDetails.getTableName(),
                        sqlInfo.getSql()
                ));
                tableAllColumnsMap.put(tableKey, tableAllColumns);
            }
        }

        // 检查每个SQL定义的权限
        for (SqlParseUtils.SqlDetails sqlDetails : sqlDetailsList) {
            checkDatabasePermission(sqlDetails, resourceKeyMap);
            checkSchemaPermission(sqlDetails, resourceKeyMap);
            checkTablePermission(sqlDetails, resourceKeyMap);

            // 如果是 * 则判断 是否都有这些字段的权限
            if ("*".equals(sqlDetails.getColumnName())) {
                // 如果是*并且没有全部字段权限，只返回有权限字段的数据
                String originalColumn = sqlDetails.getColumnName();
                Set<String> tableAllColumns = tableAllColumnsMap.getOrDefault(sqlDetails.getTableFullName(), Set.of());
                for (String tableAllColumn : tableAllColumns) {
                    sqlDetails.setColumnName(tableAllColumn);
                    try {
                        checkColumnPermission(sqlDetails, resourceKeyMap);
                    } catch (Exception e) {
                        context.setAllColumnPermission(false);
                        break;
                    }
                }
                sqlDetails.setColumnName(originalColumn);
            }else {
                checkColumnPermission(sqlDetails, resourceKeyMap);
            }

        }

        // 权限校验通过后，对 SELECT * / a.* 做字段级收敛改写
        if (sqlInfo.getAction() == org.faicai.d4c.enums.DataBaseAction.SELECT && !context.isAllColumnPermission()) {
            String rewritten = SqlParseUtils.rewriteSelectStarToPermittedColumns(
                    sqlInfo,
                    resourceKeyMap,
                    tableAllColumnsMap,
                    dataSourceManager::getTableColumns
            );
            if (StringUtils.hasText(rewritten) && !rewritten.equals(sqlInfo.getSql())) {
                sqlInfo.setSql(rewritten);
            }
        }
    }

    /**
     * 构建资源
     * @param resources key {@link ResourceType}  value 全路径名称
     */
    private Map<ResourceType, Set<String>> buildResourceKeyMap(List<UserResource> resources) {
        Map<ResourceType, Set<String>> resourceKeyMap = new EnumMap<>(ResourceType.class);
        for (UserResource resource : resources) {
            resourceKeyMap
                    .computeIfAbsent(resource.getResourceType(), k -> new HashSet<>())
                    .add(resource.getKey().toLowerCase());
        }
        return resourceKeyMap;
    }

    /**
     * 校验数据库权限
     */
    private void checkDatabasePermission(SqlParseUtils.SqlDetails sqlDetails, Map<ResourceType, Set<String>> resourceKeyMap) {
        String databaseName = sqlDetails.getDatabaseFullName();
        if (StringUtils.hasText(databaseName)) {
            if (!resourceKeyMap.containsKey(ResourceType.DATABASE)) throw new BusinessException(ResponseCode.NOT_HAVE_DATABASE_PERMISSIONS);
            if (!resourceKeyMap.get(ResourceType.DATABASE).contains(databaseName)) throw new BusinessException(ResponseCode.NOT_HAVE_CUSTOM_DATABASE_PERMISSIONS, databaseName);
        }
    }

    /**
     * 校验Schema权限
     */
    private void checkSchemaPermission(SqlParseUtils.SqlDetails sqlDetails, Map<ResourceType, Set<String>> resourceKeyMap) {
        String schemaFullName = sqlDetails.getSchemaFullName();
        if (StringUtils.hasText(schemaFullName)) {
            if (!resourceKeyMap.containsKey(ResourceType.SCHEMA)) throw new BusinessException(ResponseCode.NOT_HAVE_SCHEMA_PERMISSIONS);
            if (!resourceKeyMap.get(ResourceType.SCHEMA).contains(schemaFullName)) throw new BusinessException(ResponseCode.NOT_HAVE_CUSTOM_SCHEMA_PERMISSIONS, sqlDetails.getSchemaName());
        }
    }

    /**
     * 校验表权限
     */
    private void checkTablePermission(SqlParseUtils.SqlDetails sqlDetails, Map<ResourceType, Set<String>> resourceKeyMap) {
        String tableFullName = sqlDetails.getTableFullName();
        if (StringUtils.hasText(tableFullName)) {
            if (!resourceKeyMap.containsKey(ResourceType.TABLE)) throw new BusinessException(ResponseCode.NOT_HAVE_TABLE_PERMISSIONS);
            String databaseName = sqlDetails.getDatabaseName();
            String schemaName = sqlDetails.getSchemaName();
            String tableName = sqlDetails.getTableName();
            if (!resourceKeyMap.get(ResourceType.TABLE).contains(tableFullName)) throw new BusinessException(ResponseCode.NOT_HAVE_CUSTOM_TABLE_PERMISSIONS, "", "" , schemaName == null ? databaseName: schemaName, tableName);
        }
    }

    /**
     * 校验字段权限
     */
    private void checkColumnPermission(SqlParseUtils.SqlDetails sqlDetails, Map<ResourceType, Set<String>> resourceKeyMap) {
        String columnFullName = sqlDetails.getColumnFullName();
        if (StringUtils.hasText(columnFullName)) {
            if (!resourceKeyMap.containsKey(ResourceType.COLUMN)) throw new BusinessException(ResponseCode.NOT_HAVE_COLUMN_PERMISSIONS);
            if (!resourceKeyMap.get(ResourceType.COLUMN).contains(columnFullName))
                throw new BusinessException(ResponseCode.NOT_HAVE_CUSTOM_COLUMN_PERMISSIONS, "", "", sqlDetails.getTableName(), sqlDetails.getColumnName());
        }
    }


}