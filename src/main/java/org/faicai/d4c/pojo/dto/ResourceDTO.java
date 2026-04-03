package org.faicai.d4c.pojo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.faicai.d4c.enums.ResourceType;
import org.faicai.d4c.pojo.entity.Resource;

/**
 * @Describe：资源分页查询参数
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-01-15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResourceDTO extends PageQueryBase<Resource> {

    /**
     * 数据库连接ID
     */
    private Long databaseConnectId;

    /**
     * 资源类型
     */
    private ResourceType resourceType;

    /**
     * 数据库名称（模糊查询）
     */
    private String databaseName;

    /**
     * 模式名称（模糊查询）
     */
    private String schemaName;

    /**
     * 表名称（模糊查询）
     */
    private String tableName;

    /**
     * 列名称（模糊查询）
     */
    private String columnName;

    /**
     * 描述（模糊查询）
     */
    private String description;
}
