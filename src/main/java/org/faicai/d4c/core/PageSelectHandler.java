package org.faicai.d4c.core;

import lombok.extern.slf4j.Slf4j;
import org.faicai.d4c.core.db.DbSql;
import org.faicai.d4c.core.db.DbSqlFactory;
import org.faicai.d4c.enums.DataBaseAction;
import org.faicai.d4c.utils.sql.SqlInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * @Describe：分页
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-09-13
 */
@Component
@Order(5)
@Slf4j
public class PageSelectHandler extends AbstractSqlExecutionHandler {

    @Autowired
    private DataSourceManager dataSourceManager;

    @Override
    public void handle(SqlExecutionContext context) {
        SqlInfo sqlInfo = context.getSqlInfo();
        if (DataBaseAction.SELECT.equals(sqlInfo.getAction())) {
            addPage(context);
        }
        invokeNext(context);
    }


    public void addPage(SqlExecutionContext context){
        SqlInfo sqlInfo = context.getSqlInfo();
        try {
            // 总条数
//            Long totalRows = dataSourceManager.count(sqlInfo);
//            context.setTotalRows(totalRows);
            DbSql dbSql = DbSqlFactory.getDbSql(sqlInfo.getDbDialect());
            assert dbSql != null;
            String pageSql = dbSql.addPage(sqlInfo.getSql(), sqlInfo.getPage(), sqlInfo.getPageSize());
            sqlInfo.setSql(pageSql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
