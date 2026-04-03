package org.faicai.d4c.core;

public abstract class AbstractSqlExecutionHandler implements SqlExecutionHandler {
    private SqlExecutionHandler nextHandler;

    @Override
    public void setNextHandler(SqlExecutionHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    protected void invokeNext(SqlExecutionContext context) {
        if (nextHandler != null) {
            nextHandler.handle(context);
        }
    }
}