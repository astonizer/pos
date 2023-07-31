package com.increff.pos.spring;

import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@Configuration
public class DbConfig {

    @Autowired
    private ApplicationProperties applicationProperties;

    public static final String PACKAGE_POJO = "com.increff.pos.pojo";

    @Bean(name = "dataSource")
    public DataSource getDataSource() {
//		logger.info("jdbcDriver: " + jdbcDriver + ", jdbcUrl: " + jdbcUrl + ", jdbcUsername: " + jdbcUsername);
        BasicDataSource bean = new BasicDataSource();
        bean.setDriverClassName(applicationProperties.JDBC_DRIVER);
        bean.setUrl(applicationProperties.JDBC_URL);
        bean.setUsername(applicationProperties.JDBC_USERNAME);
        bean.setPassword(applicationProperties.JDBC_PASSWORD);
        bean.setInitialSize(2);
        bean.setDefaultAutoCommit(false);
        //bean.setMaxTotal(10);
        bean.setMinIdle(2);
        bean.setValidationQuery("Select 1");
        bean.setTestWhileIdle(true);
        bean.setTimeBetweenEvictionRunsMillis(10 * 60 * 100);
        return bean;
    }

    @Bean(name = "entityManagerFactory")
    @Autowired
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        //logger.info("hibernateDialect: " + jdbcDriver + ", hibernateHbm2ddl: " + hibernateHbm2ddl);
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setPackagesToScan(PACKAGE_POJO);
        HibernateJpaVendorAdapter jpaAdapter = new HibernateJpaVendorAdapter();
        bean.setJpaVendorAdapter(jpaAdapter);

        Properties jpaProperties = hibernateProperties();
        bean.setJpaProperties(jpaProperties);
        return bean;
    }

    @Bean(name = "transactionManager")
    @Autowired
    public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {
        JpaTransactionManager bean = new JpaTransactionManager();
        bean.setEntityManagerFactory(emf.getObject());
        return bean;
    }

    private Properties hibernateProperties() {
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", applicationProperties.HIBERNATE_DIALECT);
        jpaProperties.put("hibernate.show_sql", applicationProperties.HIBERNATE_SHOW_SQL);
        jpaProperties.put("hibernate.hbm2ddl.auto", applicationProperties.HIBERNATE_HBM2DDL);
        jpaProperties.put("hibernate.physical_naming_strategy", applicationProperties.HIBERNATE_PHYSICAL_NAMING_STRATEGY);
        jpaProperties.put("hibernate.jdbc.time_zone", applicationProperties.HIBERNATE_JDBC_TIME_ZONE);
        return jpaProperties;
    }

}
