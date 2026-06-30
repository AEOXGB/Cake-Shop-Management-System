package com.fr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口
 * 对应数据库表：user
 * 
 * 功能描述：负责用户信息的增删改查操作，包括普通用户和管理员账号的管理。
 * 主要业务场景：用户注册、登录验证、个人信息修改、账号状态管理、权限控制等。
 * 
 * 继承自 MyBatis-Plus 的 BaseMapper<User>，自动提供以下通用 CRUD 方法：
 * - insert(User entity)：插入一条用户记录
 * - deleteById(Serializable id)：根据主键ID删除用户
 * - delete(Wrapper<User> wrapper)：根据条件删除用户
 * - updateById(User entity)：根据主键ID更新用户信息
 * - update(User entity, Wrapper<User> wrapper)：根据条件更新用户信息
 * - selectById(Serializable id)：根据主键ID查询用户
 * - selectBatchIds(Collection<? extends Serializable> idList)：根据主键ID批量查询用户
 * - selectOne(Wrapper<User> wrapper)：根据条件查询单条用户记录
 * - selectCount(Wrapper<User> wrapper)：根据条件统计用户数量
 * - selectList(Wrapper<User> wrapper)：根据条件查询用户列表
 * - selectPage(IPage<User> page, Wrapper<User> wrapper)：分页查询用户列表
 * 
 * @author CakeShop Team
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {


}
