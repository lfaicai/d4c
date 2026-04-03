package org.faicai.d4c.core.db;

import org.faicai.d4c.utils.SqlUtil;

import java.util.Set;

/**
 * @Describe：sql server 的元数据 SQL
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2026-03-26
 */
public class SqlServerBaseSql extends DbSql {

    @Override
    public String allTableInfo(String dbName, String schema) {
        // 说明：连接本身已切换到目标数据库，因此不强依赖 dbName
        // 同时尽量排除系统 schema，仅返回 user 表/视图的列信息
        return """
                SELECT
                    c.ORDINAL_POSITION as [idx],
                    c.TABLE_CATALOG as [databaseName],
                    c.TABLE_SCHEMA as [schemaName],
                    c.TABLE_NAME as [tableName],
                    c.COLUMN_NAME as [columnName],
                    CAST(ep.value AS nvarchar(4000)) as [description],
                    c.DATA_TYPE as [dataType]
                FROM
                    INFORMATION_SCHEMA.COLUMNS c
                LEFT JOIN sys.columns sc
                    ON sc.object_id = OBJECT_ID(QUOTENAME(c.TABLE_SCHEMA) + '.' + QUOTENAME(c.TABLE_NAME))
                    AND sc.name = c.COLUMN_NAME
                LEFT JOIN sys.extended_properties ep
                    ON ep.major_id = sc.object_id
                    AND ep.minor_id = sc.column_id
                    AND ep.name = 'MS_Description'
                WHERE
                    c.TABLE_SCHEMA NOT IN ('INFORMATION_SCHEMA', 'sys')
                ORDER BY
                    c.TABLE_SCHEMA, c.TABLE_NAME, c.ORDINAL_POSITION;
                """;
    }

    @Override
    public String tableDetailsSql(String schema, String tableName, Set<String> columnNames) {
        return String.format("""
                SELECT
                    c.COLUMN_NAME as [columnName],
                    c.DATA_TYPE as [dataType],
                    c.CHARACTER_MAXIMUM_LENGTH as [charLength],
                    c.NUMERIC_PRECISION as [numericPrecision],
                    c.NUMERIC_SCALE as [numericScale],
                    c.IS_NULLABLE as [nullable],
                    c.COLUMN_DEFAULT as [defaultValue],
                    CAST(ep.value AS nvarchar(4000)) as [columnComment]
                FROM
                    INFORMATION_SCHEMA.COLUMNS c
                LEFT JOIN sys.columns sc
                    ON sc.object_id = OBJECT_ID(QUOTENAME(c.TABLE_SCHEMA) + '.' + QUOTENAME(c.TABLE_NAME))
                    AND sc.name = c.COLUMN_NAME
                LEFT JOIN sys.extended_properties ep
                    ON ep.major_id = sc.object_id
                    AND ep.minor_id = sc.column_id
                    AND ep.name = 'MS_Description'
                WHERE
                    c.TABLE_SCHEMA = %s
                    AND c.TABLE_NAME = %s
                    %s
                ORDER BY
                    c.ORDINAL_POSITION;
                """, SqlUtil.escapeSqlValue(schema), SqlUtil.escapeSqlValue(tableName),
                SqlUtil.andInClause("c.COLUMN_NAME", columnNames));
    }

    @Override
    public String databaseListSql() {
        return """
                SELECT
                    name as [databaseName]
                FROM
                    sys.databases
                WHERE
                    name NOT IN ('master', 'tempdb', 'model', 'msdb')
                ORDER BY
                    name;
                """;
    }

    @Override
    public String schemaListSql() {
        return """
                SELECT
                    name as [schemaName]
                FROM
                    sys.schemas
                WHERE
                    is_ms_shipped = 0
                    AND name <> 'INFORMATION_SCHEMA'
                ORDER BY
                    name;
                """;
    }

    @Override
    public String addPage(String sql, Long offset, Long rowCount) {
        // 使用 ROW_NUMBER() 进行分页（不依赖原 SQL 的 ORDER BY）
        return String.format("""
                SELECT * FROM (
                    SELECT
                        t.*,
                        ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS rn
                    FROM (%s) t
                ) x
                WHERE
                    x.rn > %s
                    AND x.rn <= %s;
                """, sql, (offset - 1) * rowCount, offset * rowCount);
    }

    @Override
    public String columnNamesSql(String dbName, String schema, String tableName) {
        return String.format("""
                SELECT
                    c.COLUMN_NAME as [columnName]
                FROM
                    INFORMATION_SCHEMA.COLUMNS c
                WHERE
                    c.TABLE_SCHEMA = %s
                    AND c.TABLE_NAME = %s
                ORDER BY
                    c.ORDINAL_POSITION;
                """, SqlUtil.escapeSqlValue(schema), SqlUtil.escapeSqlValue(tableName));
    }
}

