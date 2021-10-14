package com.vivacon.user_service.service;

import com.vivacon.user_service.share.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService {
    UserDto createUser(UserDto user);

    UserDto getUserDetailsByEmail(String email);

    UserDto getUserById(String userId);
}
