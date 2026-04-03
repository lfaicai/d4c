package org.faicai.d4c.pojo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.faicai.d4c.pojo.entity.UcRole;

@EqualsAndHashCode(callSuper = true)
@Data
public class UcRoleDTO extends PageQueryBase<UcRole> {

    private Long ucId;

    private Long roleId;

}
