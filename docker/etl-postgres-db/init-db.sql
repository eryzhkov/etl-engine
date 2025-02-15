CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE SCHEMA IF NOT EXISTS ems;

-- TEST DATA !!!
CREATE SCHEMA IF NOT EXISTS ext;

create table ext.positions (
  id integer not null primary key,
  position_name text not null,
  position_code text not null
);

insert into ext.positions(id, position_name, position_code) values (1, 'position-name-1', 'position-code-1');
insert into ext.positions(id, position_name, position_code) values (2, 'position-name-2', 'position-code-2');
insert into ext.positions(id, position_name, position_code) values (3, 'position-name-3', 'position-code-3');