package org.faicai.d4c.pojo.vo;

import lombok.Data;
import org.faicai.d4c.enums.ResourceType;
import org.faicai.d4c.pojo.entity.Permission;

/**
 * @Describe： 资源节点
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-10-17
 */
@Data
public class ResourceNodeVO {

    private Long id;

    /**
     * 数据库连接id
     */
    private Long connectId;

    /**
     * 数据库连接名称
     */
    private String connectName;

    /**
     * 名称
     */
    private String name;

    /**
     * 父节点名称
     */
    private String parentName;

    /**
     * 类型
     */
    private ResourceType type;

    /**
     * 数据库类型
     */
    private String dbType;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * schema名称
     */
    private String schemaName;

    private boolean selected;

    private ResourceNodeVO[] children;

    public ResourceNodeVO[] getChildren() {
        if (type == ResourceType.COLUMN){
            return children;
        }
        return new ResourceNodeVO[0];
    }
}
