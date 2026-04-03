package org.faicai.d4c.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;
import org.faicai.d4c.utils.CreateValidationGroup;


@Data
public class UserCreateDTO {

    private Long id;

    private String provider;

    private String externalId;

    @NotBlank(message = "{user.name.notblank}")
    @Size(min = 2, max = 20, message = "{user.name.size}")
    private String username;

    private String account;

    @NotBlank(groups = CreateValidationGroup.class, message = "{user.password.notblank}")
    @Size(min = 6, max = 20, message = "{user.password.size}")
    private String password;

    private String email;

    private Integer status;

    private String iconUrl;
}
