--liquibase formatted sql

--changeset buol:refresh_tokens splitStatements:false logicalFilePath:classpath:/migrations-default/refresh_tokens.sql
create table refresh_tokens
(
    id uuid not null,
    username varchar(512) not null,
    unixcreated bigint not null,
    unixexpired bigint not null,
    tokenid varchar not null,
    status varchar(16)
);

create unique index refresh_tokens_tokenid_uindex
    on refresh_tokens (tokenid);

create unique index refresh_tokens_username_uindex
    on refresh_tokens (username);

alter table refresh_tokens
    add constraint refresh_tokens_pk
        primary key (username);

