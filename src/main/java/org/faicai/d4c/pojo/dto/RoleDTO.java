package org.faicai.d4c.pojo.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.faicai.d4c.pojo.entity.Role;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleDTO extends PageQueryBase<Role> {

    private String roleName;

    private String roleCode;

}
