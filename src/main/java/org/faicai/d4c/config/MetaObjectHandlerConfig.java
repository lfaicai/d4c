package org.faicai.d4c.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * 公共字段填充
 * @author facai.lan
 */
@Configuration
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    private static final String CREATED_BY = "createdBy";
    private static final String UPDATED_BY = "updatedBy";


    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.setFieldValByName(CREATED_AT, now, metaObject);
        try {
            this.setFieldValByName(CREATED_BY, 1L, metaObject);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.setFieldValByName(UPDATED_AT, now, metaObject);
        try {
            this.setFieldValByName(UPDATED_BY, 1L, metaObject);
        } catch (Exception ignored) {
        }
    }
}