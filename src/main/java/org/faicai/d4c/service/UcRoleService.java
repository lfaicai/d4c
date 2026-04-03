package org.faicai.d4c.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.faicai.d4c.mapper.UcRoleMapper;
import org.faicai.d4c.pojo.dto.SimpleUserDTO;
import org.faicai.d4c.pojo.dto.UcRoleDTO;
import org.faicai.d4c.pojo.dto.UserQueryDTO;
import org.faicai.d4c.pojo.dto.UserRolePageQueryDTO;
import org.faicai.d4c.pojo.entity.D4cUser;
import org.faicai.d4c.pojo.entity.Role;
import org.faicai.d4c.pojo.entity.UcRole;
import org.faicai.d4c.pojo.vo.PageResult;
import org.faicai.d4c.pojo.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UcRoleService extends ServiceImpl<UcRoleMapper, UcRole> implements IService<UcRole> {

    private final RoleService roleService;
    private final D4cUserService d4cUserService;

    /**
     * 根据用户ID查询角色列表
     */
    public List<Role> getRolesByUserId(Long userId) {
        return baseMapper.getRolesByUserId(userId);
    }

    /**
     * 根据角色ID查询用户ID列表
     */
    public List<Long> getUserIdsByRoleId(Long roleId) {
        return baseMapper.getUserIdsByRoleId(roleId);
    }

    /**
     * 根据角色ID查询用户信息列表
     */
    public List<D4cUser> getUsersByRoleId(Long roleId) {
        List<Long> userIds = getUserIdsByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIds)) {
            return List.of();
        }
        return d4cUserService.listByIds(userIds);
    }

    /**
     * 根据角色ID查询简化用户信息列表（只包含ID、账号、用户名）
     */
    public List<SimpleUserDTO> getSimpleUsersByRoleId(Long roleId) {
        List<Long> userIds = getUserIdsByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIds)) {
            return List.of();
        }
        
        List<D4cUser> users = d4cUserService.listByIds(userIds);
        return users.stream()
                .map(user -> new SimpleUserDTO(user.getId(), user.getAccount(), user.getUsername()))
                .toList();
    }

    /**
     * 分页查询角色下的用户信息（支持模糊查询）
     */
    public PageResult<SimpleUserDTO> pageQueryUsersByRoleId(UserRolePageQueryDTO queryDTO) {
        // 先获取该角色下的所有用户ID
        List<Long> userIds = getUserIdsByRoleId(queryDTO.getRoleId());
        if (CollectionUtils.isEmpty(userIds)) {
            return new PageResult<>(queryDTO.getPage(), queryDTO.getPageSize(), List.of(), 0L);
        }

        // 构建用户查询条件
        LambdaQueryWrapper<D4cUser> userQueryWrapper = Wrappers.lambdaQuery();
        userQueryWrapper.in(D4cUser::getId, userIds);

        UserQueryDTO userQueryDTO = new UserQueryDTO();
        userQueryDTO.setUsername(queryDTO.getUsername());
        userQueryDTO.setAccount(queryDTO.getAccount());
        userQueryDTO.setPage(queryDTO.getPage());
        userQueryDTO.setPageSize(queryDTO.getPageSize());
        userQueryDTO.setIds(userIds);

        // 执行分页查询
        PageResult<UserVO> userVOPageResult = d4cUserService.pageQuery(userQueryDTO);

        // 转换为SimpleUserDTO
        List<SimpleUserDTO> simpleUsers = userVOPageResult.getRows().stream()
                .map(user -> new SimpleUserDTO(user.getId(), user.getAccount(), user.getUsername()))
                .toList();
        return new PageResult<>(userVOPageResult.getPage(), userVOPageResult.getPageSize(), simpleUsers, userVOPageResult.getTotalRows());
    }

    /**
     * 检查用户是否拥有指定角色
     */
    public boolean checkUserHasRole(Long userId, Long roleId) {
        return baseMapper.checkUserHasRole(userId, roleId) > 0;
    }

    /**
     * 为用户分配角色
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUser(Long userId, List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return true;
        }
        
        // 先删除用户的所有角色
        LambdaQueryWrapper<UcRole> deleteWrapper = Wrappers.lambdaQuery();
        deleteWrapper.eq(UcRole::getUcId, userId);
        remove(deleteWrapper);
        
        // 添加新角色
        List<UcRole> ucRoles = roleIds.stream()
                .map(roleId -> {
                    UcRole ucRole = new UcRole();
                    ucRole.setUcId(userId);
                    ucRole.setRoleId(roleId);
                    return ucRole;
                })
                .toList();
        
        return saveBatch(ucRoles);
    }

    /**
     * 为角色分配用户
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean assignUsersToRole(Long roleId, List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        }
        List<Long> dbUserIds = getUserIdsByRoleId(roleId);
        userIds.removeAll(dbUserIds);
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        }
        // 添加新用户
        List<UcRole> ucRoles = userIds.stream()
                .map(userId -> {
                    UcRole ucRole = new UcRole();
                    ucRole.setUcId(userId);
                    ucRole.setRoleId(roleId);
                    return ucRole;
                })
                .toList();
        
        return saveBatch(ucRoles);
    }

    /**
     * 移除用户的角色
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeUserRole(Long userId, Long roleId) {
        LambdaQueryWrapper<UcRole> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UcRole::getUcId, userId)
               .eq(UcRole::getRoleId, roleId);
        return remove(wrapper);
    }

    /**
     * 移除用户的所有角色
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAllUserRoles(Long userId) {
        LambdaQueryWrapper<UcRole> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UcRole::getUcId, userId);
        return remove(wrapper);
    }

    /**
     * 分页查询用户角色关联
     */
    public PageResult<UcRole> pageQuery(UcRoleDTO ucRoleDTO) {
        LambdaQueryWrapper<UcRole> queryWrapper = Wrappers.lambdaQuery();
        if (ucRoleDTO.getUcId() != null) {
            queryWrapper.eq(UcRole::getUcId, ucRoleDTO.getUcId());
        }
        if (ucRoleDTO.getRoleId() != null) {
            queryWrapper.eq(UcRole::getRoleId, ucRoleDTO.getRoleId());
        }
        PageDTO<UcRole> pageDTO = baseMapper.selectPage(ucRoleDTO.toPageDTO(), queryWrapper);
        return new PageResult<>(pageDTO.getCurrent(), pageDTO.getPages(), pageDTO.getRecords(), pageDTO.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean removeUsersToRole(Long roleId, List<Long> userIds) {
        LambdaQueryWrapper<UcRole> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UcRole::getRoleId, roleId);
        wrapper.in(UcRole::getUcId, userIds);
        return remove(wrapper);
    }
}
