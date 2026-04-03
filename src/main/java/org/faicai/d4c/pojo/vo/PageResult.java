package org.faicai.d4c.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @Describe： 分页返回值
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-09-16
 */
@Data
public class PageResult<T> {

    /**
     * 结果列表头
     */
    private List<String> heads;

    /**
     * 结果列表
     */
    private List<T> rows;

    /**
     * 总记录数
     */
    private long totalRows;

    /**
     *当前页码
     */
    private long page;

    /**
     *每页条数
     */
    private long pageSize;

    private boolean allColumnPermission;

    public PageResult() {
    }

    public PageResult(long page, long pageSize, List<T> rows, long totalRows) {
        this.page = page;
        this.pageSize = pageSize;
        this.rows = rows;
        this.totalRows = totalRows;
    }

    public PageResult(long page, long pageSize, List<String> heads, List<T> rows, long totalRows, boolean allColumnPermission) {
        this.heads = heads;
        this.page = page;
        this.pageSize = pageSize;
        this.rows = rows;
        this.totalRows = totalRows;
        this.allColumnPermission = allColumnPermission;
    }
}
