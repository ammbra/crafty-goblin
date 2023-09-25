drop table if exists todo cascade;
create table todo (id uuid not null, content BLOB, created_on timestamp(6), url varchar(255), completed boolean, ordering tinyint, primary key (id));
