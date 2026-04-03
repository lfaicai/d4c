package org.faicai.d4c.core.db;


import org.faicai.d4c.utils.SqlUtil;

import java.util.Set;

public class MysqlBaseSql extends DbSql {

    @Override
    public String allTableInfo(String dbName, String schema) {
        return """
                select
                	ORDINAL_POSITION  as idx ,
                	TABLE_SCHEMA as databaseName,
                 	TABLE_NAME as tableName,
                 	COLUMN_NAME as columnName,
                 	COLUMN_COMMENT as description,
                    DATA_TYPE as 'dataType',
                    IS_NULLABLE as 'nullable',
                    COLUMN_DEFAULT as 'defaultValue'
                from
                	INFORMATION_SCHEMA.COLUMNS
                where
                	TABLE_SCHEMA NOT IN ('information_schema', 'mysql', 'performance_schema', 'sys')
                order by
                	TABLE_NAME,
                	ORDINAL_POSITION;
                """;
    }

    @Override
    public String tableDetailsSql(String schema, String tableName, Set<String> columnNames) {
        return String.format("""
                select
                    COLUMN_NAME as 'columnName',
                    DATA_TYPE as 'dataType',
                    CHARACTER_MAXIMUM_LENGTH as 'charLength',
                    NUMERIC_PRECISION as 'numericPrecision',
                    NUMERIC_SCALE as 'numericScale',
                    IS_NULLABLE as 'nullable',
                    COLUMN_DEFAULT as 'defaultValue',
                    COLUMN_COMMENT as 'columnComment'
                from
                    INFORMATION_SCHEMA.COLUMNS
                where
                    TABLE_NAME = %s %s
                order by
                    ORDINAL_POSITION;
                """, SqlUtil.escapeSqlValue(tableName), SqlUtil.andInClause("COLUMN_NAME", columnNames));
    }

    @Override
    public String databaseListSql() {
        return "SHOW DATABASES;";
    }

    @Override
    public String schemaListSql() {
        return "";
    }

    @Override
    public String addPage(String sql, Long offset, Long rowCount) {
        return String.format("SELECT * FROM (%s) t LIMIT %s, %s", sql, (offset - 1) * rowCount, rowCount);
    }

    @Override
    public String columnNamesSql(String dbName, String schema, String tableName) {
        return String.format("""
                select
                    COLUMN_NAME as columnName
                from
                    INFORMATION_SCHEMA.COLUMNS
                where
                    TABLE_SCHEMA = %s and TABLE_NAME  = %s
                order by ORDINAL_POSITION;
                """, SqlUtil.escapeSqlValue(dbName), SqlUtil.escapeSqlValue(tableName));
    }


}
