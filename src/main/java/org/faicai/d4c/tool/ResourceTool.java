package org.faicai.d4c.tool;

import org.faicai.d4c.pojo.vo.ColumnVO;
import org.faicai.d4c.pojo.vo.TableVO;
import org.faicai.d4c.service.ResourceService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public class ResourceTool {



    private final ResourceService resourceService;

    public ResourceTool(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Tool(description = "获取当前库所有表描述和id")
    public List<TableVO> getTables(@ToolParam(description = "数据库连接Id")Long databaseConnectId,
                                   @ToolParam(description = "用户id")Long userId,
                                   @ToolParam(description = "数据库连接名称")String databaseName
    ){
        return resourceService.findAllTableByConnectIdAndUserId(databaseConnectId, databaseName, userId);
    }

    @Tool(description = "批量获取指定表字段详情")
    public List<ColumnVO> getTableInfos(@ToolParam(description = "数据库连接Id")Long databaseConnectId,
                                        @ToolParam(description = "用户id")Long userId,
                                 @ToolParam(description = "表名") Set<String> tableNames){
        return resourceService.findAllColumnByTableNamesAndConnectIdAndUserId(databaseConnectId, tableNames, userId);
    }
}
