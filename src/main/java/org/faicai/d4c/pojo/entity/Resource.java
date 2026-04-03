package org.faicai.d4c.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.faicai.d4c.enums.ResourceType;
import org.springframework.util.StringUtils;

/**
 * @Describe： db资源
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-08-11- 17:02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("resource")
public class Resource extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long databaseConnectId;

    private ResourceType resourceType;

    private Integer idx;

    private String databaseName;

    private String schemaName;

    private String tableName;

    private String columnName;

    private String description;

    private String descriptionAi;

    private String dataType;


    public String getTableNameKey(){
        if (StringUtils.hasText(schemaName)) {
            return String.join(".", databaseName, schemaName, tableName);
        }
        return String.join(".", databaseName, tableName);
    }

    public String getKey(){
        return switch (resourceType) {
            case CONNECTION -> "";
            case DATABASE -> databaseName;
            case SCHEMA -> String.join(".", databaseName, schemaName);
            case TABLE -> {
                if (StringUtils.hasText(schemaName)) {
                    yield String.join(".", databaseName, schemaName, tableName);
                }
                yield String.join(".", databaseName, tableName);
            }
            case COLUMN -> {
                if (StringUtils.hasText(schemaName)) {
                    yield String.join(".", databaseName, schemaName, tableName, columnName);
                }
                yield String.join(".", databaseName, tableName, columnName);
            }
        };
    }
}
