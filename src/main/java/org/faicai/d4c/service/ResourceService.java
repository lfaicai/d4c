package org.faicai.d4c.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.datasource.DataSourceException;
import org.faicai.d4c.constant.ResponseCode;
import org.faicai.d4c.core.DataSourceManager;
import org.faicai.d4c.enums.DataBaseAction;
import org.faicai.d4c.enums.DbDialect;
import org.faicai.d4c.enums.ResourceType;
import org.faicai.d4c.exception.BusinessException;
import org.faicai.d4c.mapper.ResourceMapper;
import org.faicai.d4c.pojo.dto.ResourceDTO;
import org.faicai.d4c.pojo.dto.ResourceUpdateDTO;
import org.faicai.d4c.pojo.dto.RoleResourceNodesDTO;
import org.faicai.d4c.pojo.entity.DataBaseConnectConfig;
import org.faicai.d4c.pojo.entity.Resource;
import org.faicai.d4c.pojo.entity.UserResource;
import org.faicai.d4c.pojo.vo.*;
import org.faicai.d4c.utils.SecurityUtils;
import org.faicai.d4c.utils.sql.SqlInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ResourceService extends ServiceImpl<ResourceMapper, Resource> implements IService<Resource> {

    private final DataBaseConnectConfigService dbConnectConfigService;

    private final DataSourceManager dataSourceManager;

    public ResourceService(@Lazy DataBaseConnectConfigService dbConnectConfigService, @Lazy DataSourceManager dataSourceManager) {
        this.dbConnectConfigService = dbConnectConfigService;
        this.dataSourceManager = dataSourceManager;
    }

    /**
     * 刷新资源
     *
     * @param id DataBaseConnectConfigService.id
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean refreshResource(Long id) {
        DataBaseConnectConfig dataBaseConnectConfig = dbConnectConfigService.getById(id);
        try {
            List<Resource> resources = dataSourceManager.getResourceInfo(dataBaseConnectConfig);
            return batchUpsertByKey(resources);
        } catch (SQLException e) {
            throw new DataSourceException("获取数据源失败!", e);
        }
    }

    /**
     * 刷新资源
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean refreshResource(DataBaseConnectConfig dataBaseConnectConfig) {
        try {
            List<Resource> resources = dataSourceManager.getResourceInfo(dataBaseConnectConfig);
            return batchUpsertByKey(resources);
        } catch (SQLException e) {
            throw new DataSourceException("获取数据源失败!", e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpsertByKey(List<Resource> resources) {
        Resource resource = resources.get(0);
        List<Resource> resourcesBD = getByDatabaseConnectId(resource.getDatabaseConnectId());
        if (CollectionUtils.isNotEmpty(resourcesBD)) {
            addUpdateId(resources, resourcesBD);
            Set<Long> deleteIds = getDeleteIds(resources, resourcesBD);
            if (CollectionUtils.isNotEmpty(deleteIds)) this.removeByIds(deleteIds);
        }
        return saveOrUpdateBatch(resources);

    }



    /**
     * 添加update数据的ID
     */
    public void addUpdateId(List<Resource> resources, final List<Resource> resourcesDB) {
        Map<String, Resource> dbResourceMap = resourcesDB.stream().collect(Collectors.groupingBy(Resource::getKey, Collectors.collectingAndThen(Collectors.toList(),
                list -> list.get(0))));
        for (Resource resource : resources) {
            if (dbResourceMap.containsKey(resource.getKey())) {
                Resource resourceDB = dbResourceMap.get(resource.getKey());
                resource.setId(resourceDB.getId());
            }
        }

    }

    /**
     * 获取需要删除的数据
     */
    public Set<Long> getDeleteIds(List<Resource> resources, final List<Resource> resourcesDB) {
        Map<String, Resource> resourceMap = resources.stream().collect(Collectors.groupingBy(Resource::getKey, Collectors.collectingAndThen(Collectors.toList(),
                list -> list.get(0))));
        HashSet<Long> ids = new HashSet<>();
        for (Resource resource : resourcesDB) {
            if (!resourceMap.containsKey(resource.getKey()) && !ResourceType.CONNECTION.equals(resource.getResourceType())) {
                ids.add(resource.getId());
            }
        }
        return ids;
    }

    public List<Resource> getByDatabaseConnectId(Long databaseConnectId) {
        LambdaQueryWrapper<Resource> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Resource::getDatabaseConnectId, databaseConnectId);
        return this.list(queryWrapper);
    }

    public List<Resource> getByDcIdAndType(Long databaseConnectId, ResourceType resourceType) {
        LambdaQueryWrapper<Resource> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Resource::getDatabaseConnectId, databaseConnectId);
        queryWrapper.eq(Resource::getResourceType, resourceType);
        return this.list(queryWrapper);
    }



    /**
     * 根据数据库连接获取当前用户资源
     * @param databaseConnectId 数据库连接
     */
    public List<UserResource> findCurrentUserResources(Long databaseConnectId){
        Long userId = SecurityUtils.getCurrentUserId();
        return baseMapper.findCurrentUserResources(databaseConnectId, userId);
    }

    /**
     * 根据数据库连接获取当前用户资源
     * @param databaseConnectId 数据库连接
     */
    public List<UserResource> findCurrentUserResourcesByAction(Long databaseConnectId, DataBaseAction action){
        Long userId = SecurityUtils.getCurrentUserId();
        return baseMapper.findCurrentUserResourcesByAction(userId, databaseConnectId, action.getCode());
    }


    public List<TableVO> findAllTableByDbId(Long connectId){
        return baseMapper.findAllTableByConnectId(connectId);
    }


    /**
     * 获取当前用户所拥有权限的表+描述
     */
    public List<TableVO> findAllTableByConnectIdAndUserId(Long connectId, String databaseName, Long userId){
        return baseMapper.findAllTableByConnectIdAndUserId(connectId, userId, databaseName);
    }

    /**
     * 根据连接id和表名 批量获取当前用户所拥有权限的表+描述
     */
    public List<ColumnVO> findAllColumnByTableNamesAndConnectIdAndUserId(Long connectId, Set<String> tableNames, Long userId){
        return baseMapper.findAllColumnByTableNamesAndConnectIdAndUserId(connectId, userId, tableNames);
    }


    /**
     * 获取当前用户所拥有权限的数据库
     */
    public List<DataBaseVO> findCurrentUserDbList(Long connectId){
        Long userId = SecurityUtils.getCurrentUserId();
//        return baseMapper.findCurrentUserDbConfig(userId);
        return baseMapper.findAllDb(connectId);
    }

    public List<TableVO> findCurrentUserTableByConnectionIdAndDbName(Long connectId, String dbName) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (baseMapper.isAllTable(userId, connectId)) {
            return baseMapper.findAllTableByConnectIdAdnDbName(connectId, dbName);
        }
        return baseMapper.findCurrentUserTableByConnectIdAdnDbName(userId, connectId, dbName);
    }

    public List<TableVO> findAllTableByConnectIdAdnDbName(Long connectId, String dbName) {
        return baseMapper.findAllTableByConnectIdAdnDbName(connectId, dbName);
    }

    public List<TableVO> findCurrentUserTableByConnectionIdAndSchema(Long connectId, String schema) {
//        Long userId = SecurityUtils.getCurrentUserId();
//        if (baseMapper.isAllTable(userId, dbId)) {
//            return baseMapper.findAllTableByDbId(dbId);
//        }
//        return baseMapper.findCurrentUserTableByDbId(userId, dbId, schema);
        return baseMapper.findAllTableByConnectId(connectId);
    }

    /**
     * 获取当前用户所拥有权限的表字段
     */
    public Set<String> findCurrentUserColumns(SqlInfo sqlInfo){
        Long userId = SecurityUtils.getCurrentUserId();
        if (baseMapper.isTableAllColumn(userId, sqlInfo.getConnectId(), sqlInfo.getTable())) {
            return Set.of("*");
        }
        return findValidCurrentUserColumnByTableName(userId, sqlInfo);
    }

    /**
     * 获取当前用户所拥有权限的数据库
     */
    public Set<String> findCurrentUserTableColumns(SqlInfo sqlInfo){
        Long userId = SecurityUtils.getCurrentUserId();
        if (baseMapper.isTableAllColumn(userId, sqlInfo.getConnectId(), sqlInfo.getTable())) {
            return dataSourceManager.getTableColumns(sqlInfo);
        }
        return findValidCurrentUserColumnByTableName(userId, sqlInfo);
    }

    /**
     * 获取有效的表字段
     * @param userId
     * @param sqlInfo
     * @return
     */
    public Set<String> findValidCurrentUserColumnByTableName(Long userId, SqlInfo sqlInfo){
        Set<String> tableColumns = dataSourceManager.getTableColumns(sqlInfo);
        Set<String> currentUserColumnByTableName = baseMapper.findCurrentUserColumnByTableName(userId, sqlInfo.getConnectId(), sqlInfo.getTable());
        return currentUserColumnByTableName.stream().filter(tableColumns::contains).collect(Collectors.toSet());
    }

    public List<ConnectionVO> findCurrentUserConnections() {
        Long userId = SecurityUtils.getCurrentUserId();
        return baseMapper.findCurrentUserConnections(userId);
    }

    public List<SchemaVO> findCurrentUserSchemas(Long connectId, String dbName) {
        return baseMapper.findAllSchemaByConnectIdAndDbName(connectId, dbName);
    }
    public List<SchemaVO> findCurrentUserSchemas(Long connectId) {
        return baseMapper.findAllSchemaByConnectId(connectId);
    }

    /**
     * 分页查询资源
     */
    public PageResult<Resource> pageQuery(ResourceDTO resourceDTO) {
        LambdaQueryWrapper<Resource> queryWrapper = Wrappers.lambdaQuery();
        
        if (resourceDTO.getDatabaseConnectId() != null) {
            // TODO 报错
            queryWrapper.eq(Resource::getDatabaseConnectId, resourceDTO.getDatabaseConnectId());
        }
        if (resourceDTO.getResourceType() != null) {
            queryWrapper.eq(Resource::getResourceType, resourceDTO.getResourceType());
        }
        if (StringUtils.hasText(resourceDTO.getDatabaseName())) {
            queryWrapper.like(Resource::getDatabaseName, resourceDTO.getDatabaseName());
        }
        if (StringUtils.hasText(resourceDTO.getSchemaName())) {
            queryWrapper.like(Resource::getSchemaName, resourceDTO.getSchemaName());
        }
        if (StringUtils.hasText(resourceDTO.getTableName())) {
            queryWrapper.like(Resource::getTableName, resourceDTO.getTableName());
        }
        if (StringUtils.hasText(resourceDTO.getColumnName())) {
            queryWrapper.like(Resource::getColumnName, resourceDTO.getColumnName());
        }
        if (StringUtils.hasText(resourceDTO.getDescription())) {
            queryWrapper.like(Resource::getDescription, resourceDTO.getDescription());
        }


        PageDTO<Resource> resourcePageDTO = baseMapper.selectPage(resourceDTO.toPageDTO(), queryWrapper);
        return new PageResult<>(resourcePageDTO.getCurrent(), resourcePageDTO.getPages(), resourcePageDTO.getRecords(), resourcePageDTO.getTotal());
    }


    /**
     * 获取当前用户连接节点
     */
    public List<ResourceNodeVO> findCurrentUserConnectionNodes() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<ConnectionVO> connectionVOS = baseMapper.findCurrentUserConnections(userId);
        return getResourceNodeVOS(connectionVOS);
    }


    /**
     * 转为ResourceNodeVO
     */
    private List<ResourceNodeVO> getResourceNodeVOS(List<ConnectionVO> connectionVOS) {
        return connectionVOS.stream().filter(Objects::nonNull).map(connectionVO -> {
            ResourceNodeVO resourceNodeVO = new ResourceNodeVO();
            resourceNodeVO.setId(connectionVO.getId());
            resourceNodeVO.setConnectId(connectionVO.getId());
            resourceNodeVO.setConnectName(connectionVO.getName());
            resourceNodeVO.setName(connectionVO.getName());
            resourceNodeVO.setType(ResourceType.CONNECTION);
            resourceNodeVO.setDbType(connectionVO.getDbType());
            return resourceNodeVO;
        }).collect(Collectors.toList());
    }

    /**
     * 获取所有连接节点
     */
    public List<ResourceNodeVO> findAllConnectionNodes() {
        List<ConnectionVO> connectionVOS = baseMapper.findAllConnections();
        return getResourceNodeVOS(connectionVOS);
    }

    /**
     * 获取当前角色的连接节点
     */
    public List<PermissionResourceNodeVO> findConnectionNodesByRoleId(Long roleId) {
        return baseMapper.findConnectionNodesByRoleId(roleId, ResourceType.CONNECTION);
    }


    /**
     * 获取节点
     * @param connectId 连接id
     * @param nodeType 节点类型
     * @param nodeName 节点名称
     */
    public List<ResourceNodeVO> findNodes(Long connectId, ResourceType nodeType, String nodeName) {
        return baseMapper.findNodes(connectId, nodeType, nodeName);
    }

    public List<PermissionResourceNodeVO> findNodesByRoleId(Long connectId, String dbName, String schemaName, Long roleId, ResourceType nodeType, String nodeName) {
        nodeType = getDbDialect(connectId, nodeType);
        return baseMapper.findNodesByRoleId(connectId, dbName, schemaName, roleId, nodeType, nodeName);
    }

    public List<ResourceNodeVO> findChildrenNodes(Long connectId, ResourceType nodeType, String nodeName) {
        nodeType = getDbDialect(connectId, nodeType);
        return baseMapper.findNodes(connectId, nodeType, nodeName);
    }

    /**
     * 获取所有孙子节点id
     * @param id 当前节点id
     * @param resourceType
     * @return
     */
    public Set<Long> findLowerLevelNodeIds(Long id, ResourceType resourceType) {
        return baseMapper.findLowerLevelNodeIds(id, resourceType);
    }
    public Set<Long> findUpperLevelNodeIds(Long id, ResourceType resourceType) {
        return baseMapper.findUpperLevelNodeIds(id, resourceType);
    }


    public List<ResourceNodeVO> findChildrenNodesByRoleId(RoleResourceNodesDTO roleResourceNodesDTO) {
        ResourceType nodeType = getDbDialect(roleResourceNodesDTO.getConnectId(), roleResourceNodesDTO.getNodeType());
        List<ResourceNodeVO> childrenNodes = baseMapper.findNodesAndDbName(roleResourceNodesDTO.getConnectId(), nodeType, roleResourceNodesDTO.getNodeName(), roleResourceNodesDTO.getDbName());
        Set<Long> roleConnectionIds = baseMapper.findSelectedConnectionsByRole(roleResourceNodesDTO.getRoleId(), nodeType);
        for (ResourceNodeVO childrenNode : childrenNodes) {
            if (roleConnectionIds.contains(childrenNode.getId())) {
                childrenNode.setSelected(true);
            }
        }
        return childrenNodes;
    }


    public List<ResourceNodeVO> findCurrentUserChildrenNodes(Long connectId, ResourceType nodeType, String nodeName) {
        nodeType = getDbDialect(connectId, nodeType);
        Long userId = SecurityUtils.getCurrentUserId();
        return baseMapper.findCurrentUserNodes(userId, connectId, nodeType, nodeName);
    }

    private ResourceType getDbDialect(Long connectId, ResourceType nodeType) {
        DataBaseConnectConfig dataBaseConnectConfig = dbConnectConfigService.getById(connectId);
        if (dataBaseConnectConfig == null) {
            return null;
        }
        DbDialect dbType = dataBaseConnectConfig.getDbType();
        return switch (nodeType) {
            case CONNECTION -> dbType.isHasDatabase() ? ResourceType.DATABASE : ResourceType.SCHEMA;
            case DATABASE -> dbType.isHasSchema() ? ResourceType.SCHEMA : ResourceType.TABLE;
            case SCHEMA -> ResourceType.TABLE;
            case TABLE -> ResourceType.COLUMN;
            default -> nodeType;
        };
    }

    public SelectResult getTableDetails(Long connectId, String schemaName, String tableName) {
        Long userId = SecurityUtils.getCurrentUserId();
        Set<String> currentUserColumnByTableName = baseMapper.findCurrentUserColumnByTableName(userId, connectId, tableName);
        if (CollectionUtils.isEmpty(currentUserColumnByTableName)) {
            throw new BusinessException(ResponseCode.NOT_HAVE_TABLE_PERMISSIONS);
        }
        return dataSourceManager.getTableDetails(connectId, schemaName, tableName, currentUserColumnByTableName);
    }

    public ResourceNodeVO findNodeById(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return baseMapper.findNodeById(userId, id);
    }

    public List<ResourceNodeVO> findAllNodeByConnectId(Long connectId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return baseMapper.findNodesByUserIdAndConnectId(userId, connectId);
    }

    public Boolean updateDescriptionAi(ResourceUpdateDTO resourceUpdateDTO) {
        return baseMapper.updateDescriptionAiById(resourceUpdateDTO.getId(), resourceUpdateDTO.getDescriptionAi());
    }
}
