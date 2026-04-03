package org.faicai.d4c.core;


import lombok.Data;
import org.faicai.d4c.pojo.entity.DataBaseConnectConfig;
import org.faicai.d4c.pojo.vo.PageResult;
import org.faicai.d4c.pojo.vo.SelectResult;
import org.faicai.d4c.utils.sql.SqlInfo;
import org.faicai.d4c.utils.sql.SqlResult;

import java.util.List;
import java.util.Map;

/**
 * SQL执行上下文
 */
@Data
public class SqlExecutionContext {
    private final SqlInfo sqlInfo;
    private long startTime;
    private long endTime;
    private boolean executionSuccess;
    private Exception exception;
    private SqlResult result;
    private DataBaseConnectConfig dataBaseConnectConfig;
    private long totalRows;
    boolean allColumnPermission = true;

    public SqlExecutionContext(SqlInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
        this.startTime = System.currentTimeMillis();
    }

    public void setResult(SqlResult result) {
        if (sqlInfo.isPaging()) {
            if (result.getData() instanceof SelectResult selectResult){
                List<Map<String, Object>> list = selectResult.getRows();
                PageResult<Map<String, Object>> pageResult = new PageResult<>(sqlInfo.getPage(), sqlInfo.getPageSize(), selectResult.getHeads(), list, totalRows, allColumnPermission);
                result.setData(pageResult);
            }
        }
        this.result = result;
    }
}