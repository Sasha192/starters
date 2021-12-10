--liquibase formatted sql

--changeset buol:migrations-init splitStatements:false logicalFilePath:classpath:/migrations-tenant/migrations-init.sql
create table tenant_test_table
(
    id uuid not null PRIMARY KEY,
    name varchar not null
);

