package org.faicai.d4c.pojo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Describe：用户角色分页查询参数
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserRolePageQueryDTO extends PageQueryBase<SimpleUserDTO> {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 用户账号（模糊查询）
     */
    private String account;

    /**
     * 用户名（模糊查询）
     */
    private String username;
}
