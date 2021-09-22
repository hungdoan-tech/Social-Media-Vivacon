package com.vivacon.user_service.service.impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vivacon.user_service.data_access.entity.UserEntity;
import com.vivacon.user_service.data_access.repository.UsersRepository;
import com.vivacon.user_service.service.UsersService;
import com.vivacon.user_service.shared.UserDto;

@Service
public class UsersServiceImpl implements UsersService {

	private ModelMapper mapper;

	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private UsersRepository usersRepository;

	@Autowired
	public UsersServiceImpl(ModelMapper mapper, BCryptPasswordEncoder bCryptPasswordEncoder,
			UsersRepository usersRepository) {
		this.mapper = mapper;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.usersRepository = usersRepository;
	}

	@Override
	public UserDto createUser(UserDto userDetails) {

		userDetails.setUserId(UUID.randomUUID().toString());

		UserEntity userEntity = mapper.map(userDetails, UserEntity.class);
		userEntity.setEncryptedPassword(this.bCryptPasswordEncoder.encode(userDetails.getPassword()));

		UserEntity savedUserEntity = this.usersRepository.save(userEntity);

		UserDto savedUserDto = this.mapper.map(savedUserEntity, UserDto.class);

		return savedUserDto;
	}
}
