package com.vivacon.user_service.service.impl;

import com.vivacon.user_service.data_access.entity.UserEntity;
import com.vivacon.user_service.data_access.repository.UsersRepository;
import com.vivacon.user_service.service.UsersService;
import com.vivacon.user_service.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {

    private ModelMapper mapper;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UsersRepository usersRepository;

    @Autowired
    public ModelMapper getMapper() {
        return mapper;
    }

    @Autowired
    public BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return bCryptPasswordEncoder;
    }

    @Autowired
    public UsersRepository getUsersRepository() {
        return usersRepository;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity inSystemUser = this.usersRepository.findByEmail(username);
        return new User(inSystemUser.getEmail(), inSystemUser.getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = usersRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        return new ModelMapper().map(userEntity, UserDto.class);
    }
}
