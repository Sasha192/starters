--liquibase formatted sql

--changeset bunin:check_connection splitStatements:false logicalFilePath:classpath:/migrations-default/check_connection.sql
SELECT 1;
