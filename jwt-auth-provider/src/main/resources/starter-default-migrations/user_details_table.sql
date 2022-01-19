--liquibase formatted sql

--changeset bunin:user_details_table splitStatements:false logicalFilePath:classpath:/migrations-default/user_details_table.sql
create table user_details
(
    username varchar(512) not null
        constraint user_details_pk
            primary key,
    password varchar(512) not null,
    account_non_expired bool,
    account_non_locked bool,
    credentials_non_expired bool,
    enabled bool,
    basic_account bool default true,
    provider_type varchar(16),
    public_details jsonb
);

