package com.vivacon.user_service.presentation.controller;

import com.vivacon.user_service.presentation.model.CreateUserRequestModel;
import com.vivacon.user_service.presentation.model.CreateUserResponseModel;
import com.vivacon.user_service.presentation.model.UserResponseModel;
import com.vivacon.user_service.service.UsersService;
import com.vivacon.user_service.share.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

    private Environment environment;

    private ModelMapper mapper;

    private UsersService usersService;

    @Autowired
    public UsersController(Environment environment, ModelMapper mapper, UsersService usersService) {
        this.environment = environment;
        this.mapper = mapper;
        this.usersService = usersService;
    }

    @GetMapping("/status/check")
    public String checkingStatus() {
        return "active is started on port " + this.environment.getProperty("local.server.port");
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CreateUserResponseModel> signupNewUser(@RequestBody @Valid CreateUserRequestModel requestUser) {
        UserDto userDto = this.mapper.map(requestUser, UserDto.class);
        UserDto createdUser = this.usersService.createUser(userDto);
        CreateUserResponseModel responseUser = this.mapper.map(createdUser, CreateUserResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponseModel> getUser(@PathVariable("id") String userId) {
        UserDto userDto = this.usersService.getUserById(userId);
        UserResponseModel userResponse = this.mapper.map(userDto, UserResponseModel.class);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }
}
