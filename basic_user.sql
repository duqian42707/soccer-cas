
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for basic_user
-- ----------------------------
DROP TABLE IF EXISTS `basic_user`;
CREATE TABLE `basic_user` (
  `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `account` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `avatar_url` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `gender` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `last_password_reset_time` datetime DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `user_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `last_modified_time` datetime DEFAULT NULL,
  `mod_user_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc4e23ymy8js3t97mv71idjjhh` (`mod_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of basic_user
-- ----------------------------
BEGIN;
INSERT INTO `basic_user` VALUES ('1', 'admin', NULL, NULL, NULL, NULL, '$2a$10$dFfdqjrar3ri3TLANHP3SeMMUjNTO8KlXg67kDT9aeFIX1doPHLk6', NULL, NULL, '管理员', '2019-02-15 07:38:05', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
