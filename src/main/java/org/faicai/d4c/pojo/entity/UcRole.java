package org.faicai.d4c.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Describe：roles
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-08-05
 */
@Data
@TableName("uc_role")
public class UcRole {

    private Long ucId;

    private Long roleId;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT, value = "created_by")
    private Long createdBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, value = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.UPDATE, value = "updated_by")
    private Long updatedBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE, value = "updated_at")
    private LocalDateTime updatedAt;

}
