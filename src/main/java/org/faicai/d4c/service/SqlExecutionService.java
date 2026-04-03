package org.faicai.d4c.service;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.config.SqlExecutionChain;
import org.faicai.d4c.constant.ResponseCode;
import org.faicai.d4c.core.SqlExecutionContext;
import org.faicai.d4c.exception.BusinessException;
import org.faicai.d4c.pojo.entity.DataBaseConnectConfig;
import org.faicai.d4c.pojo.vo.SelectResult;
import org.faicai.d4c.pojo.vo.TableDetailsVO;
import org.faicai.d4c.utils.SecurityUtils;
import org.faicai.d4c.utils.sql.SqlInfo;
import org.faicai.d4c.utils.sql.SqlResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SqlExecutionService {

    private final SqlExecutionChain executionChain;
    private final ResourceService resourceService;
    private final DataBaseConnectConfigService dbConnectConfigService;

    private final static String SELECT_SQL_TEMPLATE = "SELECT %s FROM %s";
    private final static String SELECT_SQL_WHERE_TEMPLATE = "SELECT %s FROM %s WHERE %S";
    private final static String  ALL_COLUMNS = "*";


    public Object executeSql(SqlInfo sqlInfo) {
        SqlExecutionContext context = new SqlExecutionContext(sqlInfo);
        executionChain.execute(context);
        if (!context.isExecutionSuccess()) {
            throw new RuntimeException("SQL执行失败", context.getException());
        }
        return context.getResult();
    }


    public Object pageByTable(SqlInfo sqlInfo) {
//        List<String> currentUserColumns = resourceService.findCurrentUserColumns(sqlInfo);
//        if (currentUserColumns.isEmpty()) {
//            throw new BusinessException(ResponseCode.NOT_HAVE_TABLE_PERMISSIONS);
//        }
//        String columns = String.join(",", currentUserColumns);

        String selectSql;
        if (StringUtils.hasText(sqlInfo.getSelectWhere())){
            selectSql = String.format(SELECT_SQL_WHERE_TEMPLATE, ALL_COLUMNS, sqlInfo.getTable(), sqlInfo.getSelectWhere());
        }else {
            selectSql = String.format(SELECT_SQL_TEMPLATE, ALL_COLUMNS, sqlInfo.getTable());
        }
        sqlInfo.setSql(selectSql);
        return executeSql(sqlInfo);
    }

}