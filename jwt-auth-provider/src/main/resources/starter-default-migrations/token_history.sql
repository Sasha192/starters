--liquibase formatted sql

--changeset bunin:refresh_tokens splitStatements:false logicalFilePath:classpath:/migrations-default/refresh_tokens.sql
create table token_history
(
    id bigserial not null primary key ,
    username varchar(512) not null,
    ip_address varchar(64) not null,
    user_agent varchar(512) not null,
    unix_created bigint not null
);

create index ip_address_username_uindex
    on refresh_tokens (username);

