package com.smart.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回结果
 *
 * @author smart
 */
@Data
public class PageResult<T> implements Serializable {

    private long total;

    private long page;

    private long size;

    private List<T> records;

    public static <T> PageResult<T> of(long total, long page, long size, List<T> records) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPage(page);
        pageResult.setSize(size);
        pageResult.setRecords(records);
        return pageResult;
    }
}
