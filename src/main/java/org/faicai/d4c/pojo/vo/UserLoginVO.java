package org.faicai.d4c.pojo.vo;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginVO {

    private TokenPair token;

    private Long userId;

    private String email;

    private String iconUrl;

}
