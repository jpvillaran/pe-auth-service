package com.patriotenergygroup.peauthservice.configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "securityEntityManagerFactory", 
	transactionManagerRef = "securityTransactionManager",
	basePackages = { "com.patriotenergygroup.peauthservice.repository" })
public class SecurityDbConfiguration {

	@Primary
	@Bean(name = "securityDataSource")
	@ConfigurationProperties(prefix = "security.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean(name = "securityEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean securityEntityManagerFactory(
			EntityManagerFactoryBuilder builder,
			@Qualifier("securityDataSource") DataSource dataSource) {
		return builder
				.dataSource(dataSource)
				.packages("com.patriotenergygroup.peauthservice.domain")
				.persistenceUnit("security")
				.build();
	}
	
	@Primary
	@Bean(name = "securityTransactionManager")
	public PlatformTransactionManager securityTransactionManager(
			@Qualifier("securityEntityManagerFactory") EntityManagerFactory securityEntityManagerFactory) {
		return new JpaTransactionManager(securityEntityManagerFactory);
	}
}
