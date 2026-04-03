package org.faicai.d4c.pojo.vo;


import lombok.Data;

@Data
public class UserVO {

    private Long id;

    private String provider;

    private String externalId;

    private String username;

    private String account;

    private String password;

    private String email;

    private Integer status;

    private String iconUrl;
}
