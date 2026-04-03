package org.faicai.d4c.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @Describe：user
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-08-05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("d4c_user")
public class D4cUser extends BaseEntity implements UserDetails {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String provider;

    private String externalId;

    private String username;

    private String account;

    private String password;

    private String email;

    private Integer status;

    private String iconUrl;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }



}
