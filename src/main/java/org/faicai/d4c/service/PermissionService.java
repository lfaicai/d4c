package org.faicai.d4c.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.faicai.d4c.enums.ResourceType;
import org.faicai.d4c.mapper.PermissionMapper;
import org.faicai.d4c.pojo.entity.Permission;
import org.faicai.d4c.utils.SecurityUtils;
import org.faicai.d4c.utils.sql.SqlInfo;
import org.faicai.d4c.utils.sql.SqlParseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> implements IService<Permission> {

    private final ResourceService resourceService;

    boolean interceptor(SqlInfo sqlInfo){
        SqlParseUtils.SqlDefinition sqlDefinition = SqlParseUtils.sqlParse(sqlInfo);
        List<SqlParseUtils.SqlDetails> sqlDetailsList = sqlDefinition.getSqlDetailsList();
        return true;
    }


    /**
     * 保存或更新权限，并联动处理上下级节点权限
     * 业务规则：
     * 1）当前节点始终保存
     * 2）非 COLUMN 节点 → 同步权限到下级节点
     * 3）非 CONNECTION 节点 → 给上级节点补充查询权限(canSelect=true)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdate(Permission permission) {

        // 最终需要批量保存的权限集合
        List<Permission> result = new ArrayList<>();

        // ===================== 处理下层节点 =====================
        // COLUMN 不向下联动
        if (!isColumn(permission)) {
            result.addAll(buildLowerPermissions(permission));
        }

        // ===================== 处理上层节点 =====================
        // CONNECTION 不向上联动
        if (!isConnection(permission) && !permission.allNo()) {
            result.addAll(buildUpperPermissions(permission));
        }

        Permission p = baseMapper.getByResourceIdAndRoleIdAndUserId(permission.getResourceId(), permission.getRoleId(),  SecurityUtils.getCurrentUserId());
        if (p != null) {
            permission.setId(p.getId());
        }
        result.add(permission);
        // ③ 批量保存或更新
        return saveOrUpdateBatch(result);
    }



    /**
     * 判断是否为 COLUMN 资源
     */
    private boolean isColumn(Permission p) {
        return ResourceType.COLUMN.equals(p.getResourceType());
    }

    /**
     * 判断是否为 CONNECTION 资源
     */
    private boolean isConnection(Permission p) {
        return ResourceType.CONNECTION.equals(p.getResourceType());
    }

    /**
     * ===========================
     * 构建下层节点权限（继承当前权限）
     * ===========================
     * 规则：
     * - 找到当前资源的所有下级资源
     * - 如果下级已有权限 → 以当前权限覆盖（保留原ID）
     * - 如果下级没有权限 → 新建权限
     */
    private List<Permission> buildLowerPermissions(Permission permission) {

        Set<Long> lowerIds =
                resourceService.findLowerLevelNodeIds(
                        permission.getResourceId(),
                        permission.getResourceType()
                );

        // 没有下级，直接返回空
        if (lowerIds == null || lowerIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 查询数据库中已有的权限
        Map<Long, Permission> dbMap =
                loadPermissionMap(lowerIds, permission.getRoleId());

        List<Permission> list = new ArrayList<>();

        for (Long resourceId : lowerIds) {
            // 如果数据库已有，取出来；否则新建
            Permission p = dbMap.getOrDefault(resourceId, new Permission());
            Long oldId = p.getId();
            // 以当前权限为模板复制属性
            BeanUtils.copyProperties(permission, p);
            // 恢复原主键 & 设置目标资源ID
            p.setId(oldId);
            p.setResourceId(resourceId);

            list.add(p);
        }

        return list;
    }

    /**
     * ===========================
     * 构建上层节点权限（补充查询权限）
     * ===========================
     * 规则：
     * - 找到当前资源的所有上级资源
     * - 如果上级已有权限：
     *     - 若 canSelect=true → 什么都不做
     *     - 若 canSelect=false → 改成 true
     * - 如果上级没有权限：
     *     - 新建权限，canSelect=true
     */
    private List<Permission> buildUpperPermissions(Permission permission) {
        Set<Long> upperIds =
                resourceService.findUpperLevelNodeIds(
                        permission.getResourceId(),
                        permission.getResourceType()
                );

        if (upperIds == null || upperIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Permission> dbMap =
                loadPermissionMap(upperIds, permission.getRoleId());

        List<Permission> list = new ArrayList<>();

        for (Long resourceId : upperIds) {
            Permission p = dbMap.get(resourceId);
            if (p != null) {
                // 已有权限 & 已经可查询 → 跳过
                if (p.isCanSelect()) {
                    continue;
                }
                // 已有权限但不可查询 → 改为可查询
                p.setCanSelect(true);
            } else {
                // 没有权限 → 新建
                p = new Permission();
                p.setRoleId(permission.getRoleId());
                p.setResourceId(resourceId);
                p.setCanSelect(true);
            }

            list.add(p);
        }

        return list;
    }

    /**
     * ===========================
     * 统一加载数据库权限为 Map
     * key = resourceId
     * value = Permission
     * ===========================
     */
    private Map<Long, Permission> loadPermissionMap(Set<Long> resourceIds, Long roleId) {

        if (resourceIds == null || resourceIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return listByResourceIdsAndRoleId(resourceIds, roleId)
                .stream()
                .collect(Collectors.toMap(
                        Permission::getResourceId,
                        p -> p
                ));
    }



    public List<Permission> listByResourceIdsAndRoleId(Set<Long> resourceIds, Long roleId) {
        LambdaQueryWrapper<Permission> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(Permission::getResourceId, resourceIds);
        queryWrapper.eq(Permission::getRoleId, roleId);
        return list(queryWrapper);
    }

    public List<Permission> listByRole(Long roleId) {
        LambdaQueryWrapper<Permission> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Permission::getRoleId, roleId);
        return list(queryWrapper);
    }

    public List<Long> findCurrentUserTableByTableIds(Set<Long> tableIds) {
        Long userId = SecurityUtils.getCurrentUserId();
        return baseMapper.findSelectTableIdsByUserIdAndTableIds(userId,tableIds);
    }
}
