package org.faicai.d4c.controller;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.service.SqlExecutionService;
import org.faicai.d4c.utils.R;
import org.faicai.d4c.utils.sql.SqlInfo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sql")
public class SqlExecutionController {

    private final SqlExecutionService sqlExecutionService;

    @PostMapping("/exec")
    public R<Object> exec(@RequestBody SqlInfo sqlInfo){
        return R.ok(sqlExecutionService.executeSql(sqlInfo));
    }


    @PostMapping("/page")
    public R<Object> page(@RequestBody SqlInfo sqlInfo){
        return R.ok(sqlExecutionService.executeSql(sqlInfo));
    }

    @PostMapping("/pageByTable")
    @ResponseBody
    public R<Object> pageByTable(@RequestBody SqlInfo sqlInfo){
        return R.ok(sqlExecutionService.pageByTable(sqlInfo));
    }

}
