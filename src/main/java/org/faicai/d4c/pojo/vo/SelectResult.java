package org.faicai.d4c.pojo.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Describe：查询返回值参数
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-09-16
 */
@Data
public class SelectResult {
    private List<String> heads;
    private List<Map<String, Object>> rows;


    public SelectResult() {
    }

    public SelectResult(List<String> heads, List<Map<String, Object>> rows) {
        this.heads = heads;
        this.rows = rows;
    }
}
