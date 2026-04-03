package org.faicai.d4c.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @Describe：管理员修改用户密码DTO
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-01-27
 */
@Data
public class UserPasswordUpdateDTO {

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String newPassword;
}

