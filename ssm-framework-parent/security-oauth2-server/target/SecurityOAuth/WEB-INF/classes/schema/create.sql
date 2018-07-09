CREATE DATABASE IF NOT EXISTS ssm_framwork DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
use ssm_framwork;
-- �û���
drop table if exists sys_user;
create Table sys_user(
	id int primary key auto_increment,
	username varchar(50) not null unique,
	password varchar(128),
	nick_name varchar(100),			-- �ǳ�
	regist_time datetime,			-- �˺�ע��ʱ��
	valid_end_time date,			-- �˺���Ч��ֹ����accountNonExpired
	last_modify_pwd_time datetime,	-- �ϴ������޸ģ����ж��˻������Ƿ���ڣ���90���޸�һ�����룩credentialsNonExpired
	last_login_time datetime,		-- �ϴε�½ϵͳʱ��
	locked tinyint(1) default false,				-- �˺��Ƿ�����
	enabled tinyint(1) default true,				-- �˺��Ƿ����
	insert_time_for_his timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	update_time_for_his timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ��ɫ��
drop table if exists sys_role;
create Table sys_role(
	id int primary key auto_increment,
	role_code varchar(100) not null unique,		-- ��ɫ����
	role_name varchar(200) not null,			-- ��ɫ����
	parent_code varchar(100) not null,			-- ���׽�ɫ���룬�����������ɫ������ͬ
	enabled tinyint(1) default true,						-- ��ǰ��ɫ�Ƿ����
	insert_time_for_his timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	update_time_for_his timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ��ɫ�û��м��
drop table if exists sys_role_user;
create Table sys_role_user(
	role_id int,
	user_id int,
	primary key (role_id,user_id)
);


-- ��Ӳ����û�����ɫ
insert into sys_user(username,password,nick_name,regist_time,valid_end_time,last_modify_pwd_time) values('czadxubin','$2a$10$.p0ZklYxzFXhNd6zP8whUeBfMDVAE.wjFIkRZjwEPvFqQszNXYcJy','����',CURRENT_TIMESTAMP,'2020-12-01',CURRENT_TIMESTAMP);
insert into sys_role(role_code,role_name,parent_code) values('ROLE_ADMIN','ϵͳ����Ա','ROLE_ADMIN');
insert into sys_role_user(role_id,user_id) values(1,1);