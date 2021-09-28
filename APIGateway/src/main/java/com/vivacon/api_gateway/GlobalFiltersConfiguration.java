package com.vivacon.api_gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Configuration
public class GlobalFiltersConfiguration {

    private final Logger logger = LoggerFactory.getLogger(GlobalFiltersConfiguration.class);

    @Order(0)
    @Bean
    public GlobalFilter firstLoggingFilter() {
        return ((exchange, chain) -> {
            logger.info("The first global logging pre filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                logger.info("The last global logging post filter");
            }));
        });
    }

    @Order(1)
    @Bean
    public GlobalFilter secondLoggingFilter() {
        return ((exchange, chain) -> {
            logger.info("The second global logging pre filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                logger.info("The first global logging post filter");
            }));
        });
    }
}
