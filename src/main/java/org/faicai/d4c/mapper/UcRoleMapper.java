package org.faicai.d4c.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.faicai.d4c.pojo.entity.Role;
import org.faicai.d4c.pojo.entity.UcRole;

import java.util.List;

@Mapper
public interface UcRoleMapper extends BaseMapper<UcRole> {

    /**
     * 根据用户ID查询角色列表
     */
    @Select("SELECT r.* FROM role r " +
            "INNER JOIN uc_role ur ON r.id = ur.role_id " +
            "WHERE ur.uc_id = #{userId} AND ur.deleted = false AND r.deleted = false")
    List<Role> getRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户ID列表
     */
    @Select("SELECT ur.uc_id FROM uc_role ur " +
            "WHERE ur.role_id = #{roleId} AND ur.deleted = false")
    List<Long> getUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查用户是否拥有指定角色
     */
    @Select("SELECT COUNT(1) FROM uc_role ur " +
            "WHERE ur.uc_id = #{userId} AND ur.role_id = #{roleId} AND ur.deleted = false")
    int checkUserHasRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

}
