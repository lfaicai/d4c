package org.faicai.d4c.pojo.dto;

import lombok.Data;

/**
 * @Describe：简化的用户信息DTO，只包含ID、账号、用户名
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-01-27
 */
@Data
public class SimpleUserDTO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 账号
     */
    private String account;
    
    /**
     * 用户名
     */
    private String username;
    
    public SimpleUserDTO() {}
    
    public SimpleUserDTO(Long id, String account, String username) {
        this.id = id;
        this.account = account;
        this.username = username;
    }
}
