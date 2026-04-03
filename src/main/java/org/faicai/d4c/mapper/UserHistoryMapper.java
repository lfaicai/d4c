package org.faicai.d4c.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.faicai.d4c.pojo.entity.UserHistory;

import java.util.List;

@Mapper
public interface UserHistoryMapper extends BaseMapper<UserHistory> {

    List<UserHistory> findCurrentHistory(Long currentUserId);
}
