package org.faicai.d4c.core.db;

import org.faicai.d4c.utils.SqlUtil;

import java.util.Set;

/**
 * @Describe：pgSql
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-08-28
 */
public class PgBaseSql extends DbSql {

    @Override
    public String allTableInfo(String dbName, String schema) {
        return """
                    SELECT
                        a.attnum as "idx",
                        current_database() as "databaseName",
                        n.nspname as "schemaName",
                        c.relname AS "tableName",
                        a.attname AS "columnName",
                        col_description(a.attrelid, a.attnum) AS "description",
                        pg_catalog.format_type(a.atttypid, -1) AS "dataType"
                    FROM
                        pg_class c
                    JOIN
                        pg_attribute a ON a.attrelid = c.oid
                    JOIN
                        pg_namespace n ON n.oid = c.relnamespace
                    WHERE
                       c.relkind = 'r'
                       AND a.attnum > 0
                       AND NOT a.attisdropped
                       AND n.nspname NOT IN ('information_schema', 'pg_catalog', 'pg_toast')
                       AND n.nspname NOT LIKE 'pg_temp%'
                       AND n.nspname NOT LIKE 'pg_toast_temp%'
                    ORDER BY
                        current_database(), n.nspname, c.relname, a.attnum;
                """;
    }

    @Override
    public String tableDetailsSql(String schema, String tableName, Set<String> columnNames) {
        return String.format("""
                SELECT
                	cols.column_name AS "columnName",
                	cols.data_type AS "dataType",
                    cols.character_maximum_length AS "charLength",
                    cols.numeric_precision AS "numericPrecision",
                    cols.numeric_scale AS "numericScale",
                    cols.is_nullable AS "nullable",
                    cols.column_default AS "defaultValue",
                    pg_catalog.col_description(c.oid, cols.ordinal_position::int) AS "columnComment"
                FROM
                    information_schema.columns cols
                    JOIN pg_catalog.pg_class c ON c.relname = cols.table_name
                    JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
                WHERE
                    cols.table_schema = %s
                    AND n.nspname = %s
                    AND cols.table_name = %s
                    %s
                ORDER BY
                    cols.ordinal_position;
                """, SqlUtil.escapeSqlValue(schema), SqlUtil.escapeSqlValue(schema), SqlUtil.escapeSqlValue(tableName), SqlUtil.andInClause("COLUMN_NAME", columnNames));
    }


    @Override
    public String databaseListSql() {
        return "SELECT datname FROM pg_database WHERE datistemplate = false;";
    }

    @Override
    public String schemaListSql() {
        return "SELECT nspname FROM pg_catalog.pg_namespace WHERE nspname NOT LIKE 'pg_%' AND nspname != 'information_schema';";
    }

    @Override
    public String addPage(String sql, Long offset, Long rowCount) {
        return String.format("SELECT * FROM (%S) t LIMIT %S OFFSET %S;", sql, offset * rowCount, (offset - 1) * rowCount);
    }

    @Override
    public String columnNamesSql(String dbName, String schema, String tableName) {
        return String.format("""
                SELECT
                    a.attname AS "columnName"
                FROM
                    pg_class c
                JOIN
                    pg_attribute a ON a.attrelid = c.oid
                WHERE
                    c.relkind = 'r'
                    AND a.attnum > 0
                    AND NOT a.attisdropped
                    AND c.relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = %s)
                    AND c.relname = %s
                ORDER BY c.relname, a.attnum;
                """, SqlUtil.escapeSqlValue(schema), SqlUtil.escapeSqlValue(tableName));
    }
}
