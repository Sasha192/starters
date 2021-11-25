package org.wpstarters.multitenancyspringbootstarter.migrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.wpstarters.multitenancyspringbootstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.domain.SimpleTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.domain.SimpleTenantRepository;

import javax.annotation.CheckForNull;
import java.util.UUID;

public class UUIDTenantsManipulator implements ITenantsManipulator<UUID> {

    private static final Logger logger = LoggerFactory.getLogger(UUIDTenantsManipulator.class);

    private final SimpleTenantRepository tenantRepository;
    private final IMigrationsService migrationsService;

    public UUIDTenantsManipulator(SimpleTenantRepository tenantRepository,
                                  IMigrationsService migrationsService) {
        this.tenantRepository = tenantRepository;
        this.migrationsService = migrationsService;
    }

    @Override
    @CheckForNull
    public Tenant<UUID> createTenant() {
        UUID tenantId = UUID.randomUUID();
        SimpleTenant newTenant = new SimpleTenant.Builder()
                .id(tenantId)
                .active(true)
                .schema(generateSchema(tenantId))
                .build();
        try {
            migrationsService.createSchema(newTenant.getSchema());
            migrationsService.runMigrationsOnTenant(newTenant);
            tenantRepository.save(newTenant);
            return newTenant;
        } catch (DataAccessException e) {
            logger.error("Exception occurred, while creating new Tenant", e);
        } catch (Exception e) {
            logger.error("Exception occurred, while running Migrations for new Tenant {}", newTenant, e);
        }
        // we will reach here if something went wrong
        // rollback changes
        migrationsService.deleteSchema(newTenant.getSchema());
        return null;
    }

    private String generateSchema(UUID tenantId) {
        String schema = tenantId.toString().replace("-", "");
        if ((System.currentTimeMillis() & 1) == 0) {
            schema = schema.substring(0, schema.length() / 2);
        } else {
            schema = schema.substring(schema.length() / 2);
        }
        return schema;
    }

    @Override
    public String removeTenant(Tenant<UUID> tenant) {
        return null;
    }
}
