/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50548
Source Host           : localhost:3306
Source Database       : superwechat

Target Server Type    : MYSQL
Target Server Version : 50548
File Encoding         : 65001

Date: 2016-06-08 16:29:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_superwechat_avatar
-- ----------------------------
DROP TABLE IF EXISTS `t_superwechat_avatar`;
CREATE TABLE `t_superwechat_avatar` (
  `m_avatar_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `m_avatar_user_name` varchar(30) NOT NULL,
  `m_avatar_path` varchar(30) NOT NULL,
  `m_avatar_type` int(11) DEFAULT NULL,
  `m_avatar_last_update_time` varchar(15) NOT NULL,
  PRIMARY KEY (`m_avatar_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_superwechat_avatar
-- ----------------------------
INSERT INTO `t_superwechat_avatar` VALUES ('1', 'zhangsan', 'user_avatar', '0', '1462870455');
INSERT INTO `t_superwechat_avatar` VALUES ('2', 'lisi', 'user_avatar', '0', '1462870455');
INSERT INTO `t_superwechat_avatar` VALUES ('3', 'wangwu', 'user_avatar', '0', '1462870455');
INSERT INTO `t_superwechat_avatar` VALUES ('4', '1460308192724', 'group_avatar', '1', '1462870455');
INSERT INTO `t_superwechat_avatar` VALUES ('5', 'xiaoli', 'user_avatar', '0', '1465366197599');

-- ----------------------------
-- Table structure for t_superwechat_contact
-- ----------------------------
DROP TABLE IF EXISTS `t_superwechat_contact`;
CREATE TABLE `t_superwechat_contact` (
  `m_contact_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `m_contact_user_name` varchar(30) NOT NULL,
  `m_contact_cname` varchar(30) NOT NULL,
  PRIMARY KEY (`m_contact_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_superwechat_contact
-- ----------------------------
INSERT INTO `t_superwechat_contact` VALUES ('1', 'zhangsan', 'lisi');
INSERT INTO `t_superwechat_contact` VALUES ('3', 'zhangsan', 'wangwu');
INSERT INTO `t_superwechat_contact` VALUES ('4', 'wangwu', 'zhangsan');
INSERT INTO `t_superwechat_contact` VALUES ('5', 'lisi', 'zhangsan');

-- ----------------------------
-- Table structure for t_superwechat_group
-- ----------------------------
DROP TABLE IF EXISTS `t_superwechat_group`;
CREATE TABLE `t_superwechat_group` (
  `m_group_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `m_group_hxid` varchar(20) DEFAULT NULL,
  `m_group_name` varchar(30) NOT NULL,
  `m_group_description` varchar(200) DEFAULT NULL,
  `m_group_owner` varchar(30) NOT NULL,
  `m_group_last_modified_time` varchar(15) NOT NULL,
  `m_group_max_users` int(11) DEFAULT NULL,
  `m_group_affiliations_count` int(11) DEFAULT NULL,
  `m_group_is_public` int(11) DEFAULT NULL,
  `m_group_allow_invites` int(11) DEFAULT NULL,
  PRIMARY KEY (`m_group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_superwechat_group
-- ----------------------------
INSERT INTO `t_superwechat_group` VALUES ('1', '1460308192724', 'OMG', '临时测试的群组', 'zhangsan', '1462870455', '-1', '4', '1', '0');

-- ----------------------------
-- Table structure for t_superwechat_location
-- ----------------------------
DROP TABLE IF EXISTS `t_superwechat_location`;
CREATE TABLE `t_superwechat_location` (
  `m_location_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `m_location_user_name` varchar(30) NOT NULL,
  `m_location_latitude` double NOT NULL,
  `m_location_longitude` double NOT NULL,
  `m_location_is_searched` int(11) DEFAULT NULL,
  `m_location_last_update_time` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`m_location_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_superwechat_location
-- ----------------------------
INSERT INTO `t_superwechat_location` VALUES ('1', 'zhangsan', '39.930657', '116.397518', '1', '1465290856356');
INSERT INTO `t_superwechat_location` VALUES ('2', 'lisi', '39.932199', '116.183566', '1', '1465290856356');
INSERT INTO `t_superwechat_location` VALUES ('3', 'wangwu', '39.915118', '116.403962', '1', '1465291129532');
INSERT INTO `t_superwechat_location` VALUES ('4', 'xiaoli', '39.915215', '116.403725', '1', '1465372754726');

-- ----------------------------
-- Table structure for t_superwechat_member
-- ----------------------------
DROP TABLE IF EXISTS `t_superwechat_member`;
CREATE TABLE `t_superwechat_member` (
  `m_member_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `m_member_user_name` varchar(30) NOT NULL,
  `m_member_group_id` int(11) NOT NULL,
  `m_member_group_hxid` varchar(20) NOT NULL,
  `m_member_permission` int(11) DEFAULT NULL,
  PRIMARY KEY (`m_member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_superwechat_member
-- ----------------------------
INSERT INTO `t_superwechat_member` VALUES ('1', 'zhangsan', '1', '1460308192724', '1');
INSERT INTO `t_superwechat_member` VALUES ('2', 'lisi', '1', '1460308192724', '0');

-- ----------------------------
-- Table structure for t_superwechat_user
-- ----------------------------
DROP TABLE IF EXISTS `t_superwechat_user`;
CREATE TABLE `t_superwechat_user` (
  `m_user_name` varchar(30) NOT NULL,
  `m_user_password` varchar(20) NOT NULL,
  `m_user_nick` varchar(30) NOT NULL,
  PRIMARY KEY (`m_user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_superwechat_user
-- ----------------------------
INSERT INTO `t_superwechat_user` VALUES ('lisi', 'a', '李思思');
INSERT INTO `t_superwechat_user` VALUES ('wangwu', 'a', '王五');
INSERT INTO `t_superwechat_user` VALUES ('xiaoli', 'aaa', 'lilili');
INSERT INTO `t_superwechat_user` VALUES ('zhangsan', 'zs', 'mynick');
