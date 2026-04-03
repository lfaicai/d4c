package org.faicai.d4c.controller.admin;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.constant.CommonConstants;
import org.faicai.d4c.pojo.dto.RoleDTO;
import org.faicai.d4c.pojo.entity.Role;
import org.faicai.d4c.pojo.vo.PageResult;
import org.faicai.d4c.service.RoleService;
import org.faicai.d4c.utils.R;
import org.faicai.d4c.utils.UpdateValidationGroup;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/role")
public class RoleManagementController {


    private final RoleService roleService;


    @PostMapping("/pageQuery")
    public R<PageResult<Role>> pageQuery(@RequestBody RoleDTO roleDTO) {
        return R.ok(roleService.pageQuery(roleDTO));
    }

    @PostMapping("/create")
    public R<Role> create(@RequestBody Role role) {
        if (!role.getRoleCode().startsWith(CommonConstants.ROLE_PREFIX)) {
            role.setRoleCode(CommonConstants.ROLE_PREFIX + role.getRoleCode().toUpperCase());
        }
        if (roleService.save(role)) {
            return R.ok(role);
        }
        return R.failed();
    }

    @DeleteMapping("/{id}")
    private R<Boolean> delete(@PathVariable Long id) {
        return R.ok(roleService.removeById(id));
    }

    @PutMapping("/update")
    private R<Role> update(@Validated(UpdateValidationGroup.class) @RequestBody Role role) {
        if (roleService.updateById(role)) {
            return R.ok(role);
        }
        return R.failed();
    }
}
