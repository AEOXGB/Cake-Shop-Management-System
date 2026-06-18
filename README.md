# 蛋糕店管理系统

> 一个基于 Spring Boot 的蛋糕店全链路管理系统，包含用户端、管理员端和骑手端三大模块

## 🍰 项目简介

本项目是一个面向蛋糕店的综合管理系统，旨在提供完整的线上购物和配送解决方案。系统包含三个主要角色：

- **用户端**：商品浏览、购物车管理、订单下单、订单查询
- **管理员端**：商品管理、订单管理、用户管理、骑手管理、数据统计
- **骑手端**：订单接单、取货配送、收入明细、消息通知

## ✨ 功能列表

### 用户端功能
| 功能模块 | 功能描述 | 状态 |
| :--- | :--- | :---: |
| 商品浏览 | 首页商品展示、热销商品、新品上市 | ✅ |
| 商品详情 | 商品图片展示、价格、库存、描述 | ✅ |
| 购物车 | 添加商品、修改数量、删除商品 | ✅ |
| 订单管理 | 下单、支付、订单查询、取消订单 | ✅ |
| 用户中心 | 个人信息、修改密码 | ✅ |

### 管理员端功能
| 功能模块 | 功能描述 | 状态 |
| :--- | :--- | :---: |
| 商品管理 | 添加商品、编辑商品、删除商品、商品列表 | ✅ |
| 类别管理 | 添加类别、编辑类别、类别列表 | ✅ |
| 订单管理 | 订单列表、订单详情、订单佣金设置 | ✅ |
| 用户管理 | 用户列表、用户详情、编辑用户 | ✅ |
| 骑手管理 | 骑手审核、骑手列表、骑手状态管理 | ✅ |
| 数据统计 | 销售统计、用户统计、订单统计 | ✅ |
| 系统设置 | 修改密码、字体大小调整、夜间模式 | ✅ |
| 操作日志 | 记录系统操作日志 | ✅ |

### 骑手端功能
| 功能模块 | 功能描述 | 状态 |
| :--- | :--- | :---: |
| 订单管理 | 待接单、待取货、配送中、已完成 | ✅ |
| 订单详情 | 订单信息、商品列表、佣金查看 | ✅ |
| 接单配送 | 接单、取货、配送、完成订单 | ✅ |
| 收入明细 | 收入统计、订单佣金明细 | ✅ |
| 消息通知 | 新订单通知、新商品通知 | ✅ |
| 个人中心 | 个人信息、设置入口 | ✅ |
| 系统设置 | 修改密码、夜间模式、字体调整 | ✅ |

## 🛠 技术栈

### 后端技术
| 技术 | 版本 | 说明 |
| :--- | :--- | :--- |
| Java | 8+ | 开发语言 |
| Spring Boot | 2.2.6.RELEASE | 后端框架 |
| MyBatis Plus | 3.5.0 | ORM框架 |
| MySQL | 8.0.19 | 数据库 |
| Thymeleaf | 3.0.11 | 模板引擎 |
| ZUI | 1.9.x | 前端组件库 |

### 前端技术
| 技术 | 说明 |
| :--- | :--- |
| HTML5 | 页面结构 |
| CSS3 | 样式设计 |
| JavaScript | 交互逻辑 |
| jQuery | DOM操作 |
| ECharts | 数据可视化 |
| LocalStorage | 本地存储 |

## 📁 项目结构

