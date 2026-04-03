package org.faicai.d4c.pojo.vo;

import lombok.Data;

/**
 * @Describe： 表详情
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-12-22
 */
@Data
public class TableDetailsVO {

    private Integer idx;
    private String columnName;
    private Integer dataType;
    private Integer charLength;
    private Integer numericPrecision;
    private Integer numericScale;
    private String nullable;
    private String defaultValue;
    private String columnComment;

}
