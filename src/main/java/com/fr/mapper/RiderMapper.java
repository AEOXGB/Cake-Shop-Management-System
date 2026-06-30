package com.fr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.Rider;
import org.apache.ibatis.annotations.Mapper;

/**
 * 骑手数据访问接口
 * 对应数据库表：rider
 * 
 * 功能描述：负责骑手账号信息的增删改查操作，包括骑手注册、登录验证、身份信息管理、账号状态管理等。
 * 主要业务场景：骑手入驻申请、骑手登录、个人信息维护、接单状态管理、骑手信息审核等。
 * 
 * 骑手信息包含：骑手ID、手机号、登录密码、姓名、头像、身份证号、账号状态、创建时间、更新时间等。
 * 骑手状态说明：0-离线/休息，1-在线/接单中，2-配送中等。
 * 
 * 继承自 MyBatis-Plus 的 BaseMapper<Rider>，自动提供以下通用 CRUD 方法：
 * - insert(Rider entity)：注册一个新骑手账号
 * - deleteById(Serializable id)：根据主键ID删除骑手账号
 * - delete(Wrapper<Rider> wrapper)：根据条件删除骑手账号
 * - updateById(Rider entity)：根据主键ID更新骑手信息
 * - update(Rider entity, Wrapper<Rider> wrapper)：根据条件更新骑手信息
 * - selectById(Serializable id)：根据骑手ID查询骑手详情
 * - selectBatchIds(Collection<? extends Serializable> idList)：根据骑手ID批量查询骑手
 * - selectOne(Wrapper<Rider> wrapper)：根据条件查询单条骑手记录（如根据手机号查询）
 * - selectCount(Wrapper<Rider> wrapper)：根据条件统计骑手数量
 * - selectList(Wrapper<Rider> wrapper)：根据条件查询骑手列表
 * - selectPage(IPage<Rider> page, Wrapper<Rider> wrapper)：分页查询骑手列表
 * 
 * @author CakeShop Team
 * @since 1.0.0
 */
@Mapper
public interface RiderMapper extends BaseMapper<Rider> {
}