package org.faicai.d4c.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.faicai.d4c.enums.ResourceType;

/**
 * @Describe：sql权限
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-08-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("permission")
public class Permission extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roleId;
    private Long resourceId;
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

    @TableField(exist = false)
    private ResourceType resourceType;


    public boolean allNo() {
        return !canSelect &&
                !canUpdate &&
                !canDelete &&
                !canInsert &&
                !canDrop &&
                !canMerge &&
                !canCreate &&
                !canAlter &&
                !canCreateIndex &&
                !canDropIndex &&
                !canReferenced &&
                !canAdd &&
                !canAddPartition &&
                !canAnalyze;
    }

}
