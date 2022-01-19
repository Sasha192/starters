--liquibase formatted sql

--changeset bunin:refresh_tokens splitStatements:false logicalFilePath:classpath:/migrations-default/refresh_tokens.sql
create table refresh_tokens
(
    id uuid not null primary key,
    username varchar(512) not null,
    ip_address varchar(64) not null,
    user_agent varchar(512) not null,
    unix_created bigint not null,
    unix_expired bigint not null,
    token_id varchar not null,
    status varchar(16)
);

create index refresh_tokens_tokenid_uindex
    on refresh_tokens (token_id);

create index refresh_tokens_username_uindex
    on refresh_tokens (username);

