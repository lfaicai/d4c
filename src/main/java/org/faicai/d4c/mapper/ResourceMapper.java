package org.faicai.d4c.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.faicai.d4c.enums.ResourceType;
import org.faicai.d4c.pojo.entity.Resource;
import org.faicai.d4c.pojo.entity.UserResource;
import org.faicai.d4c.pojo.vo.*;
import org.faicai.d4c.utils.sql.SqlParseUtils;

import java.util.List;
import java.util.Set;

@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

    List<Resource> resourcesList(List<SqlParseUtils.SqlDetails> sqlDetails);

    List<UserResource> findCurrentUserResources(@Param("databaseConnectId") Long databaseConnectId, @Param("userId") Long userId);



    @Select("""
            SELECT
            	r.id AS id,
            	r.database_name as name,
            	dcc.db_type
            FROM
            	resource r LEFT JOIN database_connect_config dcc ON r.database_connect_id  = dcc.id
            WHERE
            	r.resource_type = 'DATABASE'
                AND r.database_connect_id = #{connectId}
            	AND r.deleted = false
            """)
    List<DataBaseVO> findAllDb(@Param("connectId") Long connectId);

    @Select("""
            SELECT DISTINCT
            	c.id,
            	name,
            	db_type
            FROM
                uc_role ur
                LEFT JOIN permission p ON
                    ur.role_id = p.role_id
            	LEFT JOIN  resource r ON
            	    p.resource_id = r.id
                LEFT JOIN database_connect_config c ON
                    r.database_connect_id = c.id
            WHERE
            	r.deleted = false and p.deleted = false and ur.uc_id = #{userId} AND r.resource_type  = 'CONNECTION' and p.can_select = true
            """)
    List<ConnectionVO> findCurrentUserConnections(@Param("userId") Long userId);

    @Select("""
            SELECT
            	id,
            	name,
            	db_type
            FROM
                database_connect_config
            WHERE
            	deleted = false
            """)
    List<ConnectionVO> findAllConnections();

    @Select("""
            SELECT
            	r.id AS id
            FROM
                permission p
                LEFT JOIN  resource r on p.resource_id = r.id
            WHERE
                r.deleted = false AND p.deleted = false AND r.resource_type = #{resourceType} AND p.role_id =#{roleId}
            """)
    Set<Long> findSelectedConnectionsByRole(@Param("roleId") Long roleId, @Param("resourceType") ResourceType resourceType);


    List<PermissionResourceNodeVO> findConnectionNodesByRoleId(@Param("roleId")Long roleId, @Param("resourceType") ResourceType resourceType);

    @Select("""
                SELECT
                	r.database_connect_id AS id,
                	dcc.name,
                	dcc.db_type
                FROM
                	uc_role ur
                LEFT JOIN permission p ON
                	ur.role_id = p.role_id
                LEFT JOIN resource r ON
                	p.resource_id = r.id
                LEFT JOIN database_connect_config dcc on r.database_connect_id  = dcc.id
                WHERE
                    r.deleted = false and p.deleted = false and
                	ur.uc_id = #{userId} AND r.resource_type  = 'DATABASE'
            """)
    List<DataBaseVO> findCurrentUserDbConfig(@Param("userId") Long userId);



    @Select("""
            SELECT
                	r.id AS id,
                	r.schema_name AS name,
                	dcc.db_type
                FROM
                    resource r
                LEFT JOIN database_connect_config dcc on r.database_connect_id  = dcc.id
                WHERE
                    r.deleted = false
                    AND r.database_connect_id = #{connectId}
                    AND r.database_name = #{dbName}
                    AND r.resource_type  = 'SCHEMA'
                    AND r.schema_name <> '*'
            """)
    List<SchemaVO> findAllSchemaByConnectIdAndDbName(@Param("connectId") Long connectId, @Param("dbName")String dbName);

    @Select("""
            SELECT
                	r.id AS id,
                	r.schema_name AS name,
                	dcc.db_type
                FROM
                    resource r
                LEFT JOIN database_connect_config dcc on r.database_connect_id  = dcc.id
                WHERE
                    r.deleted = false
                    AND r.database_connect_id = #{connectId}
                    AND r.resource_type  = 'SCHEMA'
                    AND r.schema_name <> '*'
            """)
    List<SchemaVO> findAllSchemaByConnectId(@Param("connectId") Long connectId);

    @Select("""
                SELECT
                	r.id AS id,
                	r.schema_name AS name,
                	dcc.db_type
                FROM
                	uc_role ur
                LEFT JOIN permission p ON
                	ur.role_id = p.role_id
                LEFT JOIN resource r ON
                	p.resource_id = r.id
                LEFT JOIN database_connect_config dcc on r.database_connect_id  = dcc.id
                WHERE
                    r.deleted = false AND p.deleted = false AND
                	ur.uc_id = #{userId} AND r.database_connect_id = #{connectId} AND r.resource_type  = 'SCHEMA' AND r.schema_name <> '*'
            """)
    List<SchemaVO> findCurrentUserSchemaByConnectId(@Param("userId") Long userId, @Param("connectId") Long connectId);


    @Select("""
                SELECT
                	r.database_connect_id AS id,
                	r.table_name AS name,
                	dcc.db_type
                FROM
                	uc_role ur
                LEFT JOIN permission p ON
                	ur.role_id = p.role_id
                LEFT JOIN resource r ON
                	p.resource_id = r.id
                LEFT JOIN database_connect_config dcc on r.database_connect_id  = dcc.id
                WHERE
                    r.deleted = false and p.deleted = false
                    AND r.database_connect_id = #{connectId}
                	AND r.resource_type  = 'TABLE'
            """)
    List<TableVO> findAllTableByConnectId(@Param("connectId") Long connectId);

    @Select("""
               SELECT
                	r.table_name AS name,
                    r.description_ai AS description
                FROM
                	uc_role ur
                LEFT JOIN permission p ON
                	ur.role_id = p.role_id
                LEFT JOIN resource r ON
                	p.resource_id = r.id
                LEFT JOIN database_connect_config dcc on r.database_connect_id  = dcc.id
                WHERE
                    r.deleted = false and p.deleted = false
                    AND r.database_connect_id = #{connectId}
                    AND r.database_name = #{databaseName}
                	AND ur.uc_id = #{userId} AND r.resource_type  = 'TABLE'
            """)
    List<TableVO> findAllTableByConnectIdAndUserId(@Param("connectId") Long connectId, @Param("userId") Long userId, @Param("databaseName") String databaseName);


    List<ColumnVO> findAllColumnByTableNamesAndConnectIdAndUserId(@Param("connectId") Long connectId, @Param("userId") Long userId, @Param("tableNames")Set<String> tableNames);



    @Select("""
            SELECT
                	r.id AS id,
                	r.table_name AS name,
                	dcc.db_type
                FROM
                    resource r
                LEFT JOIN database_connect_config dcc on r.database_connect_id  = dcc.id
                WHERE
                    r.deleted = false
                    AND r.database_connect_id = #{connectId}
                    AND r.database_name =#{dbName}
                    AND r.resource_type  = 'TABLE' AND r.table_name <> '*'
            """)
    List<TableVO> findAllTableByConnectIdAdnDbName(@Param("connectId") Long connectId, @Param("dbName")String dbName);

    @Select("""
            SELECT
                	r.id AS id,
                	r.table_name AS name,
                	dcc.db_type
                FROM
                    uc_role ur
                LEFT JOIN permission p ON
                	ur.role_id = p.role_id
                LEFT JOIN resource r ON
                 	p.resource_id = r.id
                LEFT JOIN database_connect_config dcc on r.database_connect_id  = dcc.id
                WHERE
                    r.deleted = false
                    AND ur.uc_id = #{userId}
                    AND r.database_connect_id = #{connectId}
                    AND r.database_name =#{dbName}
                    AND r.resource_type  = 'TABLE' AND r.table_name <> '*'
            """)
    List<TableVO> findCurrentUserTableByConnectIdAdnDbName(@Param("userId") Long userId, @Param("connectId") Long connectId, @Param("dbName")String dbName);

    @Select("""
                SELECT
                	r.id AS id,
                	r.table_name AS name,
                	dcc.db_type
                FROM
                	uc_role ur
                LEFT JOIN permission p ON
                	ur.role_id = p.role_id
                LEFT JOIN resource r ON
                	p.resource_id = r.id
                LEFT JOIN database_connect_config dcc on r.database_connect_id  = dcc.id
                WHERE
                    r.deleted = false
                    AND p.deleted = false
                    AND ur.uc_id = #{userId}
                    AND r.database_connect_id = #{connectId}
                    AND r.schema_name = #{schema}
                    AND r.resource_type  = 'TABLE'
                    AND r.table_name <> '*'
            """)
    List<TableVO> findCurrentUserTableByConnectId(@Param("userId") Long userId, @Param("connectId") Long connectId, @Param("schema")String schema);


    @Select("""
                 SELECT
                 	count(1) > 0
                 FROM
                 	uc_role ur
                 LEFT JOIN permission p ON
                 	ur.role_id = p.role_id
                 LEFT JOIN resource r ON
                 	p.resource_id = r.id
                 WHERE
                    r.deleted = false and p.deleted = false and
                 	ur.uc_id = #{userId} AND r.database_connect_id = #{connectId} AND r.resource_type  = 'TABLE' AND r.table_name = '*'
            """)
    boolean isAllTable(@Param("userId") Long userId, @Param("connectId") Long connectId);

    @Select("""
                 SELECT
                 	count(1) > 0
                 FROM
                 	uc_role ur
                 LEFT JOIN permission p ON
                 	ur.role_id = p.role_id
                 LEFT JOIN resource r ON
                 	p.resource_id = r.id
                 WHERE
                    r.deleted = false and p.deleted = false and
                 	ur.uc_id = #{userId} AND r.database_connect_id = #{connectId} AND r.table_name =  #{tableName} AND r.resource_type  = 'TABLE' AND r.table_name = '*'
            """)
    boolean isTableByUserIdAndTable(@Param("userId") Long userId, @Param("connectId") Long connectId, @Param("tableName") String tableName);

    @Select("""
                 SELECT
                 	count(1) > 0
                 FROM
                 	uc_role ur
                 LEFT JOIN permission p ON
                 	ur.role_id = p.role_id
                 LEFT JOIN resource r ON
                 	p.resource_id = r.id
                 WHERE
                    r.deleted = false AND p.deleted = false AND
                 	ur.uc_id = #{userId} AND r.database_connect_id = #{connectId} AND r.resource_type  = 'COLUMN' AND r.column_name = '*' AND r.table_name = #{tableName}
            """)
    boolean isTableAllColumn(@Param("userId") Long userId, @Param("connectId") Long connectId, @Param("tableName") String tableName);


    @Select("""
                SELECT
                	r.column_name AS columnName
                FROM
                	uc_role ur
                LEFT JOIN permission p ON
                	ur.role_id = p.role_id
                LEFT JOIN resource r ON
                	p.resource_id = r.id
                WHERE
                    r.deleted = false 
                    AND p.deleted = false
                    AND ur.uc_id = #{userId} 
                    AND r.database_connect_id = #{connectId} 
                    AND r.resource_type  = 'COLUMN' 
                    AND r.table_name = #{tableName} 
                    AND p.can_select = true
            """)
    Set<String> findCurrentUserColumnByTableName(@Param("userId") Long userId, @Param("connectId") Long connectId, @Param("tableName") String tableName);

    List<UserResource> findCurrentUserResourcesByAction(@Param("userId") Long userId, @Param("connectId") Long connectId, @Param("action") String action);

    @Select("""
            SELECT
                id AS id,
                column_name AS name,
                data_type AS type,
                description
            FROM
                resource r
            WHERE
               deleted = false and database_connect_id = #{connectId} and table_name = #{tableName} and resource_type  = 'COLUMN'
            """)
    List<ColumnVO> findColumnsByConnectionIdAndTable(@Param("connectId")Long connectId,  @Param("tableName")String tableName);


    List<ResourceNodeVO> findNodes(@Param("connectId")Long connectId, @Param("resourceType") ResourceType resourceType, @Param("nodeName")String nodeName);

    List<ResourceNodeVO> findNodesByUserIdAndConnectId(@Param("userId") Long userId, @Param("connectId")Long connectId);

    List<ResourceNodeVO> findNodesAndDbName(@Param("connectId")Long connectId, @Param("resourceType") ResourceType resourceType, @Param("nodeName")String nodeName, @Param("dbName") String dbName);

    List<ResourceNodeVO> findCurrentUserNodes(@Param("userId") Long userId, @Param("connectId")Long connectId, @Param("resourceType") ResourceType resourceType, @Param("nodeName")String nodeName);

    List<PermissionResourceNodeVO> findNodesByRoleId(
            @Param("connectId")Long connectId,
            @Param("databaseName") String databaseName,
            @Param("schemaName") String schemaName,
            @Param("roleId")Long roleId,
            @Param("resourceType")ResourceType resourceType,
            @Param("nodeName")String nodeName);

    ResourceNodeVO findNodeById(@Param("userId") Long userId, @Param("id")Long id);

    Set<Long> findLowerLevelNodeIds(@Param("id")Long id, @Param("resourceType")ResourceType resourceType);
    Set<Long> findUpperLevelNodeIds(@Param("id")Long id, @Param("resourceType")ResourceType resourceType);

    @Update("update resource set description_ai = #{descriptionAi} where id = #{id}")
    Boolean updateDescriptionAiById(@Param("id")Long id, @Param("descriptionAi")String descriptionAi);
}
