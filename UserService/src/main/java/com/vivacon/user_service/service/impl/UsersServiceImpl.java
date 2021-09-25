package com.vivacon.user_service.service.impl;

import com.vivacon.user_service.data_access.entity.UserEntity;
import com.vivacon.user_service.data_access.repository.UsersRepository;
import com.vivacon.user_service.service.UsersService;
import com.vivacon.user_service.share.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Component
public class UsersServiceImpl implements UsersService {

    private ModelMapper mapper;

    private PasswordEncoder passwordEncoder;

    private UsersRepository usersRepository;

    @Autowired
    public void setMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void setBCryptPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUsersRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDto createUser(UserDto userDetails) {
        userDetails.setUserId(UUID.randomUUID().toString());
        UserEntity userEntity = mapper.map(userDetails, UserEntity.class);
        userEntity.setEncryptedPassword(this.passwordEncoder.encode(userDetails.getPassword()));

        UserEntity savedUserEntity = this.usersRepository.save(userEntity);

        UserDto savedUserDto = this.mapper.map(savedUserEntity, UserDto.class);
        return savedUserDto;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = usersRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity inSystemUser = this.usersRepository.findByEmail(username);
        return new User(inSystemUser.getEmail(), inSystemUser.getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
    }
}
