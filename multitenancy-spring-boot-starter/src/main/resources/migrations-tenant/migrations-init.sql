--liquibase formatted sql

--changeset buol:migrations-init splitStatements:false logicalFilePath:classpath:/migrations-tenant/migrations-init.sql
create table test
(
    id uuid not null
);


