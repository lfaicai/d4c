package org.faicai.d4c.controller;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.enums.ResourceType;
import org.faicai.d4c.pojo.vo.ResourceNodeVO;
import org.faicai.d4c.pojo.vo.SelectResult;
import org.faicai.d4c.pojo.vo.TableVO;
import org.faicai.d4c.service.ResourceService;
import org.faicai.d4c.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resource")
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping("/findCurrentUserTable/{connectId}/{dbName}")
    public R<List<TableVO>> findCurrentUserTableByConnectionId(@PathVariable Long connectId, @PathVariable String dbName){
        return R.ok(resourceService.findCurrentUserTableByConnectionIdAndDbName(connectId, dbName));
    }
    /**
     * 获取当前所有的数据库连接节点
     */
    @GetMapping("/findAllConnectionNodes")
    public R<List<ResourceNodeVO>> findAllConnectionNodes(){
        return R.ok(resourceService.findCurrentUserConnectionNodes());
    }


    /**
     * 获取当前节点的子节点
     */
    @GetMapping("/findChildrenNodes/{connectId}/{nodeType}/{nodeName}")
    public R<List<ResourceNodeVO>> findChildrenNodes(@PathVariable Long connectId, @PathVariable ResourceType nodeType, @PathVariable String nodeName){
        return R.ok(resourceService.findCurrentUserChildrenNodes(connectId, nodeType, nodeName));
    }

    /**
     * 获取当前节点的子节点
     */
    @GetMapping("/findNode/{id}")
    public R<ResourceNodeVO> findNodeById(@PathVariable Long id){
        return R.ok(resourceService.findNodeById(id));
    }


    /**
     * 获取表详情
     */
    @GetMapping({"/getTableDetails/{connectId}/{tableName}", "/getTableDetails/{connectId}/{schemaName}/{tableName}"})
    public R<SelectResult> getTableDetails (@PathVariable Long connectId, @PathVariable String tableName, @PathVariable(required = false) String schemaName){
        return R.ok(resourceService.getTableDetails(connectId, schemaName, tableName));
    }

    /**
     * 获取当前连接所有节点
     *
     */
    @GetMapping("/findAllNode/{connectId}")
    public R<List<ResourceNodeVO>> findAllNodeByConnectId(@PathVariable Long connectId){
        return R.ok(resourceService.findAllNodeByConnectId(connectId));
    }

}
