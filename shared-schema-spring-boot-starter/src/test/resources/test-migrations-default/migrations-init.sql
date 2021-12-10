--liquibase formatted sql

--changeset buol:migrations-init splitStatements:false logicalFilePath:classpath:/migrations-tenant/migrations-init.sql
create table test_entity_table
(
    id uuid not null PRIMARY KEY,
    name varchar not null,
    tenant_id text not null
);

