create table post(
    id serial primary key,
    name varchar(50),
    text varchar(256),
    link varchar(256) NOT NULL UNIQUE,
    created  DATE
);