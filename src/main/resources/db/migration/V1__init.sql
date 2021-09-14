
CREATE TABLE IF NOT EXISTS posts (
    id int NOT NULL auto_increment,
    title varchar(50) NOT NULL,
    content varchar(10000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id int NOT NULL auto_increment,
    content varchar(5000),
    post_id int DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);