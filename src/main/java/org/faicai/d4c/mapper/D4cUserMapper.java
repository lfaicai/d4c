package org.faicai.d4c.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.faicai.d4c.pojo.dto.UserQueryDTO;
import org.faicai.d4c.pojo.entity.D4cUser;
import org.faicai.d4c.pojo.vo.UserVO;

/**
 * <p>
 * 部门管理 Mapper 接口
 * </p>
 *
 * @author facai.lan
 */
@Mapper
public interface D4cUserMapper extends BaseMapper<D4cUser> {

    IPage<UserVO> selectPageVo(PageDTO<UserVO> page, @Param(Constants.WRAPPER) Wrapper<D4cUser> wrapper);
}
