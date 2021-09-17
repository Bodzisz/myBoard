CREATE DATABASE  IF NOT EXISTS my_board;
USE my_board;

DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;

CREATE TABLE posts (
    id int NOT NULL auto_increment,
    title varchar(50) NOT NULL,
    content varchar(10000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE comments (
    id int NOT NULL auto_increment,
    content varchar(5000),
    post_id int NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (post_id) REFERENCES posts(id)
);


INSERT INTO posts(title, content) VALUES ('testTitle', 'Test content 123');
INSERT INTO comments(content, post_id) VALUES ('Test comment 1',1), ('Test comment 2', 1);