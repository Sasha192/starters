package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant;

import liquibase.exception.LiquibaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.wpstarters.commonwebstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.migrations.SchemaTenantMigrationsService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.exceptions.TenantCreationException;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.ITenantManagementService;

import javax.annotation.CheckReturnValue;
import java.util.UUID;

public class SchemaTenantManagementService implements ITenantManagementService<UUID> {

    private static final Logger logger = LoggerFactory.getLogger(SchemaTenantManagementService.class);

    private final SchemaTenantReadRepository tenantRepository;
    private final SchemaTenantMigrationsService migrationsService;

    public SchemaTenantManagementService(SchemaTenantReadRepository tenantRepository,
                                         SchemaTenantMigrationsService migrationsService) {
        this.tenantRepository = tenantRepository;
        this.migrationsService = migrationsService;
    }

    /**
     * @return fully initialized tenant or empty Tenant - means something bad happened
     */
    @Override
    @CheckReturnValue
    public Tenant<UUID> createTenant() {

        SchemaTenant tenant = new SchemaTenant.Builder()
                .id(UUID.randomUUID())
                .schema(generateSchema())
                .active(true)
                .build();

        try {

            migrationsService.createSchema(tenant.getSchema());
            migrationsService.runMigrationsOnTenant(tenant);
            tenant = tenantRepository.save(tenant);
            return tenant;

        } catch (DataAccessException e) {

            throw new TenantCreationException("Error when creating schema: " + tenant.getSchema(), e);

        } catch (LiquibaseException e) {

            throw new TenantCreationException("Error when populating schema: ", e);

        } catch (Exception e) {

            logger.error("Exception occurred while running Migrations on {} tenant", tenant.getSchema(), e);

        }

        migrationsService.deleteSchema(tenant.getSchema());
        return new SchemaTenant();
    }

    private String generateSchema() {

        String schema = UUID.randomUUID().toString().replace("-","");
        int schemaLength = schema.length();
        if ((System.nanoTime() & 1L) == 0L) {
            schema = schema.substring(0, schemaLength);
        } else {
            schema = schema.substring(schemaLength);
        }

        return schema;

    }

}
