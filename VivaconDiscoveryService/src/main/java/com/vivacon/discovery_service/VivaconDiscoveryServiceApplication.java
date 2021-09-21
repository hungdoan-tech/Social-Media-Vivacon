package com.vivacon.discovery_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class VivaconDiscoveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VivaconDiscoveryServiceApplication.class, args);
	}

}