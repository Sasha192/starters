package org.wpstarters.multitenancyspringbootstarter.multitenancy.domain;

import liquibase.exception.LiquibaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.wpstarters.multitenancyspringbootstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.exceptions.TenantCreationException;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.migrationsproviders.IMigrationsService;

import java.util.UUID;

@Service
public class TenantManagementService implements ITenantManagementService<UUID> {

    private static final Logger logger = LoggerFactory.getLogger(TenantManagementService.class);

    private final SimpleTenantRepository tenantRepository;
    private final IMigrationsService migrationsService;

    public TenantManagementService(SimpleTenantRepository tenantRepository, IMigrationsService migrationsService) {
        this.tenantRepository = tenantRepository;
        this.migrationsService = migrationsService;
    }

    @Override
    public Tenant<UUID> createTenant() {

        SimpleTenant tenant = new SimpleTenant.Builder()
                .id(UUID.randomUUID())
                .schema(generateSchema())
                .active(true)
                .build();

        try {

            migrationsService.createSchema(tenant.getSchema());
            migrationsService.runMigrationsOnTenant(tenant);
            tenantRepository.save(tenant);
            return tenant;

        } catch (DataAccessException e) {

            throw new TenantCreationException("Error when creating schema: " + tenant.getSchema(), e);

        } catch (LiquibaseException e) {

            throw new TenantCreationException("Error when populating schema: ", e);

        } catch (Exception e) {

            logger.error("Exception occurred while running Migrations on {} tenant", tenant.getSchema(), e);

        }

        return new SimpleTenant();
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
