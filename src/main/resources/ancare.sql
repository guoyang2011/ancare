DROP DATABASE IF EXISTS ancare;
CREATE DATABASE ancare;
USE ancare;
DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user(
  id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  phone VARCHAR(50),
  password VARCHAR(25),
  icon VARCHAR(150),
  sex VARCHAR(10),
  nick VARCHAR(50),
  age INTEGER,
  area VARCHAR(120),
  job VARCHAR(120),
  creation INTEGER,
  UNIQUE KEY tb_user_unique_key(phone)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS tb_family;
CREATE TABLE tb_family(
  id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  user_id INTEGER ,
  name VARCHAR(50),
  description VARCHAR(250),
  icon VARCHAR(150),
  creation INTEGER,
  UNIQUE KEY tb_family_unique_key(name),
  FOREIGN KEY (user_id) REFERENCES tb_user(id)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS tb_familymember;
CREATE TABLE tb_familymember(
  family_id INTEGER,
  user_id INTEGER,
  membership VARCHAR(50),
  creation INTEGER,
  UNIQUE KEY tb_familymember_unique_key(family_id,user_id)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS tb_hsounds;
CREATE TABLE tb_hsounds(
  id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  user_id INTEGER,
  hsourdsFile VARCHAR(120),
  hrate DOUBLE ,
  diseaseType INT ,
  heartIndex VARCHAR(25),
  creation INTEGER,
  FOREIGN KEY (user_id) REFERENCES tb_user(id)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS tb_feedback;
CREATE TABLE tb_feedback(
  id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  user_id INTEGER,
  remark VARCHAR(250),
  tag VARCHAR (50),
  creation INTEGER ,
  FOREIGN KEY (user_id) REFERENCES tb_user(id)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8;