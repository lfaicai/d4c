package org.faicai.d4c.core.db;

import java.util.Set;

public abstract class DbSql {


    /**
     * 获取数据库所有表字段详情
     * @param dbName 数据库名称
     * @param schema schema名称
     */
    public abstract String allTableInfo(String dbName, String schema);

    public abstract String tableDetailsSql(String schema, String tableName, Set<String> columnNames);

    public abstract String databaseListSql();

    public abstract String schemaListSql();

    /**
     * 分页查询
     * @param sql 原sql
     * @param offset 偏移量
     * @param rowCount 每页条数
     * @return 添加分页后的sql
     */
    public abstract String addPage(String sql, Long offset, Long rowCount );

    public abstract String columnNamesSql(String dbName, String schema, String tableName);

    public String countSql(String sql){
        return String.format("SELECT COUNT(*) FROM (%s) t", sql);
    }
}
