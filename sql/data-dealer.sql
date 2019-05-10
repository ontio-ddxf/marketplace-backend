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
-- Table structure for tbl_order_sync
-- ----------------------------
DROP TABLE IF EXISTS `tbl_order_sync`;
CREATE TABLE `tbl_order_sync` (
  `order_id` varchar(255) NOT NULL,
  `buyer_ontid` varchar(255) DEFAULT NULL,
  `seller_ontid` varchar(255) DEFAULT NULL,
  `buy_tx` text,
  `sell_tx` text,
  `recv_token_tx` text,
  `recv_msg_tx` text,
  `cancel_tx` text,
  `buy_event` text,
  `sell_event` text,
  `recv_token_event` text,
  `recv_msg_event` text,
  `cancel_event` text,
  `buy_date` datetime DEFAULT NULL,
  `sell_date` datetime DEFAULT NULL,
  `recv_token_date` datetime DEFAULT NULL,
  `recv_msg_date` datetime DEFAULT NULL,
  `cancel_date` datetime DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL COMMENT 'boughtOnchain;buyerCancelOnchain;deliveredOnchain;sellerRecvTokenOnchain;buyerRecvMsgOnchain',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for tbl_order_data_sync
-- ----------------------------
DROP TABLE IF EXISTS `tbl_order_data_sync`;
CREATE TABLE `tbl_order_data_sync` (
  `id` varchar(255) NOT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `data_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
