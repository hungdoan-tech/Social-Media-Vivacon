package com.vivacon.user_service.presentation.controller;

import com.vivacon.user_service.presentation.model.LoginRequestModel;
import com.vivacon.user_service.service.UsersService;
import com.vivacon.user_service.shared.UserDto;
import com.vivacon.user_service.shared.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/login")
public class AuthenticationController {

    private AuthenticationManager authenticationManager;

    private JwtUtils jwtTokenUtil;

    private UsersService usersService;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtTokenUtil(JwtUtils jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Autowired
    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity signupNewUser(@RequestBody @Valid LoginRequestModel credential) {

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credential.getEmail(), credential.getPassword()));
        UserDetails userDetail = (UserDetails) authenticate.getPrincipal();
        UserDto AuthenticatedUser = this.usersService.getUserDetailsByEmail(credential.getEmail());
        String jwt = jwtTokenUtil.generateAccessToken(userDetail);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .header("users_id", AuthenticatedUser.getUserId())
                .body(null);
    }
}



