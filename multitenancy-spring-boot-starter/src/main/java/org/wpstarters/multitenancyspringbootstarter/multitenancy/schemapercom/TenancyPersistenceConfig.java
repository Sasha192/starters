package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.SchemaPerTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Conditional(SchemaPerTenant.class)
@ConditionalOnProperty(name = "${wp37-multitenancy-starter.tenantScan.enabled}", havingValue = "true")
public class TenancyPersistenceConfig {

    private final ConfigurableListableBeanFactory beanFactory;
    private final JpaProperties jpaProperties;
    private final StarterConfigurationProperties.PackageScanProperties entityPackages;


    public TenancyPersistenceConfig(ConfigurableListableBeanFactory beanFactory,
                                    JpaProperties jpaProperties,
                                    StarterConfigurationProperties configurationProperties) {
        this.beanFactory = beanFactory;
        this.jpaProperties = jpaProperties;
        this.entityPackages = configurationProperties.getTenantScan();
    }

    @Bean(value = "tenancyEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean defaultEntityManagerFactory(
            @Qualifier("tenantConnectionProvider") MultiTenantConnectionProvider connectionProvider,
            @Qualifier("tenantIdentifierResolver") CurrentTenantIdentifierResolver tenantResolver) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("tenancy-persistence-unit");
        em.setPackagesToScan(entityPackages.getEntityPackages());

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>(this.jpaProperties.getProperties());
        properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(this.beanFactory));
        properties.remove(AvailableSettings.DEFAULT_SCHEMA);
        properties.put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        properties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
        properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(value = "tenancyTransactionManager")
    public JpaTransactionManager defaultTransactionManager(@Qualifier("tenancyEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

}
