package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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
@EnableJpaRepositories(
        basePackages = { "org.wpstarters.multitenancyspringbootstarter.multitenancy.domain" },
        entityManagerFactoryRef = "defaultEntityManagerFactory",
        transactionManagerRef = "defaultTransactionManager"
)
public class DefaultSchemaPerCompanyPersistenceConfig {

    private final ConfigurableListableBeanFactory beanFactory;
    private final JpaProperties jpaProperties;
    private final StarterConfigurationProperties.PackageScanProperties entityPackages;
    private final String defaultSchema;


    public DefaultSchemaPerCompanyPersistenceConfig(ConfigurableListableBeanFactory beanFactory,
                                                    JpaProperties jpaProperties,
                                                    StarterConfigurationProperties configurationProperties) {
        this.beanFactory = beanFactory;
        this.jpaProperties = jpaProperties;
        this.entityPackages = configurationProperties.getDefaultScan();
        this.defaultSchema = configurationProperties.getDefaultSchema();
    }

    @Bean(value = "defaultEntityManagerFactory") // do not change!!!
    // @see org.37wp-starters.repositories-processor
    public LocalContainerEntityManagerFactoryBean defaultEntityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setPersistenceUnitName("default-persistence-unit");
        em.setPackagesToScan(entityPackages.getEntityPackages());
        em.setDataSource(dataSource);

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>(this.jpaProperties.getProperties());
        properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(this.beanFactory));
        properties.put(AvailableSettings.DEFAULT_SCHEMA, defaultSchema);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(value = "defaultTransactionManager") // do not change!!!
    // @see org.37wp-starters.repositories-processor
    public JpaTransactionManager defaultTransactionManager(@Qualifier("defaultEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

}
