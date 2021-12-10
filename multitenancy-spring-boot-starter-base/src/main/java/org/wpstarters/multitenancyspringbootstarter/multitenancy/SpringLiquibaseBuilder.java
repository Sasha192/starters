package org.wpstarters.multitenancyspringbootstarter.multitenancy;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

public class SpringLiquibaseBuilder {

    public static SpringLiquibase buildDefault(DataSource dataSource,
                                               String schema,
                                               String migrationPath,
                                               ResourceLoader resourceLoader,
                                               LiquibaseProperties liquibaseProperties) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(migrationPath);
        liquibase.setResourceLoader(resourceLoader);
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema(schema);
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setLabels(liquibaseProperties.getLabels());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setClearCheckSums(liquibaseProperties.isClearChecksums());
        liquibase.setShouldRun(true);
        liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable());
        // https://github.com/liquibase/liquibase/issues/1651
        return liquibase;
    }

}
