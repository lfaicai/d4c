package org.faicai.d4c.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.faicai.d4c.mapper.UserHistoryMapper;
import org.faicai.d4c.pojo.entity.UserHistory;
import org.faicai.d4c.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Describe： 用户历史记录
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-12-26
 */
@Service
@RequiredArgsConstructor
public class UserHistoryService extends ServiceImpl<UserHistoryMapper, UserHistory> implements IService<UserHistory> {

    private final PermissionService permissionService;

    public List<UserHistory> findCurrentHistory() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<UserHistory> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserHistory::getCreatedBy, userId);
        // 查询 是否还拥有权限
        List<UserHistory> userHistories = list(queryWrapper);
        Set<Long> tableIds = userHistories.stream().filter(uh -> "TABLE".equals(uh.getHistoryType())).map(UserHistory::getTableId).collect(Collectors.toSet());
        List<Long> selectTableIds = permissionService.findCurrentUserTableByTableIds(tableIds);
        for (UserHistory userHistory : userHistories) {
            userHistory.setCanSelect(selectTableIds.contains(userHistory.getTableId()));
        }
        return userHistories;
    }
}
