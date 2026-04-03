package org.faicai.d4c.pojo.entity;

import lombok.Data;
import org.faicai.d4c.enums.ResourceType;
import org.springframework.util.StringUtils;

/**
 * @Describe：权限+资源列表
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-09-04
 */
@Data
public class UserResource {

    private ResourceType resourceType;

    private Integer idx;

    private String databaseName;

    private String schemaName;

    private String tableName;

    private String columnName;

    private String description;

    private boolean canSelect;
    private boolean canUpdate;
    private boolean canDelete;
    private boolean canInsert;
    private boolean canDrop;
    private boolean canMerge;
    private boolean canCreate;
    private boolean canAlter;
    private boolean canCreateIndex;
    private boolean canDropIndex;
    private boolean canReferenced;
    private boolean canAdd;
    private boolean canAddPartition;
    private boolean canAnalyze;


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
