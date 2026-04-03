package org.faicai.d4c.controller.admin;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.enums.ResourceType;
import org.faicai.d4c.pojo.dto.ResourceDTO;
import org.faicai.d4c.pojo.dto.ResourceUpdateDTO;
import org.faicai.d4c.pojo.dto.RoleResourceNodesDTO;
import org.faicai.d4c.pojo.entity.Resource;
import org.faicai.d4c.pojo.vo.*;
import org.faicai.d4c.service.ResourceService;
import org.faicai.d4c.utils.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/resource")
public class AdminResourceController {

    private final ResourceService resourceService;



    /**
     * 分页查询资源
     */
    @PostMapping("/pageQuery")
    public R<PageResult<Resource>> pageQuery(@RequestBody ResourceDTO resourceDTO) {
        return R.ok(resourceService.pageQuery(resourceDTO));
    }

    /**
     * 根据databaseConnectId刷新资源
     */
    @GetMapping("/refresh")
    public R<Boolean> refresh(@RequestParam Long databaseConnectId) {
        return R.ok(resourceService.refreshResource(databaseConnectId));
    }

    /**
     * 根据databaseConnectId和ResourceType查询
     */
    @GetMapping("/getByDcIdAndType")
    public R<List<Resource>> getByDcIdAndType(@RequestParam("databaseConnectId") Long databaseConnectId,
                                              @RequestParam("type") ResourceType type) {
        return R.ok(resourceService.getByDcIdAndType(databaseConnectId, type));
    }

    /**
     * 获取当前所有的数据库连接节点
     */
    @GetMapping("/findAllConnectionNodes")
    public R<List<ResourceNodeVO>> findAllConnectionNodes(){
        return R.ok(resourceService.findAllConnectionNodes());
    }



    /**
     * 根据角色获取当前所有的数据库连接节点
     */
    @GetMapping("/findConnectionNodesByRoleId/{roleId}")
    public R<List<PermissionResourceNodeVO>> findConnectionNodesByRoleId(@PathVariable Long roleId){
        return R.ok(resourceService.findConnectionNodesByRoleId(roleId));
    }

    /**
     * 获取当前节点的子节点
     */
    @PostMapping("/findChildrenNodesByRoleId")
    public R<List<ResourceNodeVO>> findChildrenNodesByRoleId(@RequestBody RoleResourceNodesDTO roleResourceNodesDTO){
        return R.ok(resourceService.findChildrenNodesByRoleId(roleResourceNodesDTO));
    }


    /**
     * 根据角色获取当前节点的子节点
     */
    @GetMapping("/findNodesByRoleId/{roleId}/{nodeType}/{nodeName}")
    public R<List<PermissionResourceNodeVO>> findNodesByRoleId(
            @PathVariable Long roleId,
            @RequestParam(name = "connectId", required = false) Long connectId,
            @RequestParam(name = "dbName", required = false) String dbName,
            @RequestParam(name = "schemaName", required = false) String schemaName,
            @PathVariable ResourceType nodeType,
            @PathVariable String nodeName){
        return R.ok(resourceService.findNodesByRoleId(connectId, dbName, schemaName, roleId, nodeType, nodeName));
    }


    /**
     * 获取当前所有的数据库连接
     */
    @GetMapping("/findNodes/{connectId}/{nodeType}/{nodeName}")
    public R<List<ResourceNodeVO>> findNodes(@PathVariable Long connectId, @PathVariable ResourceType nodeType, @PathVariable String nodeName){
        return R.ok(resourceService.findNodes(connectId, nodeType, nodeName));
    }

    /**
     * 获取当前节点的子节点
     */
    @GetMapping("/findChildrenNodes/{connectId}/{nodeType}/{nodeName}")
    public R<List<ResourceNodeVO>> findChildrenNodes(@PathVariable Long connectId, @PathVariable ResourceType nodeType, @PathVariable String nodeName){
        return R.ok(resourceService.findChildrenNodes(connectId, nodeType, nodeName));
    }

    @PutMapping("/updateDescriptionAi")
    public R<Boolean> updateDescriptionAi(@RequestBody ResourceUpdateDTO resourceUpdateDTO ){
        return R.ok(resourceService.updateDescriptionAi(resourceUpdateDTO));
    }


}
