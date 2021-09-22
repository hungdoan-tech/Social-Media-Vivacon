package com.vivacon.user_service.service;

import com.vivacon.user_service.shared.UserDto;

public interface UsersService {
	UserDto createUser(UserDto userDetails);
}
