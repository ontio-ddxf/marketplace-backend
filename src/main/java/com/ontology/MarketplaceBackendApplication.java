package com.ontology;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@tk.mybatis.spring.annotation.MapperScan("com.ontology.mapper")
@EnableTransactionManagement
@EnableSwagger2
public class MarketplaceBackendApplication {


	public static void main(String[] args) {
		SpringApplication.run(MarketplaceBackendApplication.class, args);
	}

}
