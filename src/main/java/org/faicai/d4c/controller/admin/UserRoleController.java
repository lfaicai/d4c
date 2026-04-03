package org.faicai.d4c.controller.admin;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.pojo.dto.SimpleUserDTO;
import org.faicai.d4c.pojo.dto.UcRoleDTO;
import org.faicai.d4c.pojo.dto.UserRolePageQueryDTO;
import org.faicai.d4c.pojo.entity.D4cUser;
import org.faicai.d4c.pojo.entity.Role;
import org.faicai.d4c.pojo.entity.UcRole;
import org.faicai.d4c.pojo.vo.PageResult;
import org.faicai.d4c.service.UcRoleService;
import org.faicai.d4c.utils.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Describe：用户角色
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-10-15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/ur")
public class UserRoleController {

    private final UcRoleService ucRoleService;

    /**
     * 分页查询用户角色关联
     */
    @PostMapping("/pageQuery")
    public R<PageResult<UcRole>> pageQuery(@RequestBody UcRoleDTO ucRoleDTO) {
        return R.ok(ucRoleService.pageQuery(ucRoleDTO));
    }

    /**
     * 根据用户ID查询角色列表
     */
    @GetMapping("/roles/{userId}")
    public R<List<Role>> getRolesByUserId(@PathVariable Long userId) {
        return R.ok(ucRoleService.getRolesByUserId(userId));
    }

    /**
     * 根据角色ID查询用户ID列表
     */
    @GetMapping("/users/{roleId}")
    public R<List<Long>> getUserIdsByRoleId(@PathVariable Long roleId) {
        return R.ok(ucRoleService.getUserIdsByRoleId(roleId));
    }

    /**
     * 根据角色ID查询用户信息列表（完整信息）
     */
    @GetMapping("/user-details/{roleId}")
    public R<List<D4cUser>> getUsersByRoleId(@PathVariable Long roleId) {
        return R.ok(ucRoleService.getUsersByRoleId(roleId));
    }

    /**
     * 根据角色ID查询简化用户信息列表（只包含ID、账号、用户名）
     */
    @GetMapping("/simple-users/{roleId}")
    public R<List<SimpleUserDTO>> getSimpleUsersByRoleId(@PathVariable Long roleId) {
        return R.ok(ucRoleService.getSimpleUsersByRoleId(roleId));
    }

    /**
     * 分页查询角色下的用户信息（支持模糊查询）
     */
    @PostMapping("/page-query-users")
    public R<PageResult<SimpleUserDTO>> pageQueryUsersByRoleId(@RequestBody UserRolePageQueryDTO queryDTO) {
        return R.ok(ucRoleService.pageQueryUsersByRoleId(queryDTO));
    }

    /**
     * 检查用户是否拥有指定角色
     */
    @GetMapping("/check/{userId}/{roleId}")
    public R<Boolean> checkUserHasRole(@PathVariable Long userId, @PathVariable Long roleId) {
        return R.ok(ucRoleService.checkUserHasRole(userId, roleId));
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assign-roles/{userId}")
    public R<Boolean> assignRolesToUser(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        return R.ok(ucRoleService.assignRolesToUser(userId, roleIds));
    }

    /**
     * 为角色分配用户
     */
    @PostMapping("/assign-users/{roleId}")
    public R<Boolean> assignUsersToRole(@PathVariable Long roleId, @RequestBody List<Long> userIds) {
        return R.ok(ucRoleService.assignUsersToRole(roleId, userIds));
    }

    /**
     * 移除角色用户
     */
    @DeleteMapping("/remove-users/{roleId}")
    public R<Boolean> removeUsersToRole(@PathVariable Long roleId, @RequestBody List<Long> userIds) {
        return R.ok(ucRoleService.removeUsersToRole(roleId, userIds));
    }

    /**
     * 移除用户的角色
     */
    @DeleteMapping("/remove/{userId}/{roleId}")
    public R<Boolean> removeUserRole(@PathVariable Long userId, @PathVariable Long roleId) {
        return R.ok(ucRoleService.removeUserRole(userId, roleId));
    }

    /**
     * 移除用户的所有角色
     */
    @DeleteMapping("/remove-all/{userId}")
    public R<Boolean> removeAllUserRoles(@PathVariable Long userId) {
        return R.ok(ucRoleService.removeAllUserRoles(userId));
    }

    /**
     * 创建用户角色关联
     */
    @PostMapping("/create")
    public R<UcRole> create(@RequestBody UcRole ucRole) {
        if (ucRoleService.save(ucRole)) {
            return R.ok(ucRole);
        }
        return R.failed();
    }

    /**
     * 删除用户角色关联
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(ucRoleService.removeById(id));
    }

}
