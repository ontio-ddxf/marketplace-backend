/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50637
Source Host           : localhost:3306
Source Database       : ontid

Target Server Type    : MYSQL
Target Server Version : 50637
File Encoding         : 65001

Date: 2019-03-21 16:47:15
*/

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tbl_certifier
-- ----------------------------
DROP TABLE IF EXISTS `tbl_certifier`;
CREATE TABLE `tbl_certifier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ontid` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tbl_judger
-- ----------------------------
DROP TABLE IF EXISTS `tbl_judger`;
CREATE TABLE `tbl_judger` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ontid` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tbl_ons
-- ----------------------------
DROP TABLE IF EXISTS `tbl_ons`;
CREATE TABLE `tbl_ons` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ontid` varchar(255) DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
