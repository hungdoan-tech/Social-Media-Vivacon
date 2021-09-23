package com.vivacon.user_service.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivacon.user_service.presentation.model.LoginRequestModel;
import com.vivacon.user_service.service.UsersService;
import com.vivacon.user_service.shared.UserDto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UsersService usersService;

    private Environment environment;

    public AuthenticationFilter(UsersService usersService, Environment environment) {
	this.usersService = usersService;
	this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
						HttpServletResponse response)
	    throws AuthenticationException {
	try {
	    LoginRequestModel credential = new ObjectMapper().readValue(request.getInputStream(),
									LoginRequestModel.class);
	    return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
		    credential.getEmail(), credential.getPassword(), new ArrayList<>()));
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
					    HttpServletResponse response,
					    FilterChain chain,
					    Authentication authResult)
	    throws IOException, ServletException {
	String userName = ((User) authResult.getPrincipal()).getUsername();
	UserDto userDetails = usersService.getUserDetailsByEmail(userName);

	String token = Jwts.builder().setSubject(userDetails.getUserId())
			   .setExpiration(new Date(System.currentTimeMillis()
				   + Long.parseLong(environment.getProperty("token.expiration_time"))))
			   .signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secret")).compact();

	response.addHeader("token", token);
	response.addHeader("userId", userDetails.getUserId());
    }
}