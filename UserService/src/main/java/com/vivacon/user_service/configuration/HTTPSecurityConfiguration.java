package com.vivacon.user_service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.vivacon.user_service.filter.AuthenticationFilter;
import com.vivacon.user_service.service.UsersService;

@Configuration
@EnableWebSecurity
public class HTTPSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final Environment environment;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UsersService usersService;

    @Autowired
    public HTTPSecurityConfiguration(Environment environment,
				     BCryptPasswordEncoder bCryptPasswordEncoder,
				     UsersService usersService) {
	this.environment = environment;
	this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	this.usersService = usersService;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
	return new BCryptPasswordEncoder();
    }

    private AuthenticationFilter authenticationFilter() throws Exception {
	AuthenticationFilter authenticationFilter = new AuthenticationFilter(usersService, environment);
	authenticationFilter.setAuthenticationManager(authenticationManager());
	authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
	return authenticationFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
	http.csrf().disable();
	http.headers().frameOptions().disable();
	http.addFilter(authenticationFilter());
	http.authorizeRequests().antMatchers("/**").hasIpAddress(environment.getProperty("gateway.ip"));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
    }
}
