package org.faicai.d4c.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.faicai.d4c.pojo.entity.DataBaseConnectConfig;
import org.faicai.d4c.pojo.vo.DataBaseVO;
import org.faicai.d4c.pojo.vo.TableVO;

import java.util.List;

@Mapper
public interface DataBaseConnectConfigMapper extends BaseMapper<DataBaseConnectConfig> {


}
