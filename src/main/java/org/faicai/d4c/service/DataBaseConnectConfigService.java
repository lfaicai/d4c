package org.faicai.d4c.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.faicai.d4c.core.DataSourceManager;
import org.faicai.d4c.enums.ResourceType;
import org.faicai.d4c.exception.BusinessException;
import org.faicai.d4c.mapper.DataBaseConnectConfigMapper;
import org.faicai.d4c.pojo.dto.DataBaseConnectConfigDTO;
import org.faicai.d4c.pojo.entity.DataBaseConnectConfig;
import org.faicai.d4c.pojo.entity.Resource;
import org.faicai.d4c.pojo.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.SQLException;

@Service
public class DataBaseConnectConfigService extends ServiceImpl<DataBaseConnectConfigMapper, DataBaseConnectConfig> implements IService<DataBaseConnectConfig> {

    @Autowired
    private ResourceService resourceService;

    @Lazy
    @Autowired
    private DataSourceManager dataSourceManager;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(DataBaseConnectConfig entity) {
        // 更新资源
        if (super.saveOrUpdate(entity)) {
            return resourceService.refreshResource(entity);
        }
        return false;
    }

    public PageResult<DataBaseConnectConfig> pageQuery(DataBaseConnectConfigDTO dataBaseConnectConfigDTO) {
        LambdaQueryWrapper<DataBaseConnectConfig> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(dataBaseConnectConfigDTO.getName())) {
            queryWrapper.like(DataBaseConnectConfig::getName, dataBaseConnectConfigDTO.getName());
        }
        if (StringUtils.hasText(dataBaseConnectConfigDTO.getHost())) {
            queryWrapper.like(DataBaseConnectConfig::getHost, dataBaseConnectConfigDTO.getHost());
        }
        if (StringUtils.hasText(dataBaseConnectConfigDTO.getDatabaseName())) {
            queryWrapper.like(DataBaseConnectConfig::getDatabaseName, dataBaseConnectConfigDTO.getDatabaseName());
        }
        if (StringUtils.hasText(dataBaseConnectConfigDTO.getSchemaName())) {
            queryWrapper.like(DataBaseConnectConfig::getSchemaName, dataBaseConnectConfigDTO.getSchemaName());
        }
        if (null != dataBaseConnectConfigDTO.getDbType()) {
            queryWrapper.like(DataBaseConnectConfig::getDbType, dataBaseConnectConfigDTO.getDbType());
        }
        PageDTO<DataBaseConnectConfig> pageDTO = baseMapper.selectPage(dataBaseConnectConfigDTO.toPageDTO(), queryWrapper);
        return new PageResult<>(pageDTO.getCurrent(), pageDTO.getPages(), pageDTO.getRecords(), pageDTO.getTotal());
    }

    public Boolean testConnection(DataBaseConnectConfig dataBaseConnectConfig) {
        try {
            dataSourceManager.getConnection(dataBaseConnectConfig);
        } catch (BusinessException e) {
            return false;
        }
        return true;
    }

    public boolean create(DataBaseConnectConfig dataBaseConnectConfig) {

        // 加密密码
        boolean ok = save(dataBaseConnectConfig);
        if (ok){
            Resource resource = new Resource();
            resource.setId(dataBaseConnectConfig.getId());
            resource.setDatabaseConnectId(dataBaseConnectConfig.getId());
            resource.setResourceType(ResourceType.CONNECTION);
            resourceService.save(resource);
        }
        return ok;
    }
}
