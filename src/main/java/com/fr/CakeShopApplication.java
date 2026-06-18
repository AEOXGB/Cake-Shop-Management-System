package com.fr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fr.mapper")
public class CakeShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(CakeShopApplication.class, args);
	}

}
