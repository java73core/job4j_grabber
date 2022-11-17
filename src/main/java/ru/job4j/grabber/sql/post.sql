create table post(
    id serial primary key,
    name varchar(150),
    text TEXT,
    link varchar(256) NOT NULL UNIQUE,
    created timestamp
);