--liquibase formatted sql

--changeset buol:user_details_table splitStatements:false logicalFilePath:classpath:/migrations-default/user_details_table.sql
create table user_details
(
    username varchar(512) not null
        constraint user_details_pk
            primary key,
    password varchar(512) not null,
    accountnonexpired bool,
    accountnonlocked bool,
    enabled bool,
    basicaccount bool default true,
    providertype varchar(16),
    publicdetails jsonb
);

