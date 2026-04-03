package org.faicai.d4c.core.db;


import org.faicai.d4c.utils.SqlUtil;

import java.util.HashSet;
import java.util.Set;

public class OracleBaseSql extends DbSql {

    @Override
    public String allTableInfo(String dbName, String schema) {
        return """
                SELECT
                    atc.COLUMN_ID as "idx",
                    ( SELECT global_name FROM global_name ) AS "databaseName",
                    atc.OWNER AS "schemaName",
                    atc.TABLE_NAME AS "tableName",
                    atc.COLUMN_NAME AS "columnName",
                    acc.COMMENTS AS "description",
                    atc.data_type AS "dataType"
                FROM
                    ALL_TAB_COLUMNS atc
                LEFT JOIN
                    ALL_COL_COMMENTS acc
                    ON atc.OWNER = acc.OWNER
                    AND atc.TABLE_NAME = acc.TABLE_NAME
                    AND atc.COLUMN_NAME = acc.COLUMN_NAME
                WHERE
                    atc.OWNER NOT IN (
                        'SYS', 'SYSTEM', 'MDSYS', 'CTXSYS', 'XDB',
                        'ORDDATA', 'OLAPSYS', 'EXFSYS', 'WMSYS', 'APPQOSSYS'
                    )
                    AND atc.OWNER NOT LIKE 'APEX%'
                    AND atc.OWNER NOT LIKE 'FLOWS%'
                ORDER BY
                    atc.OWNER, atc.TABLE_NAME, atc.COLUMN_ID
                """;
    }

    @Override
    public String tableDetailsSql(String schema, String tableName, Set<String> columnNames) {
        return String.format("""
               SELECT
                   utc.column_name AS "columnName",
                   utc.data_type AS "dataType",
                   utc.data_length AS "charLength",
                   utc.data_precision AS "numericPrecision",
                   utc.data_scale AS "numericScale",
                   CASE
                       WHEN utc.nullable = 'N' THEN 'NOT NULL'
                       ELSE 'NULL'
                   END AS "nullable",
                   utc.data_default AS "defaultValue",
                   ucc.COMMENTS AS "columnComment"
               FROM
                   user_tab_columns utc
                   LEFT JOIN user_col_comments ucc
                       ON utc.table_name = ucc.table_name
                       AND utc.column_name = ucc.column_name
               WHERE
                   utc.table_name = UPPER(%s) %s
               ORDER BY
                   utc.column_id
               """, SqlUtil.escapeSqlValue(tableName), SqlUtil.andInClause("utc.column_name", columnNames));
    }

    @Override
    public String databaseListSql() {
        return "";
    }

    @Override
    public String schemaListSql() {
        return "SELECT DISTINCT owner FROM ALL_OBJECTS";
    }

    @Override
    public String addPage(String sql, Long offset, Long rowCount) {
        return String.format("""
                    SELECT * FROM (
                        SELECT t.*, ROWNUM AS rn FROM (%s) t WHERE ROWNUM <= %s
                    ) WHERE rn  > %s
                """, sql, offset * rowCount, (offset - 1) * rowCount);

    }

    @Override
    public String columnNamesSql(String dbName, String schema, String tableName) {
        return String.format("""
                SELECT
                    utc.COLUMN_NAME AS "columnName"
                FROM
                    USER_TAB_COLUMNS utc
                LEFT JOIN
                    USER_COL_COMMENTS ucc
                    ON
                    utc.TABLE_NAME = ucc.TABLE_NAME
                    AND utc.COLUMN_NAME = ucc.COLUMN_NAME
                WHERE
                    utc.TABLE_NAME = %s
                ORDER BY
                    utc.TABLE_NAME,utc.COLUMN_ID
                """, SqlUtil.escapeSqlValue(tableName));
    }
}
