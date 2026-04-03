package org.faicai.d4c.pojo.vo;

import lombok.Data;

@Data
public class ColumnVO {
    private Long id;

    private String name;

    private String tableName;

    private String type;

    private String description;
}
