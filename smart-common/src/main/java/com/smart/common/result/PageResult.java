package com.smart.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果封装类
 * <p>
 * 用于封装分页查询接口的返回数据，包含分页元信息和当前页的数据列表。
 * 通常作为 {@link Result} 的 data 字段使用，即 Result&lt;PageResult&lt;T&gt;&gt; 的形式。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>使用泛型 T 支持任意类型的分页数据项</li>
 *   <li>实现 {@link Serializable} 接口，支持序列化传输</li>
 *   <li>提供静态工厂方法 of()，简化创建过程</li>
 * </ul>
 * </p>
 *
 * @param <T> 分页数据项的类型
 * @author Joseph Ho
 */
@Data
public class PageResult<T> implements Serializable {

    /** 数据总条数，用于前端计算总页数 */
    private long total;

    /** 当前页码（从 1 开始） */
    private long page;

    /** 每页数据条数 */
    private long size;

    /** 当前页的数据列表 */
    private List<T> records;

    /**
     * 静态工厂方法，构建分页结果对象
     *
     * @param total   数据总条数
     * @param page    当前页码
     * @param size    每页数据条数
     * @param records 当前页的数据列表
     * @param <T>     数据项类型
     * @return 封装好的分页结果对象
     */
    public static <T> PageResult<T> of(long total, long page, long size, List<T> records) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPage(page);
        pageResult.setSize(size);
        pageResult.setRecords(records);
        return pageResult;
    }
}
