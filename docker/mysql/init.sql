/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : cookieshop

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2026-06-26 00:52:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `good_id` varchar(255) NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `intro` varchar(255) NOT NULL,
  `amount` int(255) DEFAULT NULL,
  `price` float(10,2) DEFAULT NULL,
  `total_price` float(10,2) DEFAULT NULL,
  `cover` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=154 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES ('63', '9', 'admin', '甜郁草莓配合冰淇淋的丝滑口感,让清爽与浪漫在爱情果园激情碰撞,恋上草莓,这份心情,美妙非凡.\r\n主口味:草莓口味 主要原料:草莓果溶 草莓  甜度:三星（满五星） 最佳食用温度：-12至-15摄氏度', '5', '299.00', '1495.00', '/picture/9-1.jpg');
INSERT INTO `cart` VALUES ('64', '13', 'admin', '蛋黄与蜂蜜,淡奶油共同演绎的曼妙之旅.口感Q糯浓郁,回味绵软柔长.皱巴巴的造型,甜蜜蜜的感受.', '1', '36.00', '36.00', '/picture/13-1.jpg');
INSERT INTO `cart` VALUES ('65', '10', 'vili', '优选法国芝士,奶香浓郁,质地柔滑,口感细腻.法国芝士有助于提升糕点的整体口感,完美平衡酸度与甜味,制作出的糕点更加洁白纯美.', '1', '28.00', '28.00', '/picture/10-1.jpg');
INSERT INTO `cart` VALUES ('124', '2', 'a', '意大利芝士饼干', '6', '39.00', '234.00', '/picture/14-1.jpg');
INSERT INTO `cart` VALUES ('125', '16', 'a', '草莓冰淇淋', '1', '299.00', '299.00', '/picture/9-1.jpg');
INSERT INTO `cart` VALUES ('153', '2', 's', '意大利芝士饼干', '1', '39.00', '39.00', '/picture/14-1.jpg');

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `cover` varchar(45) DEFAULT NULL,
  `image1` varchar(45) DEFAULT NULL,
  `image2` varchar(45) DEFAULT NULL,
  `price` float DEFAULT NULL,
  `intro` varchar(300) DEFAULT NULL,
  `stock` int(11) DEFAULT NULL,
  `type_id` int(11) DEFAULT NULL,
  `daytime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_type_id_idx` (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES ('2', '意大利芝士饼干', '/picture/14-1.jpg', '/picture/14-1.jpg', '/picture/14-1.jpg', '39', '采用帕玛森芝士为主要原材料制作的意大利芝士饼,奶香浓郁,鲜香可口.', '35', '0', '2026-06-10 11:12:17');
INSERT INTO `goods` VALUES ('9', '草莓冰淇淋', '/picture/9-1.jpg', '/picture/9-2.jpg', '/picture/9-3.jpg', '299', '甜郁草莓配合冰淇淋的丝滑口感,让清爽与浪漫在爱情果园激情碰撞,恋上草莓,这份心情,美妙非凡.\r\n主口味:草莓口味 主要原料:草莓果溶 草莓  甜度:三星（满五星） 最佳食用温度：-12至-15摄氏度', '42', '0', '2026-06-10 11:12:23');
INSERT INTO `goods` VALUES ('10', '玫瑰舒芙蕾', '/picture/10-1.jpg', '/picture/10-2.jpg', '/picture/10-3.jpg', '28', '优选法国芝士,奶香浓郁,质地柔滑,口感细腻.法国芝士有助于提升糕点的整体口感,完美平衡酸度与甜味,制作出的糕点更加洁白纯美.', '193', '3', '2026-06-10 11:12:29');
INSERT INTO `goods` VALUES ('11', '半熟芝士', '/picture/11-1.jpg', '/picture/11-1.jpg', '/picture/11-1.jpg', '38', '为了保证芝士的香醇,半熟芝士借鉴日本温泉煮鸡蛋的做法,把芝士,牛奶,鸡蛋,天然奶油,砂糖,小麦粉拌成面糊,通过水浴蒸烤,保证芝士蛋糕细嫩,柔软,留住芝士的香醇细滑.', '93', '0', '2026-06-10 11:13:18');
INSERT INTO `goods` VALUES ('12', '青森芝士条', '/picture/p1780638506346_0.png', '/picture/1-2.jpg', '/picture/12-1.jpg', '36', '青森芝士和风轻拂,,奶香浓郁,质地柔滑,口感细腻.', '93', '1', '2026-06-05 13:48:26');
INSERT INTO `goods` VALUES ('13', '蜂蜜蛋糕', '/picture/13-1.jpg', '/picture/13-1.jpg', '/picture/13-1.jpg', '36', '蛋黄与蜂蜜,淡奶油共同演绎的曼妙之旅.口感Q糯浓郁,回味绵软柔长.皱巴巴的造型,甜蜜蜜的感受.', '95', '3', '2026-05-04 22:28:17');
INSERT INTO `goods` VALUES ('14', '意大利芝士饼干', '/picture/14-1.jpg', '/picture/14-1.jpg', '/picture/14-1.jpg', '39', '采用帕玛森芝士为主要原材料制作的意大利芝士饼,奶香浓郁,鲜香可口.', '95', '0', '2026-05-05 22:28:21');
INSERT INTO `goods` VALUES ('15', '小熊乐园', '/picture/8-1.jpg', '/picture/8-2.jpg', '/picture/8-3.jpg', '299', '走进小熊乐园,与可爱的小熊一起准备过冬的食物吧,摘颗草莓藏放在巧克力做的房子里,好朋友一起分享劳动的果实.\r\n主口味:草莓奶油味 主要原料:乳脂奶油,纯巧克力,朗姆酒,幼砂糖,鲜草莓 甜度:二星（满五星） 最佳食用温度：5-7摄氏度', '92', '2', '2026-05-15 22:28:26');
INSERT INTO `goods` VALUES ('16', '草莓冰淇淋', '/picture/9-1.jpg', '/picture/9-2.jpg', '/picture/9-3.jpg', '299', '甜郁草莓配合冰淇淋的丝滑口感,让清爽与浪漫在爱情果园激情碰撞,恋上草莓,这份心情,美妙非凡.\r\n主口味:草莓口味 主要原料:草莓果溶 草莓  甜度:三星（满五星） 最佳食用温度：-12至-15摄氏度', '97', '3', '2026-05-23 22:28:31');
INSERT INTO `goods` VALUES ('18', '半熟芝士', '/picture/11-1.jpg', '/picture/11-1.jpg', '/picture/11-1.jpg', '38', '为了保证芝士的香醇,半熟芝士借鉴日本温泉煮鸡蛋的做法,把芝士,牛奶,鸡蛋,天然奶油,砂糖,小麦粉拌成面糊,通过水浴蒸烤,保证芝士蛋糕细嫩,柔软,留住芝士的香醇细滑.', '98', '2', '2026-05-14 22:28:37');
INSERT INTO `goods` VALUES ('19', '青森芝士条', '/picture/12-1.jpg', '/picture/1-2.jpg', '/picture/12-1.jpg', '36', '青森芝士和风轻拂,,奶香浓郁,质地柔滑,口感细腻.', '96', '4', '2026-05-05 22:28:41');
INSERT INTO `goods` VALUES ('20', '蜂蜜蛋糕', '/picture/13-1.jpg', '/picture/13-1.jpg', '/picture/13-1.jpg', '36', '蛋黄与蜂蜜,淡奶油共同演绎的曼妙之旅.口感Q糯浓郁,回味绵软柔长.皱巴巴的造型,甜蜜蜜的感受.', '98', '0', '2026-05-18 22:28:45');
INSERT INTO `goods` VALUES ('21', '意大利芝士饼干', '/picture/14-1.jpg', '/picture/14-1.jpg', '/picture/14-1.jpg', '39', '采用帕玛森芝士为主要原材料制作的意大利芝士饼,奶香浓郁,鲜香可口.', '108', '2', '2026-05-25 22:28:49');
INSERT INTO `goods` VALUES ('22', '1', '/picture/p1780638529566_1.png', '/picture/p1780638529567_2.png', '/picture/p1780638529569_3.png', '111', 'asdasdad', '231', '0', '2026-06-05 13:48:49');
INSERT INTO `goods` VALUES ('23', '3', '/picture/p1780638561007_4.png', '/picture/p1781661359503_0.png', '/picture/p1780638561009_6.png', '13', 'aaaaaaaaa', '21', '1', '2026-06-17 09:55:59');
INSERT INTO `goods` VALUES ('24', '大', '/picture/p1780638586203_7.png', '/picture/p1780638586204_8.png', '/picture/p1780638586205_9.png', '113', 'ssssss', '50', '1', '2026-06-10 11:12:07');

-- ----------------------------
-- Table structure for month_table
-- ----------------------------
DROP TABLE IF EXISTS `month_table`;
CREATE TABLE `month_table` (
  `MONTH_NUM` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of month_table
-- ----------------------------
INSERT INTO `month_table` VALUES ('1');
INSERT INTO `month_table` VALUES ('2');
INSERT INTO `month_table` VALUES ('3');
INSERT INTO `month_table` VALUES ('4');
INSERT INTO `month_table` VALUES ('5');
INSERT INTO `month_table` VALUES ('6');
INSERT INTO `month_table` VALUES ('7');
INSERT INTO `month_table` VALUES ('8');
INSERT INTO `month_table` VALUES ('9');
INSERT INTO `month_table` VALUES ('10');
INSERT INTO `month_table` VALUES ('11');
INSERT INTO `month_table` VALUES ('12');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `rider_id` int(11) DEFAULT NULL COMMENT '骑手ID',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知标题',
  `content` text COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知内容',
  `type` enum('order','system','income') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知类型：订单/系统/收入',
  `order_id` bigint(20) DEFAULT NULL COMMENT '关联订单ID',
  `is_read` tinyint(4) DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='骑手通知表';

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES ('1', '1', '新订单待接单', '有新订单需要配送，订单号：#1781662608906，金额：¥39.00', 'order', '1781662608906', '0', '2026-06-17 10:16:48');
INSERT INTO `notification` VALUES ('2', '2', '新订单待接单', '有新订单需要配送，订单号：#1781662608906，金额：¥39.00', 'order', '1781662608906', '0', '2026-06-17 10:16:48');
INSERT INTO `notification` VALUES ('3', '4', '新订单待接单', '有新订单需要配送，订单号：#1781662608906，金额：¥39.00', 'order', '1781662608906', '0', '2026-06-17 10:16:48');
INSERT INTO `notification` VALUES ('4', '5', '新订单待接单', '有新订单需要配送，订单号：#1781662608906，金额：¥39.00', 'order', '1781662608906', '0', '2026-06-17 10:16:48');
INSERT INTO `notification` VALUES ('5', '6', '新订单待接单', '有新订单需要配送，订单号：#1781662608906，金额：¥39.00', 'order', '1781662608906', '0', '2026-06-17 10:16:48');
INSERT INTO `notification` VALUES ('6', '7', '新订单待接单', '有新订单需要配送，订单号：#1781662608906，金额：¥39.00', 'order', '1781662608906', '0', '2026-06-17 10:16:48');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(100) DEFAULT NULL COMMENT '操作人用户名',
  `operation` varchar(500) DEFAULT NULL COMMENT '操作描述',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` text DEFAULT NULL COMMENT '请求参数',
  `ip` varchar(50) DEFAULT NULL COMMENT '请求IP地址',
  `create_time` datetime DEFAULT current_timestamp() COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=845 DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES ('1', 'a', '保存商品', 'AdminController.saveGoods', 'id=&name=ssasass&type_id=8&price=11&stock=11&intro=asdasdad', '0:0:0:0:0:0:0:1', '2026-06-08 01:31:34');
INSERT INTO `operation_log` VALUES ('2', 'a', '修改密码', 'AdminController.changePassword', 'oldPassword=1&newPassword=123456&confirmPassword=123456', '0:0:0:0:0:0:0:1', '2026-06-08 01:32:12');
INSERT INTO `operation_log` VALUES ('3', 'a', '修改密码', 'AdminController.changePassword', 'oldPassword=123456&newPassword=1234567&confirmPassword=1234567', '0:0:0:0:0:0:0:1', '2026-06-08 01:40:54');
INSERT INTO `operation_log` VALUES ('4', 'a', '执行操作: categoryList', 'AdminController.categoryList', '', '0:0:0:0:0:0:0:1', '2026-06-08 01:41:25');
INSERT INTO `operation_log` VALUES ('5', 'a', '执行操作: categoryList', 'AdminController.categoryList', '', '0:0:0:0:0:0:0:1', '2026-06-08 01:50:06');
INSERT INTO `operation_log` VALUES ('6', 'a', '执行操作: categoryList', 'AdminController.categoryList', '', '0:0:0:0:0:0:0:1', '2026-06-08 01:50:30');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` varchar(255) NOT NULL,
  `total` float(20,0) DEFAULT NULL,
  `amount` int(6) DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '2:已付款 3:已发货 4:已完成',
  `paytype` int(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `datetime` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `rider_id` int(11) DEFAULT NULL COMMENT '骑手ID',
  `pickup_time` varchar(20) DEFAULT NULL COMMENT '取货时间',
  `delivery_time` varchar(20) DEFAULT NULL COMMENT '配送时间',
  `complete_time` varchar(20) DEFAULT NULL COMMENT '完成时间',
  `commission` decimal(10,2) DEFAULT NULL COMMENT '佣金金额',
  `evaluation` text DEFAULT NULL COMMENT '用户评价',
  `rating` int(11) DEFAULT NULL COMMENT '评分（1-5）',
  `createtime` datetime DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `fk_user_id_idx` (`user_id`),
  KEY `idx_rider_id` (`rider_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES ('1690440734978', '1196', '4', '4', '1', '管理员', '1333333333', '中华人民共和国', '2023-07-27 14:52:14', '1', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1690441025870', '355', '3', '4', '2', '管理员', '1333333333', '中华人民共和国', '2023-07-27 14:57:05', '1', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1699936968220', '5522', '23', '4', '1', 'vili', '1344444444', '中华人民共和国', '2023-8-14 12:42:48', '2', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1712835005129', '1495', '5', '2', '1', 'vili', '1344444444', '中华人民共和国', '2024-04-11', '2', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1712839550540', '1495', '5', '2', '1', 'vili', '1344444444', '中华人民共和国', '2024-04-11 20:45:50', '2', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1713147394614', '299', '1', '2', '1', 'vili', '1344444444', '中华人民共和国', '2024-04-15 10:16:34', '2', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1713147598081', '28', '1', '2', '2', 'vili', '1344444444', '中华人民共和国', '2024-04-15 10:19:58', '2', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1713147745528', '299', '1', '2', '1', 'vili', '1344444444', '中华人民共和国', '2024-04-15 10:22:25', '2', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1713159946825', '299', '1', '2', '1', 'vili', '1344444444', '中华人民共和国', '2024-04-15 13:45:46', '2', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1713165647839', '897', '3', '2', '1', 'vili', '1344444444', '中华人民共和国', '2024-04-15 15:20:47', '2', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1716531956580', '4158', '28', '4', '1', '123', '123', '123', '2024-05-24 14:25:56', '62', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1716535756003', '3915', '7', '4', '1', '管理员', '1333333333', '中华人民共和国', '2024-05-24 15:29:16', '1', '6', '2026-06-12 15:56:09', '2026-06-17 09:04:09', '2026-06-17 09:04:13', null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1717145845959', '4242', '9', '2', '2', '管理员', '1333333333', '中华人民共和国', '2024-05-31 16:57:25', '1', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1717145852401', '2121', '1', '2', '1', '管理员', '1333333333', '中华人民共和国', '2024-05-31 16:57:32', '1', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1717145860936', '1858', '1', '2', '1', '管理员', '1333333333', '中华人民共和国', '2024-05-31 16:57:40', '1', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1730035967488', '327', '2', '4', '1', '123', '123', '123', '2024-10-27 21:32:47', '62', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1732501072252', '3137', '14', '4', '1', '123', '123', '123', '2024-11-25 10:17:52', '62', '6', '2026-06-12 16:16:20', '2026-06-12 17:00:59', '2026-06-12 17:01:01', null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1732501143472', '299', '1', '2', '1', '123', '123', '123', '2024-11-25 10:19:03', '62', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1774709963411', '2813', '13', '2', '1', '管理员', '1333333333', '中华人民共和国', '2026-03-28 22:59:23', '1', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1774710077367', '1587', '1', '2', '1', '管理员', '1333333333', '中华人民共和国', '2026-03-28 23:01:17', '1', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1774710431836', '1587', '1', '2', '1', '管理员', '1333333333', '中华人民共和国', '2026-03-28 23:07:11', '1', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1776352559452', '1645', '3', '2', '1', '123', '123', '123', '2026-04-16 23:15:59', '62', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1776352643383', '1609', '2', '2', '1', '123', '123', '123', '2026-04-16 23:17:23', '62', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779433408836', '153', '4', '2', '1', 'a', '138000000001', 's', '2026-05-22 07:03:28', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779434039887', '0', '5', '2', '1', 'a', 'sas', 'asas', '2026-05-22 15:13:59', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779434494853', '0', '1', '2', '1', 'a', 'sas', 'asas', '2026-05-22 15:21:34', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779434769945', '0', '1', '2', '1', 'a', 'sas', 'asas', '2026-05-22 15:26:09', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779435133639', '377', '3', '2', '1', 'a', 'sas', 'asas', '2026-05-22 15:32:13', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779435427751', '39', '1', '4', '1', 'a', 'sas', 'asas', '2026-05-22 15:37:07', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779435830872', '377', '3', '4', '1', 'a1', '11111111111111111', 'asas', '2026-05-22 15:43:50', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779845316338', '637', '3', '4', '1', 'a', 'sas', 'asas', '2026-05-27 09:28:36', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779846077558', '416', '4', '4', '3', 'a', 'sas', 'asas', '2026-05-27 09:41:17', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779891457599', '78', '2', '5', '1', 'a', 'sas', 'asas', '2026-05-27 22:17:37', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779891469857', '78', '2', '2', '3', 'a', 'sas', 'asas', '2026-05-27 22:17:49', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1779988659238', '517', '7', '2', '1', 'a', 'sas', 'asas', '2026-05-29 01:17:39', '70', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1780040567696', '39', '1', '4', '1', 's', 'sasssss', 'asas234', '2026-05-29 15:42:47', '72', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1780452693052', '449', '3', '5', '1', 's', 'sasssss', 'asas234', '2026-06-03 10:11:33', '72', null, null, null, null, null, null, null, '2026-06-05 14:12:09');
INSERT INTO `order` VALUES ('1780881365188', '451', '3', '5', '1', 's', 'sasssss', 'asas234', '2026-06-08 09:16:05', '72', null, null, null, null, null, null, null, '2026-06-08 09:16:05');
INSERT INTO `order` VALUES ('1781051424806', '404', '4', '2', '1', 's', 'sasssss', 'asas234', '2026-06-10 08:30:24', '72', null, null, null, null, '20.00', null, null, '2026-06-10 08:30:24');
INSERT INTO `order` VALUES ('1781051809910', '488', '4', '4', '1', 's', 'sasssss', 'asas234', '2026-06-10 08:36:49', '72', null, null, null, null, null, null, null, '2026-06-10 08:36:49');
INSERT INTO `order` VALUES ('1781251364483', '676', '7', '2', '1', 's', 'sasssss', 'asas234', '2026-06-12 16:02:44', '72', null, null, null, null, '70.00', null, null, '2026-06-12 16:02:44');
INSERT INTO `order` VALUES ('1781251614510', '39', '1', '4', '1', 's', 'sasssss', 'asas234', '2026-06-12 16:06:54', '72', '6', '2026-06-12 16:13:22', '2026-06-12 16:18:51', '2026-06-12 16:30:24', '45.00', null, null, '2026-06-12 16:06:54');
INSERT INTO `order` VALUES ('1781254888039', '338', '2', '3', '1', 's', 'sasssss', 'asas234', '2026-06-12 17:01:28', '72', null, null, null, null, '12.00', null, null, '2026-06-12 17:01:28');
INSERT INTO `order` VALUES ('1781662097749', '337', '2', '3', '1', 's', 'sasssss', 'asas234', '2026-06-17 10:08:17', '72', null, null, null, null, '1000.00', null, null, '2026-06-17 10:08:17');
INSERT INTO `order` VALUES ('1781662371072', '338', '2', '3', '1', 's', 'sasssss', 'asas234', '2026-06-17 10:12:51', '72', null, null, null, null, '11.00', null, null, '2026-06-17 10:12:51');
INSERT INTO `order` VALUES ('1781662608906', '39', '1', '4', '1', 's', 'sasssss', 'asas234', '2026-06-17 10:16:48', '72', '6', '2026-06-17 10:28:28', '2026-06-17 10:28:43', '2026-06-17 10:28:45', '11.00', null, null, '2026-06-17 10:16:48');

-- ----------------------------
-- Table structure for orderitem
-- ----------------------------
DROP TABLE IF EXISTS `orderitem`;
CREATE TABLE `orderitem` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `price` float DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `goods_id` int(11) DEFAULT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_order_id_idx` (`order_id`),
  KEY `fk_orderitem_goods_id_idx` (`goods_id`),
  CONSTRAINT `fk_order_id` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_orderitem_goods_id` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=212 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of orderitem
-- ----------------------------
INSERT INTO `orderitem` VALUES ('108', '299', '4', '9', '1690440734978');
INSERT INTO `orderitem` VALUES ('109', '299', '1', '9', '1690441025870');
INSERT INTO `orderitem` VALUES ('110', '28', '2', '10', '1690441025870');
INSERT INTO `orderitem` VALUES ('111', '299', '14', '9', '1699936968220');
INSERT INTO `orderitem` VALUES ('112', '299', '4', '16', '1699936968220');
INSERT INTO `orderitem` VALUES ('113', '28', '5', '10', '1699936968220');
INSERT INTO `orderitem` VALUES ('114', '299', '4', '9', '1712839550540');
INSERT INTO `orderitem` VALUES ('115', '299', '1', '15', '1712839550540');
INSERT INTO `orderitem` VALUES ('116', '299', '1', '9', '1713147394614');
INSERT INTO `orderitem` VALUES ('117', '28', '1', '10', '1713147598081');
INSERT INTO `orderitem` VALUES ('118', '299', '1', '9', '1713147745528');
INSERT INTO `orderitem` VALUES ('119', '299', '1', '9', '1713159946825');
INSERT INTO `orderitem` VALUES ('120', '299', '3', '9', '1713165647839');
INSERT INTO `orderitem` VALUES ('121', '36', '4', '12', '1716531956580');
INSERT INTO `orderitem` VALUES ('122', '299', '4', '15', '1716531956580');
INSERT INTO `orderitem` VALUES ('123', '28', '3', '10', '1716531956580');
INSERT INTO `orderitem` VALUES ('124', '38', '2', '11', '1716531956580');
INSERT INTO `orderitem` VALUES ('125', '39', '3', '21', '1716531956580');
INSERT INTO `orderitem` VALUES ('126', '36', '1', '13', '1716531956580');
INSERT INTO `orderitem` VALUES ('127', '39', '1', '14', '1716531956580');
INSERT INTO `orderitem` VALUES ('128', '299', '1', '16', '1716531956580');
INSERT INTO `orderitem` VALUES ('129', '36', '1', '19', '1716531956580');
INSERT INTO `orderitem` VALUES ('130', '38', '1', '18', '1716531956580');
INSERT INTO `orderitem` VALUES ('131', '299', '7', '9', '1716531956580');
INSERT INTO `orderitem` VALUES ('132', '299', '7', '9', '1716535756003');
INSERT INTO `orderitem` VALUES ('133', '299', '3', '15', '1717145845959');
INSERT INTO `orderitem` VALUES ('134', '299', '5', '9', '1717145845959');
INSERT INTO `orderitem` VALUES ('135', '28', '1', '10', '1717145845959');
INSERT INTO `orderitem` VALUES ('136', '299', '1', '9', '1717145852401');
INSERT INTO `orderitem` VALUES ('137', '36', '1', '12', '1717145860936');
INSERT INTO `orderitem` VALUES ('138', '299', '1', '9', '1730035967488');
INSERT INTO `orderitem` VALUES ('139', '28', '1', '10', '1730035967488');
INSERT INTO `orderitem` VALUES ('140', '299', '9', '9', '1732501072252');
INSERT INTO `orderitem` VALUES ('141', '36', '1', '13', '1732501072252');
INSERT INTO `orderitem` VALUES ('142', '299', '1', '16', '1732501072252');
INSERT INTO `orderitem` VALUES ('143', '36', '2', '19', '1732501072252');
INSERT INTO `orderitem` VALUES ('144', '39', '1', '21', '1732501072252');
INSERT INTO `orderitem` VALUES ('145', '299', '1', '9', '1732501143472');
INSERT INTO `orderitem` VALUES ('146', '28', '1', '10', '1774709963411');
INSERT INTO `orderitem` VALUES ('147', '39', '2', '14', '1774709963411');
INSERT INTO `orderitem` VALUES ('148', '38', '1', '11', '1774709963411');
INSERT INTO `orderitem` VALUES ('149', '36', '1', '13', '1774709963411');
INSERT INTO `orderitem` VALUES ('150', '299', '2', '15', '1774709963411');
INSERT INTO `orderitem` VALUES ('151', '39', '1', '21', '1774709963411');
INSERT INTO `orderitem` VALUES ('152', '36', '1', '20', '1774709963411');
INSERT INTO `orderitem` VALUES ('153', '36', '1', '19', '1774709963411');
INSERT INTO `orderitem` VALUES ('154', '38', '1', '18', '1774709963411');
INSERT INTO `orderitem` VALUES ('155', '299', '2', '9', '1774709963411');
INSERT INTO `orderitem` VALUES ('156', '299', '1', '9', '1774710077367');
INSERT INTO `orderitem` VALUES ('157', '299', '1', '9', '1774710431836');
INSERT INTO `orderitem` VALUES ('158', '36', '1', '12', '1776352559452');
INSERT INTO `orderitem` VALUES ('159', '39', '2', '2', '1776352559452');
INSERT INTO `orderitem` VALUES ('160', '39', '2', '2', '1776352643383');
INSERT INTO `orderitem` VALUES ('161', '39', '4', '2', '1779434039887');
INSERT INTO `orderitem` VALUES ('162', '36', '1', '13', '1779434039887');
INSERT INTO `orderitem` VALUES ('163', '39', '1', '2', '1779434494853');
INSERT INTO `orderitem` VALUES ('164', '39', '1', '2', '1779434769945');
INSERT INTO `orderitem` VALUES ('165', '39', '2', '2', '1779435133639');
INSERT INTO `orderitem` VALUES ('166', '299', '1', '9', '1779435133639');
INSERT INTO `orderitem` VALUES ('167', '39', '1', '2', '1779435427751');
INSERT INTO `orderitem` VALUES ('168', '39', '2', '2', '1779435830872');
INSERT INTO `orderitem` VALUES ('169', '299', '1', '9', '1779435830872');
INSERT INTO `orderitem` VALUES ('170', '299', '1', '9', '1779845316338');
INSERT INTO `orderitem` VALUES ('171', '299', '1', '15', '1779845316338');
INSERT INTO `orderitem` VALUES ('172', '39', '1', '2', '1779845316338');
INSERT INTO `orderitem` VALUES ('173', '39', '1', '2', '1779846077558');
INSERT INTO `orderitem` VALUES ('174', '39', '2', '2', '1779846077558');
INSERT INTO `orderitem` VALUES ('175', '299', '1', '9', '1779846077558');
INSERT INTO `orderitem` VALUES ('176', '39', '2', '2', '1779891457599');
INSERT INTO `orderitem` VALUES ('177', '39', '2', '2', '1779891469857');
INSERT INTO `orderitem` VALUES ('178', '39', '1', '2', '1779988659238');
INSERT INTO `orderitem` VALUES ('179', '299', '1', '9', '1779988659238');
INSERT INTO `orderitem` VALUES ('180', '28', '1', '10', '1779988659238');
INSERT INTO `orderitem` VALUES ('181', '38', '2', '11', '1779988659238');
INSERT INTO `orderitem` VALUES ('182', '36', '1', '12', '1779988659238');
INSERT INTO `orderitem` VALUES ('183', '39', '1', '14', '1779988659238');
INSERT INTO `orderitem` VALUES ('184', '39', '1', '2', '1780040567696');
INSERT INTO `orderitem` VALUES ('185', '39', '1', '2', '1780452693052');
INSERT INTO `orderitem` VALUES ('186', '299', '1', '9', '1780452693052');
INSERT INTO `orderitem` VALUES ('187', '111', '1', '22', '1780452693052');
INSERT INTO `orderitem` VALUES ('188', '113', '1', '24', '1780881365188');
INSERT INTO `orderitem` VALUES ('189', '299', '1', '9', '1780881365188');
INSERT INTO `orderitem` VALUES ('190', '39', '1', '2', '1780881365188');
INSERT INTO `orderitem` VALUES ('191', '39', '1', '2', '1781051424806');
INSERT INTO `orderitem` VALUES ('192', '299', '1', '9', '1781051424806');
INSERT INTO `orderitem` VALUES ('193', '28', '1', '10', '1781051424806');
INSERT INTO `orderitem` VALUES ('194', '38', '1', '11', '1781051424806');
INSERT INTO `orderitem` VALUES ('195', '39', '2', '2', '1781051809910');
INSERT INTO `orderitem` VALUES ('196', '299', '1', '9', '1781051809910');
INSERT INTO `orderitem` VALUES ('197', '111', '1', '22', '1781051809910');
INSERT INTO `orderitem` VALUES ('198', '113', '1', '24', '1781251364483');
INSERT INTO `orderitem` VALUES ('199', '299', '1', '9', '1781251364483');
INSERT INTO `orderitem` VALUES ('200', '39', '2', '2', '1781251364483');
INSERT INTO `orderitem` VALUES ('201', '39', '1', '14', '1781251364483');
INSERT INTO `orderitem` VALUES ('202', '36', '1', '20', '1781251364483');
INSERT INTO `orderitem` VALUES ('203', '111', '1', '22', '1781251364483');
INSERT INTO `orderitem` VALUES ('204', '39', '1', '2', '1781251614510');
INSERT INTO `orderitem` VALUES ('205', '39', '1', '2', '1781254888039');
INSERT INTO `orderitem` VALUES ('206', '299', '1', '9', '1781254888039');
INSERT INTO `orderitem` VALUES ('207', '38', '1', '11', '1781662097749');
INSERT INTO `orderitem` VALUES ('208', '299', '1', '9', '1781662097749');
INSERT INTO `orderitem` VALUES ('209', '39', '1', '2', '1781662371072');
INSERT INTO `orderitem` VALUES ('210', '299', '1', '9', '1781662371072');
INSERT INTO `orderitem` VALUES ('211', '39', '1', '2', '1781662608906');

-- ----------------------------
-- Table structure for recommend
-- ----------------------------
DROP TABLE IF EXISTS `recommend`;
CREATE TABLE `recommend` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) DEFAULT NULL,
  `goods_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_goods_id_idx` (`goods_id`),
  CONSTRAINT `fk_goods_id` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of recommend
-- ----------------------------
INSERT INTO `recommend` VALUES ('9', '2', '9');
INSERT INTO `recommend` VALUES ('10', '3', '10');
INSERT INTO `recommend` VALUES ('11', '3', '12');
INSERT INTO `recommend` VALUES ('12', '3', '13');
INSERT INTO `recommend` VALUES ('13', '3', '14');
INSERT INTO `recommend` VALUES ('14', '3', '15');
INSERT INTO `recommend` VALUES ('15', '3', '16');
INSERT INTO `recommend` VALUES ('17', '3', '18');
INSERT INTO `recommend` VALUES ('18', '3', '19');
INSERT INTO `recommend` VALUES ('33', '2', '10');
INSERT INTO `recommend` VALUES ('34', '2', '11');
INSERT INTO `recommend` VALUES ('35', '2', '12');
INSERT INTO `recommend` VALUES ('36', '2', '13');
INSERT INTO `recommend` VALUES ('37', '2', '14');
INSERT INTO `recommend` VALUES ('38', '2', '15');
INSERT INTO `recommend` VALUES ('39', '2', '16');
INSERT INTO `recommend` VALUES ('40', '2', '18');

-- ----------------------------
-- Table structure for rider
-- ----------------------------
DROP TABLE IF EXISTS `rider`;
CREATE TABLE `rider` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '骑手ID',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(100) NOT NULL COMMENT '密码（加密存储）',
  `name` varchar(50) NOT NULL COMMENT '骑手姓名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `status` int(11) DEFAULT 1 COMMENT '状态：0-待审核，1-正常，2-禁用',
  `create_time` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(20) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone` (`phone`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='骑手信息表';

-- ----------------------------
-- Records of rider
-- ----------------------------
INSERT INTO `rider` VALUES ('1', '13800138001', '123456', '张三', '/static/images/rider1.jpg', '110101199001011234', '1', '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO `rider` VALUES ('2', '13800138002', '123456', '李四', '/static/images/rider2.jpg', '110101199102022345', '1', '2024-01-02 11:00:00', '2024-01-02 11:00:00');
INSERT INTO `rider` VALUES ('3', '13800138003', '123456', '王五', '/static/images/rider3.jpg', '110101199203033456', '0', '2024-01-03 12:00:00', '2024-01-03 12:00:00');
INSERT INTO `rider` VALUES ('4', '13131989898', '123', 'asdo', null, '', '1', '2026-06-12 15:07:27', '2026-06-12 15:18:57');
INSERT INTO `rider` VALUES ('5', '13131387889', '123', 'gs', null, '', '1', '2026-06-12 15:22:48', '2026-06-12 15:23:08');
INSERT INTO `rider` VALUES ('6', '18750138077', '1234', 'gs1', null, '', '1', '2026-06-12 15:27:42', '2026-06-12 15:27:58');
INSERT INTO `rider` VALUES ('7', '18750138076', '123', 'aa', null, '', '1', '2026-06-12 15:31:59', '2026-06-12 15:32:12');

-- ----------------------------
-- Table structure for type
-- ----------------------------
DROP TABLE IF EXISTS `type`;
CREATE TABLE `type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of type
-- ----------------------------
INSERT INTO `type` VALUES ('1', '冰淇淋系列');
INSERT INTO `type` VALUES ('2', '零食系列');
INSERT INTO `type` VALUES ('3', '儿童系列');
INSERT INTO `type` VALUES ('4', '法式系列');
INSERT INTO `type` VALUES ('5', '经典系列');
INSERT INTO `type` VALUES ('8', '节日系列');
INSERT INTO `type` VALUES ('11', '买不起系列1');
INSERT INTO `type` VALUES ('12', '大');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `address` varchar(45) DEFAULT NULL,
  `isadmin` varchar(1) DEFAULT NULL,
  `isvalidate` varchar(1) DEFAULT NULL,
  `createtime` datetime DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admin', 'admin@vilicode.com', 'admin', '管理员', '1333333333', '中华人民共和国', '1', '1', '2026-06-05 14:09:21');
INSERT INTO `user` VALUES ('2', '1', 'vili@vilicode.com', '1', 'vili', '1344444444', '中华人民共和国', '0', '1', '2026-06-05 14:09:21');
INSERT INTO `user` VALUES ('62', 'vili', '4115@qq.con', 'vili', '123', '123', '123', '0', '0', '2026-06-05 14:09:21');
INSERT INTO `user` VALUES ('63', '小明', '123/299@qq.com', '199', '9', '9', '9', '1', '1', '2026-06-05 14:09:21');
INSERT INTO `user` VALUES ('64', '小蓝', '1635048270@qq.com', 'admin5', '12', '123456789', '福建省福州市长乐区滨海路168号', '1', '1', '2026-06-05 14:09:21');
INSERT INTO `user` VALUES ('66', '小红', '1635048270111@qq.com', 'admin7', '56565656', '14235246356768', '福建省福州市长乐区滨海路168号', '1', '1', '2026-06-05 14:09:21');
INSERT INTO `user` VALUES ('68', '小绿', '163504827110@qq.com', 'admin', '11', '111', '福建省福州市长乐区滨海路168号', '1', '1', '2026-06-05 14:09:21');
INSERT INTO `user` VALUES ('70', 'a', '111@qq.com', '123456', 'a1', 'sas', 'asas', '1', '1', '2026-06-05 14:09:21');
INSERT INTO `user` VALUES ('72', 's', '11121122@qq.com', '123', 'qqq', 'sasssss', 'asas234', '0', '1', '2026-06-05 14:09:21');
INSERT INTO `user` VALUES ('73', 'q', '11sssss1@qq.com', '123', 'g', '1313133133113', 'sassssssssssss', '0', '0', '2026-06-05 14:10:21');
INSERT INTO `user` VALUES ('74', '2', 'asdasdad@qqcom', '123', 'asd', '1313133133113', 'sassssssssssss', '0', '0', '2026-06-12 15:03:39');
INSERT INTO `user` VALUES ('75', 'x', 'adadd@qq.com', '123', '2', 'a', 'w', '0', '0', '2026-06-23 09:40:09');
INSERT INTO `user` VALUES ('76', 'e', 'sasasdasd@qq.com', '123', 'qqq', '13110780226', 'asdasdssssss', '0', '0', '2026-06-23 09:42:15');
INSERT INTO `user` VALUES ('77', 'testuser_1782183122743', null, null, null, null, null, '0', '0', '2026-06-23 10:52:08');
INSERT INTO `user` VALUES ('78', 'flowtest_1782183128935', null, null, null, null, null, '0', '0', '2026-06-23 10:52:08');
INSERT INTO `user` VALUES ('79', 'e2etest_1782183128986', null, null, null, null, null, '0', '0', '2026-06-23 10:52:08');
INSERT INTO `user` VALUES ('80', 'apitest_1782183129103', null, null, null, null, null, '0', '0', '2026-06-23 10:52:09');
INSERT INTO `user` VALUES ('81', 'integration_1782183129251', null, 'integration123', '集成测试', '13500135000', '集成测试地址', null, '1', '2026-06-23 10:52:09');
INSERT INTO `user` VALUES ('82', 'testuser_1782263555222', null, null, null, null, null, '0', '0', '2026-06-24 09:12:44');
INSERT INTO `user` VALUES ('83', 'flowtest_1782263564800', null, null, null, null, null, '0', '0', '2026-06-24 09:12:44');
INSERT INTO `user` VALUES ('84', 'e2etest_1782263564909', null, null, null, null, null, '0', '0', '2026-06-24 09:12:44');
INSERT INTO `user` VALUES ('85', 'apitest_1782263566036', null, null, null, null, null, '0', '0', '2026-06-24 09:12:46');
INSERT INTO `user` VALUES ('86', 'integration_1782263566826', null, 'integration123', '集成测试', '13500135000', '集成测试地址', null, '1', '2026-06-24 09:12:46');
INSERT INTO `user` VALUES ('87', 'testuser_1782263785351', null, null, null, null, null, '0', '0', '2026-06-24 09:16:33');
INSERT INTO `user` VALUES ('88', 'flowtest_1782263793896', null, null, null, null, null, '0', '0', '2026-06-24 09:16:33');
INSERT INTO `user` VALUES ('89', 'e2etest_1782263794167', null, null, null, null, null, '0', '0', '2026-06-24 09:16:34');
INSERT INTO `user` VALUES ('90', 'apitest_1782263794453', null, null, null, null, null, '0', '0', '2026-06-24 09:16:34');
INSERT INTO `user` VALUES ('91', 'integration_1782263794571', null, 'integration123', '集成测试', '13500135000', '集成测试地址', null, '1', '2026-06-24 09:16:34');
INSERT INTO `user` VALUES ('92', 'testuser_1782263909590', null, null, null, null, null, '0', '0', '2026-06-24 09:18:39');
INSERT INTO `user` VALUES ('93', 'flowtest_1782263922159', null, null, null, null, null, '0', '0', '2026-06-24 09:18:42');
INSERT INTO `user` VALUES ('94', 'e2etest_1782263927388', null, null, null, null, null, '0', '0', '2026-06-24 09:18:47');
INSERT INTO `user` VALUES ('95', 'apitest_1782263937269', null, null, null, null, null, '0', '0', '2026-06-24 09:18:57');
INSERT INTO `user` VALUES ('96', 'integration_1782263938865', null, 'integration123', '集成测试', '13500135000', '集成测试地址', null, '1', '2026-06-24 09:18:58');
