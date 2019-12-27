DROP TABLE IF EXISTS `hotel`;

CREATE TABLE `hotel`
(
    `hotel_id`    int(11)      NOT NULL COMMENT '酒店ID',
    `name`        varchar(120) NOT NULL COMMENT '酒店名称',
    `extranet`    varchar(200) NOT NULL COMMENT '酒店外网地址',
    `create_time` timestamp    NOT NULL default current_timestamp COMMENT '记录生成时间',
    PRIMARY KEY (`hotel_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  default charset = utf8 comment '酒店表';

INSERT INTO `hotel`
values ('157', '黎川国安假日酒店', 'http://lichuanguoan.hotel.kekcn.cn:8888/tx', '2019-12-25 12:00:00');
INSERT INTO `hotel`
values ('89', '刘星测试账号', 'http://liuxing.txpark.kekcn.cn:8888/tx', '2019-12-26 12:00:00');

DROP TABLE IF EXISTS `machine`;

CREATE TABLE `machine`
(
    `machine_id`   bigint(20) NOT NULL auto_increment comment '主机ID',
    `machine_code` bigint(20) not null comment '机器码',
    `hotel_id`     int(11)    Not NULL comment '主机所属酒店ID',
    `machine_ip`   varchar(30) comment '主机IP',
    `machine_port` varchar(30) comment '主机接收数据端口',
    `create_time`  timestamp  not null default current_timestamp COMMENT '记录生成时间',
    primary key (`machine_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  default charset = utf8 comment '主机表';

INSERT INTO `machine`
values ('1000', '812005399', '89', '', '', '2019-12-26 15:00:00');