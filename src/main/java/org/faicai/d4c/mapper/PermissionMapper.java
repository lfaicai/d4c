package org.faicai.d4c.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.faicai.d4c.pojo.entity.Permission;

import java.util.List;
import java.util.Set;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {


    @Select("""
        select p.* from uc_role ur left join "permission" p on ur.role_id = p.role_id where ur.uc_id = #{userId} and p.resource_id = #{resourceId} and ur.role_id  = #{roleId}
    """)
    Permission getByResourceIdAndRoleIdAndUserId(@Param("resourceId") Long resourceId, @Param("roleId")Long roleId, @Param("userId")Long userId);

//    @Select({"""
//        <script>
//            select p.resource_id from uc_role ur left join "permission" p on ur.role_id = p.role_id where ur.uc_id = #{userId}
//               <if test='tableIds != null and matnrs.size() > 0'> </
//        </script>
//    """})
    List<Long> findSelectTableIdsByUserIdAndTableIds(@Param("userId")Long userId, @Param("tableIds") Set<Long> tableIds);
}
