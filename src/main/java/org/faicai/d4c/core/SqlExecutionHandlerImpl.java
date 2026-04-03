package org.faicai.d4c.core;

import lombok.extern.slf4j.Slf4j;
import org.faicai.d4c.utils.sql.SqlResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(6)
@Slf4j
public class SqlExecutionHandlerImpl extends AbstractSqlExecutionHandler {


    @Autowired
    private DataSourceManager dataSourceManager;
    
    @Override
    public void handle(SqlExecutionContext context) {
        try {
            log.info("Executing SQL: {}", context.getSqlInfo().getSql());
            // 实际执行SQL
            Object o = dataSourceManager.execute(context.getSqlInfo());
            context.setResult(new SqlResult(context.getSqlInfo().getAction(), o));
            context.setExecutionSuccess(true);
        } catch (Exception e) {
            context.setExecutionSuccess(false);
            context.setException(e);
        } finally {
            context.setEndTime(System.currentTimeMillis());
        }
        invokeNext(context);
    }
}