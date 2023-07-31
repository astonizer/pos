package com.increff.pos.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationProperties {

    @Value("${jdbc.driverClassName}")
    public String JDBC_DRIVER;
    
    @Value("${jdbc.url}")
    public String JDBC_URL;
    
    @Value("${jdbc.username}")
    public String JDBC_USERNAME;
    
    @Value("${jdbc.password}")
    public String JDBC_PASSWORD;
    
    @Value("${hibernate.dialect}")
    public String HIBERNATE_DIALECT;
    
    @Value("${hibernate.show_sql}")
    public String HIBERNATE_SHOW_SQL;
    
    @Value("${hibernate.hbm2ddl.auto}")
    public String HIBERNATE_HBM2DDL;
    
    @Value("${hibernate.physical_naming_strategy}")
    public String HIBERNATE_PHYSICAL_NAMING_STRATEGY;

    @Value("${hibernate.jdbc.time_zone}")
    public String HIBERNATE_JDBC_TIME_ZONE;

    @Value("${invoice_app.url}")
    public String INVOICE_APP_URL;
    
}
