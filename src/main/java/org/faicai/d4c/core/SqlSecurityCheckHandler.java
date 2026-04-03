package org.faicai.d4c.core;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import org.faicai.d4c.constant.ResponseCode;
import org.faicai.d4c.enums.DataBaseAction;
import org.faicai.d4c.exception.BusinessException;
import org.faicai.d4c.utils.sql.SqlInfo;
import org.faicai.d4c.utils.sql.SqlParseUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @Describe：SQL 安全策略校验。在权限校验之后、列别名改写与分页包装之前执行，
 * 用于拦截多语句、注释绕过、DDL 以及无 WHERE 的全表更新/删除等风险行为。
 * 开关与细项见配置前缀 {@code d4c.sql-security.*}。
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2026-04-03
 */
@Component
@Order(2)
public class SqlSecurityCheckHandler extends AbstractSqlExecutionHandler {

    @Value("${d4c.sql-security.enabled:true}")
    private boolean enabled;

    @Value("${d4c.sql-security.allow-multi-statement:false}")
    private boolean allowMultiStatement;

    @Value("${d4c.sql-security.allow-comment:false}")
    private boolean allowComment;

    @Value("${d4c.sql-security.allow-ddl:false}")
    private boolean allowDdl;

    @Value("${d4c.sql-security.allow-full-table-mutation:false}")
    private boolean allowFullTableMutation;

    @Override
    public void handle(SqlExecutionContext context) {
        if (!enabled) {
            invokeNext(context);
            return;
        }
        SqlInfo sqlInfo = context.getSqlInfo();
        String sql = normalizeSql(sqlInfo.getSql());
        if (!StringUtils.hasText(sql)) {
            throw new BusinessException(ResponseCode.UNSAFE_SQL_BLOCKED, "empty sql");
        }
        sqlInfo.setSql(sql);

        if (!allowComment && containsComment(sql)) {
            throw new BusinessException(ResponseCode.UNSAFE_SQL_BLOCKED, "sql comments are not allowed");
        }

        List<SQLStatement> stmtList;
        try {
            stmtList = SQLUtils.parseStatements(sql, sqlInfo.getDbType());
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.UNSAFE_SQL_BLOCKED, "sql parse error");
        }

        if (stmtList.isEmpty()) {
            throw new BusinessException(ResponseCode.UNSAFE_SQL_BLOCKED, "empty sql statement");
        }

        if (!allowMultiStatement && stmtList.size() > 1) {
            throw new BusinessException(ResponseCode.UNSAFE_SQL_BLOCKED, "multi statement sql is not allowed");
        }

        SqlParseUtils.SqlDefinition sqlDefinition = SqlParseUtils.sqlParse(sqlInfo);
        if (sqlInfo.getAction() == null && sqlDefinition.getAction() != null) {
            sqlInfo.setAction(sqlDefinition.getAction());
        }

        if (!allowDdl && isDdl(sqlInfo.getAction())) {
            throw new BusinessException(ResponseCode.UNSAFE_SQL_BLOCKED, "ddl statement is not allowed");
        }

        if (!allowFullTableMutation && hasFullTableMutation(stmtList)) {
            throw new BusinessException(ResponseCode.UNSAFE_SQL_BLOCKED, "full table update/delete is not allowed");
        }

        invokeNext(context);
    }

    /**
     * 去掉 SQL 末尾多余分号，避免客户端习惯性输入分号影响后续校验。
     */
    private String normalizeSql(String sql) {
        if (!StringUtils.hasText(sql)) {
            return sql;
        }
        String normalized = sql.trim();
        while (normalized.endsWith(";")) {
            normalized = normalized.substring(0, normalized.length() - 1).trim();
        }
        return normalized;
    }

    private boolean containsComment(String sql) {
        String lowerSql = sql.toLowerCase(Locale.ROOT);
        return lowerSql.contains("--") || lowerSql.contains("/*") || lowerSql.contains("*/") || lowerSql.contains("#");
    }

    private boolean hasFullTableMutation(List<SQLStatement> statements) {
        for (SQLStatement statement : statements) {
            if (statement instanceof SQLUpdateStatement updateStatement && updateStatement.getWhere() == null) {
                return true;
            }
            if (statement instanceof SQLDeleteStatement deleteStatement && deleteStatement.getWhere() == null) {
                return true;
            }
        }
        return false;
    }

    private boolean isDdl(DataBaseAction action) {
        if (action == null) {
            return false;
        }
        Set<DataBaseAction> ddlActions = EnumSet.of(
                DataBaseAction.DROP,
                DataBaseAction.CREATE,
                DataBaseAction.ALTER,
                DataBaseAction.CREATE_INDEX,
                DataBaseAction.DROP_INDEX,
                DataBaseAction.ADD,
                DataBaseAction.ADD_PARTITION,
                DataBaseAction.ANALYZE
        );
        return ddlActions.contains(action);
    }
}
