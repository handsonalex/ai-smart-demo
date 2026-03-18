package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper
 *
 * @author Joseph Ho
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
