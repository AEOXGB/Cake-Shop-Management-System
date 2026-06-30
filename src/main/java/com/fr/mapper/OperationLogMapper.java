package com.fr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志数据访问接口
 * 对应数据库表：operation_log
 * 
 * 功能描述：负责系统操作日志的增删改查操作，用于记录所有管理员和用户的关键操作行为，便于审计追踪和问题排查。
 * 主要业务场景：后台管理操作记录、用户重要行为日志、操作审计、问题追溯、安全监控等。
 * 
 * 操作日志信息包含：日志ID、操作人用户名、操作描述（如"删除商品"、"审核用户"等）、
 * 请求的类名.方法名、请求参数、操作人IP地址、操作时间等。
 * 
 * 继承自 MyBatis-Plus 的 BaseMapper<OperationLog>，自动提供以下通用 CRUD 方法：
 * - insert(OperationLog entity)：新增一条操作日志记录
 * - deleteById(Serializable id)：根据主键ID删除操作日志
 * - delete(Wrapper<OperationLog> wrapper)：根据条件删除操作日志
 * - updateById(OperationLog entity)：根据主键ID更新操作日志（一般不修改日志）
 * - update(OperationLog entity, Wrapper<OperationLog> wrapper)：根据条件更新操作日志
 * - selectById(Serializable id)：根据日志ID查询日志详情
 * - selectBatchIds(Collection<? extends Serializable> idList)：根据日志ID批量查询日志
 * - selectOne(Wrapper<OperationLog> wrapper)：根据条件查询单条日志记录
 * - selectCount(Wrapper<OperationLog> wrapper)：根据条件统计日志数量
 * - selectList(Wrapper<OperationLog> wrapper)：根据条件查询日志列表
 * - selectPage(IPage<OperationLog> page, Wrapper<OperationLog> wrapper)：分页查询日志列表
 * 
 * @author CakeShop Team
 * @since 1.0.0
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}