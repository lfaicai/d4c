package org.faicai.d4c.core;

import org.faicai.d4c.enums.DataBaseAction;
import org.faicai.d4c.utils.sql.SqlInfo;
import org.faicai.d4c.utils.sql.SqlParseUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 处理分页包装成派生表时的重复列名问题。
 *
 * <p>例如：SELECT c.created_at, r.created_at ... 在被包成 SELECT * FROM (<sql>) t 时，
 * MySQL/PG 等会要求派生表列名唯一，否则抛 Duplicate column name。</p>
 */
@Component
@Order(4)
public class DuplicateColumnAliasHandler extends AbstractSqlExecutionHandler {

    @Override
    public void handle(SqlExecutionContext context) {
        SqlInfo sqlInfo = context.getSqlInfo();
        if (DataBaseAction.SELECT.equals(sqlInfo.getAction())) {
            String rewritten = SqlParseUtils.rewriteDuplicateSelectColumnLabels(sqlInfo);
            if (StringUtils.hasText(rewritten) && !rewritten.equals(sqlInfo.getSql())) {
                sqlInfo.setSql(rewritten);
            }
        }
        invokeNext(context);
    }
}

