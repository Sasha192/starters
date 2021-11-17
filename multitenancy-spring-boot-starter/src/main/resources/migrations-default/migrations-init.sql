--liquibase formatted sql

--changeset buol:migrations-init splitStatements:false logicalFilePath:classpath:/migrations-default/migrations-init.sql
create table tenants
(
    id uuid not null,
    schema text not null,
    active bool default false not null
);

create unique index tenants_id_uindex
    on tenants (id);

create unique index tenants_schema_uindex
    on tenants (schema);