```
CakeShop/
├── src/main/java/com/fr/
│   ├── controller/          # 控制器层
│   │   ├── AdminController.java    # 管理员控制器
│   │   ├── UserController.java     # 用户控制器
│   │   ├── RiderController.java    # 骑手控制器
│   │   ├── GoodsController.java    # 商品控制器
│   │   ├── OrderController.java    # 订单控制器
│   │   └── ...
│   ├── service/             # 服务层
│   │   ├── UserService.java
│   │   ├── GoodsService.java
│   │   └── ...
│   ├── mapper/              # 数据访问层
│   │   ├── UserMapper.java
│   │   ├── GoodsMapper.java
│   │   └── ...
│   ├── javaBean/            # 实体类
│   │   ├── User.java
│   │   ├── Goods.java
│   │   ├── Order.java
│   │   ├── Rider.java
│   │   └── ...
│   ├── config/              # 配置类
│   │   ├── MyMVCConfig.java
│   │   ├── CustomInterceptor.java
│   │   └── ...
│   └── CakeShopApplication.java    # 启动类
├── src/main/resources/
│   ├── templates/           # 前端模板
│   │   ├── admin/           # 管理员页面
│   │   ├── rider/           # 骑手页面
│   │   └── *.html           # 用户端页面
│   ├── static/              # 静态资源
│   │   ├── css/
│   │   ├── js/
│   │   └── images/
│   └── application.yml      # 应用配置
├── picture/                 # 商品图片存储
├── pom.xml                  # Maven配置
└── README.md                # 项目说明
```

## 🚀 快速开始

### 环境要求
- JDK 8+
- Maven 3.6+
- MySQL 8.0+

### 数据库配置

1. 创建数据库 `cakeshop`
2. 修改 `src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cakeshop?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 启动项目

```bash
# 进入项目目录
cd CakeShop

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

### 访问地址

| 页面 | 地址 | 账号 | 密码 |
| :--- | :--- | :--- | :--- |
| 用户首页 | http://localhost:8080/ | - | - |
| 用户登录 | http://localhost:8080/login | user | 123456 |
| 管理员首页 | http://localhost:8080/admin | admin | admin |
| 骑手首页 | http://localhost:8080/rider | rider | 123456 |

## 📊 数据库表结构

### 核心数据表

| 表名 | 说明 | 主要字段 |
| :--- | :--- | :--- |
| `user` | 用户表 | id, username, password, phone, address |
| `goods` | 商品表 | id, name, price, cover, stock, type_id |
| `type` | 类别表 | id, name, description |
| `order` | 订单表 | id, user_id, total_price, status, rider_id, commission |
| `order_item` | 订单项表 | id, order_id, goods_id, quantity, price |
| `rider` | 骑手表 | id, username, password, phone, id_card, status |
| `notification` | 通知表 | id, rider_id, title, content, type, order_id |
| `operation_log` | 操作日志表 | id, operator, operation, time |

## 📈 需求迭代

### V1.0 基础版本 ✅
- 用户端：商品浏览、购物车、订单下单
- 管理员端：商品管理、订单管理、用户管理
- 数据库设计与基础架构搭建

### V2.0 骑手端 ✅
- 骑手注册与审核流程
- 骑手订单管理（接单、取货、配送）
- 骑手收入明细
- 消息通知系统

### V3.0 数据可视化 ✅
- 管理员端数据统计页面
- 骑手端首页数据图表
- 订单状态分布、销售趋势分析

### V4.0 用户体验优化 ✅
- 夜间模式切换
- 字体大小调整
- 商品图片展示优化
- 网页标签图标配置

## 🔧 开发说明

### 代码规范
- 使用 UTF-8 编码
- 遵循 Java 命名规范
- 方法和类添加中文注释
- 数据库字段使用下划线命名

### 安全规范
- 密码加密存储（MD5）
- SQL 注入防护（MyBatis 参数化查询）
- 权限控制（拦截器验证）
- 敏感信息脱敏处理

## 📝 项目推广计划

### 第一阶段：内部测试
- 完成功能测试和 Bug 修复
- 内部员工试用反馈收集
- 性能优化和稳定性提升

### 第二阶段：小规模推广
- 面向本地蛋糕店进行试点
- 收集商家反馈和需求
- 根据反馈进行功能迭代

### 第三阶段：全面推广
- 完善商家入驻流程
- 开发移动端适配
- 扩展支付方式（微信支付、支付宝）
- 增加营销功能（优惠券、满减活动）

### 第四阶段：生态扩展
- 多店铺支持
- 供应链管理
- 数据分析报表
- 移动端 App 开发

## 📄 许可证

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📞 联系方式

如有问题或建议，请联系：
- 邮箱：support@cakeshop.com
- 项目地址：https://github.com/xxx/CakeShop

---

*Made with ❤️ for Cake Shop Management*
