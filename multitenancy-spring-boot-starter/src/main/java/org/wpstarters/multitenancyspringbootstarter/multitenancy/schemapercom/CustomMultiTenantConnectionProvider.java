package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wpstarters.multitenancyspringbootstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.domain.SimpleTenantRepository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CustomMultiTenantConnectionProvider implements MultiTenantConnectionProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomMultiTenantConnectionProvider.class);

    private final DataSource datasource;
    private final SimpleTenantRepository tenantRepository;
    private final StarterConfigurationProperties.CacheConfigurationProperties cacheProperties;

    private LoadingCache<String, String> tenantSchemas;

    public CustomMultiTenantConnectionProvider(DataSource datasource,
                                               SimpleTenantRepository tenantRepository,
                                               StarterConfigurationProperties.CacheConfigurationProperties cacheProperties) {
        this.datasource = datasource;
        this.tenantRepository = tenantRepository;
        this.cacheProperties = cacheProperties;
    }

    @PostConstruct
    public void heatUpCache() {
        TimeUnit cacheTimeUnits = cacheProperties.getTimeUnit() == null ? TimeUnit.MINUTES: cacheProperties.getTimeUnit();
        tenantSchemas = CacheBuilder.newBuilder()
                .maximumSize(cacheProperties.getMaxSize())
                .expireAfterAccess(cacheProperties.getExpiration(), cacheTimeUnits)
                .build(new CacheLoader<>() {
                    public String load(String key) {
                        UUID id = UUID.fromString(key);
                        Tenant<UUID> tenant = tenantRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("No such tenant: " + key));
                        return tenant.getSchema();
                    }
                });
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return datasource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        logger.info("Get connection for tenant {}", tenantIdentifier);
        String tenantSchema;
        try {
            tenantSchema = tenantSchemas.get(tenantIdentifier);
        } catch (ExecutionException e) {
            throw new RuntimeException("No such tenant: " + tenantIdentifier);
        }
        final Connection connection = getAnyConnection();
        connection.setSchema(tenantSchema);
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        logger.info("Release connection for tenant {}", tenantIdentifier);
        connection.setSchema(null);
        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if ( MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType) ) {
            return (T) this;
        } else {
            throw new UnknownUnwrapTypeException( unwrapType );
        }
    }
}
