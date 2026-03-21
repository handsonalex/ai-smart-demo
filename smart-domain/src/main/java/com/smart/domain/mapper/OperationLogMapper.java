package com.smart.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.domain.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志数据访问层接口，对应MySQL数据库中的 t_operation_log 表。
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，自动获得单表CRUD、分页查询、条件构造器查询等能力。
 * 常用场景：记录用户操作日志、按操作类型和时间范围查询审计记录等。
 * </p>
 *
 * @author Joseph Ho
 * @see OperationLog
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
