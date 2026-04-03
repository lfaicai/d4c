package org.faicai.d4c.pojo.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import lombok.Data;

/**
 * @Describe：分页查询参数
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-10-11
 */
@Data
public class PageQueryBase<T> {

    private long page = 1L;

    private long pageSize = 20L;


    public PageDTO<T> toPageDTO(){
        return new PageDTO<T>(page, pageSize);
    }
}
