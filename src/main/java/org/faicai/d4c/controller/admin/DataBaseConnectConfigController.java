package org.faicai.d4c.controller.admin;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.pojo.dto.DataBaseConnectConfigDTO;
import org.faicai.d4c.pojo.entity.DataBaseConnectConfig;
import org.faicai.d4c.pojo.vo.PageResult;
import org.faicai.d4c.service.DataBaseConnectConfigService;
import org.faicai.d4c.utils.R;
import org.faicai.d4c.utils.UpdateValidationGroup;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/dbc")
public class DataBaseConnectConfigController {

    private final DataBaseConnectConfigService dataBaseConnectConfigService;


    /**
     * 分页查询
     */
    @PostMapping("/pageQuery")
    public R<PageResult<DataBaseConnectConfig>> pageQuery(@RequestBody DataBaseConnectConfigDTO dataBaseConnectConfigDTO) {
        return R.ok(dataBaseConnectConfigService.pageQuery(dataBaseConnectConfigDTO));
    }

    /**
     * 获取列表
     */
    @GetMapping("/list")
    public R<List<DataBaseConnectConfig>> list(){
        return R.ok(dataBaseConnectConfigService.list());
    }

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<DataBaseConnectConfig> create(@RequestBody DataBaseConnectConfig dataBaseConnectConfig) {
        if (dataBaseConnectConfigService.create(dataBaseConnectConfig)) {
            return R.ok(dataBaseConnectConfig);
        }
        return R.failed();
    }

    /**
     * 更新保存
     */
    @PostMapping("/saveOrUpdate")
    public R<Boolean> saveOrUpdate(@RequestBody DataBaseConnectConfig dataBaseConnectConfig){
        return R.ok(dataBaseConnectConfigService.saveOrUpdate(dataBaseConnectConfig));
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public R<DataBaseConnectConfig> update(@Validated(UpdateValidationGroup.class) @RequestBody DataBaseConnectConfig dataBaseConnectConfig) {
        if (dataBaseConnectConfigService.updateById(dataBaseConnectConfig)) {
            return R.ok(dataBaseConnectConfig);
        }
        return R.failed();
    }

    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id){
        return R.ok(dataBaseConnectConfigService.removeById(id));
    }

    @PostMapping("/testConnection")
    public R<Boolean> testConnection(@RequestBody DataBaseConnectConfig dataBaseConnectConfig){
        return R.ok(dataBaseConnectConfigService.testConnection(dataBaseConnectConfig));
    }


}
