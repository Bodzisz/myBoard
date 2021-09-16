CREATE DATABASE  IF NOT EXISTS my_board;
USE my_board;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users_roles;

CREATE TABLE users (
    id int NOT NULL AUTO_INCREMENT,
    enabled tinyint(1),
    name varchar(50),
    username varchar(50) NOT NULL,
    password char(68) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (username)
);

CREATE TABLE roles (
    id int NOT NULL AUTO_INCREMENT,
    role varchar(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE users_roles(
    user_id int NOT NULL,
    role_id int NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE NO ACTION ON UPDATE NO ACTION ,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE NO ACTION ON UPDATE NO ACTION
);