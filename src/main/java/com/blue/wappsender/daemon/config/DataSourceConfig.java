package com.blue.wappsender.daemon.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion del datasource principal para acceso a la BBDD
 * @author jmendoza
 *
 */
@Configuration
public class DataSourceConfig {
	
	@Bean(name="dataSource")
	public DataSource getDataSource() {
		return DataSourceBuilder
				.create()
				.url("jdbc:mysql://localhost:3306/blue_wapp")
				.username("root")
				.password("root")
				.driverClassName("com.mysql.jdbc.Driver")
				.build();
	}
	
}
