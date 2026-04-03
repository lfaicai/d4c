package org.faicai.d4c.core.db;


import org.faicai.d4c.enums.DbDialect;

public class DbSqlFactory {


    public static DbSql getDbSql(DbDialect dbDialect){
        return switch (dbDialect) {
            case MYSQL -> new MysqlBaseSql();
            case SQLSERVER -> new SqlServerBaseSql();
            case POSTGRESQL -> new PgBaseSql();
            case ORACLE -> new OracleBaseSql();
            default -> null;
        };
    }
}
