package org.faicai.d4c.controller.admin;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.pojo.entity.Permission;
import org.faicai.d4c.service.PermissionService;
import org.faicai.d4c.utils.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/permission")
public class PermissionController {

    private final PermissionService permissionService;


    /**
     * 分配资源
     */
    @PostMapping("/saveOrUpdate")
    public R<Boolean> saveOrUpdate(@RequestBody Permission permission) {
        boolean b = permissionService.saveOrUpdate(permission);
        return R.ok();
    }



    /**
     * 根据角色获取资源
     */
    @GetMapping("/role/{roleId}")
    public R<List<Permission>> listByRole(@PathVariable Long roleId) {
        return R.ok(permissionService.listByRole(roleId));
    }

}
