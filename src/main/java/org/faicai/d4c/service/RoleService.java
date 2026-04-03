package org.faicai.d4c.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.faicai.d4c.mapper.RoleMapper;
import org.faicai.d4c.pojo.dto.RoleDTO;
import org.faicai.d4c.pojo.entity.Role;
import org.faicai.d4c.pojo.vo.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService extends ServiceImpl<RoleMapper, Role> implements IService<Role> {

    private final D4cUserService d4cUserService;


    public List<Role> getRolesByUserId(Long userId){
        return null;
    }


    public PageResult<Role> pageQuery(RoleDTO roleDTO) {
        LambdaQueryWrapper<Role> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(roleDTO.getRoleName())) {
            queryWrapper.like(Role::getRoleName, roleDTO.getRoleName());
        }
        if (StringUtils.hasText(roleDTO.getRoleCode())) {
            queryWrapper.like(Role::getRoleCode, roleDTO.getRoleCode());
        }
        PageDTO<Role> rolePageDTO = baseMapper.selectPage(roleDTO.toPageDTO(), queryWrapper);
        return new PageResult<>(rolePageDTO.getCurrent(), rolePageDTO.getPages(), rolePageDTO.getRecords(), rolePageDTO.getTotal());
    }
}
