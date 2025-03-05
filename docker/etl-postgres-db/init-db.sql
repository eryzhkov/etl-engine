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

create table ext.persons (
  id integer not null primary key,
  first_name text not null,
  last_name text not null
);

insert into ext.persons(id, first_name, last_name) values (1, 'first-name-1', 'last-name-1');
insert into ext.persons(id, first_name, last_name) values (2, 'first-name-2', 'last-name-2');
insert into ext.persons(id, first_name, last_name) values (3, 'first-name-3', 'last-name-3');
