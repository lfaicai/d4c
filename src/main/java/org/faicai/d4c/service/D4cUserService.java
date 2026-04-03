package org.faicai.d4c.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.faicai.d4c.constant.ResponseCode;
import org.faicai.d4c.exception.BusinessException;
import org.faicai.d4c.mapper.D4cUserMapper;
import org.faicai.d4c.pojo.dto.UserCreateDTO;
import org.faicai.d4c.pojo.dto.UserPasswordUpdateDTO;
import org.faicai.d4c.pojo.dto.UserQueryDTO;
import org.faicai.d4c.pojo.entity.D4cUser;
import org.faicai.d4c.pojo.vo.PageResult;
import org.faicai.d4c.pojo.vo.UserVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class D4cUserService extends ServiceImpl<D4cUserMapper, D4cUser> implements IService<D4cUser>{


    @Lazy
    private final PasswordEncoder passwordEncoder;


    public D4cUser getByAccount(String account) {
        return getOne(Wrappers.<D4cUser>lambdaQuery().eq(D4cUser::getAccount, account), false);
    }

    public boolean create(UserCreateDTO userCreateDTO) {
        D4cUser user = getByAccount(userCreateDTO.getAccount());
        if (user != null) {
            throw new BusinessException(ResponseCode.USER_ALREADY_REGISTERED);
        }
        // 生成密码 加密
        String encodePassword = passwordEncoder.encode(userCreateDTO.getPassword());
        user = new D4cUser();
        user.setUsername(userCreateDTO.getUsername());
        user.setAccount(userCreateDTO.getAccount());
        user.setPassword(encodePassword);
        user.setEmail(userCreateDTO.getEmail());
        user.setIconUrl(userCreateDTO.getIconUrl());
        return this.save(user);
    }

    public PageResult<UserVO> pageQuery(UserQueryDTO userQueryDTO){
        LambdaQueryWrapper<D4cUser> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(userQueryDTO.getAccount())) {
            queryWrapper.like(D4cUser::getAccount, userQueryDTO.getAccount());
        }
        if (StringUtils.hasText(userQueryDTO.getUsername())) {
            queryWrapper.like(D4cUser::getUsername, userQueryDTO.getUsername());
        }
        if (CollectionUtils.isNotEmpty(userQueryDTO.getIds())){
            queryWrapper.in(D4cUser::getId, userQueryDTO.getIds());
        }
        if (CollectionUtils.isNotEmpty(userQueryDTO.getExcludeUserIds())){
            queryWrapper.notIn(D4cUser::getId, userQueryDTO.getExcludeUserIds());
        }

        IPage<UserVO> userVOIPage = baseMapper.selectPageVo(userQueryDTO.toPageDTO(), queryWrapper);
        return new PageResult<>(userVOIPage.getCurrent(), userVOIPage.getPages(), userVOIPage.getRecords(), userVOIPage.getTotal());
    }

    public D4cUser updateById(UserCreateDTO userCreateDTO) {
        D4cUser user = getById(userCreateDTO.getId());
        if (user == null) {
            throw new BusinessException(ResponseCode.USER_NOT_EXIST);
        }
        // 生成密码 加密
        String encodePassword = passwordEncoder.encode(userCreateDTO.getPassword());
        user.setUsername(userCreateDTO.getUsername());
        user.setAccount(userCreateDTO.getAccount());
        user.setPassword(encodePassword);
        user.setEmail(userCreateDTO.getEmail());
        user.setIconUrl(userCreateDTO.getIconUrl());
        super.updateById(user);
        return user;
    }

    /**
     * 管理员修改用户密码
     * @param userPasswordUpdateDTO 包含账号和新密码
     * @return 是否修改成功
     */
    public boolean updatePassword(UserPasswordUpdateDTO userPasswordUpdateDTO) {
        D4cUser user = getByAccount(userPasswordUpdateDTO.getAccount());
        if (user == null) {
            throw new BusinessException(ResponseCode.USER_NOT_EXIST);
        }
        // 加密新密码
        String encodePassword = passwordEncoder.encode(userPasswordUpdateDTO.getNewPassword());
        user.setPassword(encodePassword);
        return super.updateById(user);
    }
}
