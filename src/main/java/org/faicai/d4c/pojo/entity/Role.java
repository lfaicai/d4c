package org.faicai.d4c.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Describe：role
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-08-05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("role")
public class Role extends BaseEntity{

    @TableId(type = IdType.AUTO)
    private Long id;

    private String roleName;

    private String roleCode;

    private String description;


}
