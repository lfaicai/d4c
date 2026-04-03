package org.faicai.d4c.pojo.vo;

import lombok.Data;
import org.faicai.d4c.enums.ResourceType;

/**
 * @Describe：权限资源节点
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2026-01-13
 */
@Data
public class PermissionResourceNodeVO {

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
    private boolean allow;

    private ResourceNodeVO[] children;

    public ResourceNodeVO[] getChildren() {
        if (type == ResourceType.COLUMN){
            return children;
        }
        return new ResourceNodeVO[0];
    }
}
