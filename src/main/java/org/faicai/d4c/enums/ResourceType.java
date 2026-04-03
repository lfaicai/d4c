package org.faicai.d4c.enums;

import lombok.Getter;

@Getter
public enum ResourceType {
    /**
     * 连接
     */
    CONNECTION,

    /**
     * 数据库
     */
    DATABASE,

    /**
     * schema
     */
    SCHEMA,

    /**
     * 数据表
     */
    TABLE,

    /**
     * 列
     */
    COLUMN;
}
