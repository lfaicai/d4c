package org.faicai.d4c.core;

import org.faicai.d4c.constant.ResponseCode;
import org.faicai.d4c.exception.BusinessException;
import org.faicai.d4c.pojo.entity.DataBaseConnectConfig;
import org.faicai.d4c.service.DataBaseConnectConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Describe：连接校验
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-09-04
 */
@Component
@Order(1)
public class ConnectConfigCheckHandler extends AbstractSqlExecutionHandler {

    @Autowired
    private DataBaseConnectConfigService dataBaseConnectConfigService;


    @Override
    public void handle(SqlExecutionContext context) {
        DataBaseConnectConfig config = dataBaseConnectConfigService.getById(context.getSqlInfo().getConnectId());
        if (config == null) {
            throw new BusinessException(ResponseCode.CONNECT_NOT_EXIST);
        }
        context.setDataBaseConnectConfig(config);
        context.getSqlInfo().setDbType(config.getDbType().name());
        invokeNext(context);
    }


}
