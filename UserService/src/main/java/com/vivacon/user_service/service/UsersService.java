package com.vivacon.user_service.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.vivacon.user_service.shared.UserDto;

public interface UsersService extends UserDetailsService {
    UserDto createUser(UserDto user);

    UserDto getUserDetailsByEmail(String email);
}
