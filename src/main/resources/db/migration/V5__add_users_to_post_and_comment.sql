alter table posts add column user_id int NOT NULL;
alter table posts add foreign key (user_id) REFERENCES users(id);

alter table comments add column user_id int NOT NULL;
alter table comments add foreign key (user_id) REFERENCES users(id);