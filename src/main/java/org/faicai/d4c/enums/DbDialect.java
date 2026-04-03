package org.faicai.d4c.enums;


import lombok.Getter;

@Getter
public enum DbDialect {

    MYSQL(true, false, "com.mysql.cj.jdbc.Driver", "jdbc:mysql://{host}:{port}/{database}?useSSL=false&serverTimezone=UTC"),
    MARIADB(true, false, "org.mariadb.jdbc.Driver", "jdbc:mariadb://{host}:{port}/{database}"),
    TIDB(true, false, "com.mysql.cj.jdbc.Driver", "jdbc:mysql://{host}:{port}/{database}?useSSL=false&serverTimezone=UTC"),
    POLARDB(true, false, "com.mysql.cj.jdbc.Driver", "jdbc:mysql://{host}:{port}/{database}?useSSL=false&serverTimezone=UTC"),
    POSTGRESQL(true, true, "org.postgresql.Driver", "jdbc:postgresql://{host}:{port}/{database}"),
    SQLSERVER(true, true, "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://{host}:{port};databaseName={database};encrypt=true;trustServerCertificate=true;"),
    ORACLE(true, true, "oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@{host}:{port}:{database}"),
    GREENPLUM(true, true, "org.postgresql.Driver", "jdbc:postgresql://{host}:{port}/{database}"),
    REDSHIFT(true, true, "com.amazon.redshift.jdbc.Driver", "jdbc:redshift://{host}:{port}/{database}"),
    SNOWFLAKE(true, true, "net.snowflake.client.jdbc.SnowflakeDriver", "jdbc:snowflake://{account}.snowflakecomputing.com/?db={database}&schema={schema}"),
//    SQLITE(false, false, false, "org.sqlite.JDBC", "jdbc:sqlite:{filepath}"),
    CLICKHOUSE(true, false, "com.clickhouse.jdbc.ClickHouseDriver", "jdbc:clickhouse://{host}:{port}/{database}"),
    HIVE(true, false, "org.apache.hive.jdbc.HiveDriver", "jdbc:hive2://{host}:{port}/{database}"),
    PRESTO(true, true, "com.facebook.presto.jdbc.PrestoDriver", "jdbc:presto://{host}:{port}/{catalog}/{schema}"),
    TRINO(true, true, "io.trino.jdbc.TrinoDriver", "jdbc:trino://{host}:{port}/{catalog}/{schema}"),
    DEFAULT(true, true, "org.h2.Driver", "jdbc:h2:mem:{database};DB_CLOSE_DELAY=-1");


    private final boolean hasDatabase;
    private final boolean hasSchema;

    private final String driverClassName;
    private final String urlTemplate;

    DbDialect(boolean hasDatabase, boolean hasSchema, String driverClassName, String urlTemplate) {
        this.hasDatabase = hasDatabase;
        this.hasSchema = hasSchema;
        this.driverClassName = driverClassName;
        this.urlTemplate = urlTemplate;
    }

    public boolean hasDatabase() { return hasDatabase; }
    public boolean hasSchema() { return hasSchema; }

    /**
     * 根据 Druid 数据库类型名称获取枚举，找不到则返回 DEFAULT
     */
    public static DbDialect fromDruidName(String druidDbType) {
        if (druidDbType == null || druidDbType.isEmpty()) {
            return DEFAULT;
        }
        try {
            return DbDialect.valueOf(druidDbType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEFAULT;
        }
    }

    /**
     * 生成 JDBC URL，替换模板中的占位符
     */
    public String formatJdbcUrl(String host, int port, String database, String schema) {
        return urlTemplate
                .replace("{host}", host)
                .replace("{port}", String.valueOf(port))
                .replace("{database}", database == null ? "" : database)
                .replace("{schema}", schema == null ? "" : schema);
    }
}
