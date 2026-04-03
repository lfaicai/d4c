package org.faicai.d4c.utils.sql;


import com.alibaba.druid.DbType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import org.faicai.d4c.enums.DataBaseAction;
import org.faicai.d4c.enums.DbDialect;

import java.util.Locale;


@Data
@ToString
public class SqlInfo {

    @NotBlank(message = "{sql.cinnectid.notblank}")
    private Long connectId;

    @NotBlank(message = "{sql.sql.notblank}")
    private String sql;

    @NotBlank(message = "{sql.dbtype.notblank}")
    private String dbType;

    @NotBlank(message = "{sql.db.notblank}")
    private String db;

    private String schema = "public";

    private String table;

    private String selectWhere;

    private DataBaseAction action;

    private long page = 1L;

    private long pageSize = 200L;

    private boolean paging = true;

    public SqlInfo() {
    }

    public SqlInfo(String db, String dbType, String schema, String sql) {
        this.db = db;
        this.dbType = dbType;
        this.schema = schema;
        this.sql = sql;
    }
    public SqlInfo(Long connectId, String db, String dbType, String schema, String table, String sql) {
        this.connectId = connectId;
        this.db = db;
        this.table = table;
        this.dbType = dbType;
        this.schema = schema;
        this.sql = sql;
    }


    public SqlInfo(DataBaseAction action, Long connectId, String db, String dbType, String schema, String sql) {
        this.action = action;
        this.connectId = connectId;
        this.db = db;
        this.dbType = dbType;
        this.schema = schema;
        this.sql = sql;
    }

    public DbType getDbType() {
        return DbType.of(dbType.toLowerCase(Locale.ROOT));
    }

    public DbDialect getDbDialect() {
        return DbDialect.fromDruidName(dbType);
    }

    public SqlInfo deepClone(String sql) {
        return new SqlInfo(db, dbType, schema, sql);
    }

}
