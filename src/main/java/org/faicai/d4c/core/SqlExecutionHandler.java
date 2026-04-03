package org.faicai.d4c.core;

public interface SqlExecutionHandler {

    void handle(SqlExecutionContext context);

    void setNextHandler(SqlExecutionHandler nextHandler);
}
