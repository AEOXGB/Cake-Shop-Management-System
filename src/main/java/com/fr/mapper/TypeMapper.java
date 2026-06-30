package com.fr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.Type;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类数据访问接口
 * 对应数据库表：type
 * 
 * 功能描述：负责商品分类信息的增删改查操作，用于对蛋糕商品进行分类管理。
 * 主要业务场景：商品分类维护、按分类筛选商品、后台分类管理等。
 * 
 * 分类信息包含：分类ID、分类名称等。
 * 例如：生日蛋糕、慕斯蛋糕、芝士蛋糕、欧式蛋糕等分类。
 * 
 * 继承自 MyBatis-Plus 的 BaseMapper<Type>，自动提供以下通用 CRUD 方法：
 * - insert(Type entity)：新增一个商品分类
 * - deleteById(Serializable id)：根据主键ID删除商品分类
 * - delete(Wrapper<Type> wrapper)：根据条件删除商品分类
 * - updateById(Type entity)：根据主键ID更新分类信息
 * - update(Type entity, Wrapper<Type> wrapper)：根据条件更新分类信息
 * - selectById(Serializable id)：根据分类ID查询分类详情
 * - selectBatchIds(Collection<? extends Serializable> idList)：根据分类ID批量查询分类
 * - selectOne(Wrapper<Type> wrapper)：根据条件查询单条分类记录
 * - selectCount(Wrapper<Type> wrapper)：根据条件统计分类数量
 * - selectList(Wrapper<Type> wrapper)：查询全部分类列表
 * - selectPage(IPage<Type> page, Wrapper<Type> wrapper)：分页查询分类列表
 * 
 * @author CakeShop Team
 * @since 1.0.0
 */
@Mapper
public interface TypeMapper extends BaseMapper<Type> {
}
