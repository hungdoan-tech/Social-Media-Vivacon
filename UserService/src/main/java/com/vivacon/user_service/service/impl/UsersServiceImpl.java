package com.vivacon.user_service.service.impl;

import com.vivacon.user_service.data_access.AlbumServiceClient;
import com.vivacon.user_service.data_access.entity.UserEntity;
import com.vivacon.user_service.data_access.repository.UsersRepository;
import com.vivacon.user_service.presentation.model.AlbumResponseModel;
import com.vivacon.user_service.service.UsersService;
import com.vivacon.user_service.share.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UsersServiceImpl implements UsersService {

    private ModelMapper mapper;

    private PasswordEncoder passwordEncoder;

    private UsersRepository usersRepository;

    private Environment environment;

    private AlbumServiceClient albumServiceClient;

    private CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    public UsersServiceImpl(ModelMapper mapper,
                            PasswordEncoder passwordEncoder,
                            UsersRepository usersRepository,
                            Environment environment,
                            AlbumServiceClient albumServiceClient,
                            CircuitBreakerFactory circuitBreakerFactory) {
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
        this.environment = environment;
        this.albumServiceClient = albumServiceClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
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
    public UserDto getUserById(String userId) {
        UserEntity userEntity = usersRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UsernameNotFoundException(userId);
        }
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        //List<AlbumResponseModel> albums = this.albumServiceClient.getAlbums(userId);

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("AlbumsCircuitBreaker");
        List<AlbumResponseModel> albums = circuitBreaker.run(
                () -> this.albumServiceClient.getAlbums(userId),
                throwable -> new ArrayList<AlbumResponseModel>());
        userDto.setAlbums(albums);
        return userDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity inSystemUser = this.usersRepository.findByEmail(username);
        return new User(inSystemUser.getEmail(), inSystemUser.getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
    }
}
