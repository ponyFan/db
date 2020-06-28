package com.ga.annotion.multisource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * @author zelei.fan
 * @date 2020/5/6 9:47
 * @description 多数据源配置，注意点：1、application @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
 *                                 2、配置文件中url改成jdbc-url
 *                                 3、默认数据源为mysql，其他的数据源则需要在方法上加@DS("oracle")注解
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "mysql")
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource mysqlDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "oracle")
    @ConfigurationProperties(prefix = "spring.datasource.oracle")
    public DataSource oracleDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "dynamicMysql")
    public DataSource dataSource(){
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(mysqlDataSource());
        HashMap<Object, Object> hashMap = new HashMap<>(3);
        hashMap.put("mysql", mysqlDataSource());
        hashMap.put("oracle", oracleDataSource());
        dynamicDataSource.setTargetDataSources(hashMap);
        return dynamicDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        return new DataSourceTransactionManager(dataSource());
    }
}
