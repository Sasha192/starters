--liquibase formatted sql

--changeset buol:migrations-init splitStatements:false logicalFilePath:classpath:/migrations-default/migrations-init.sql
SELECT 1;
