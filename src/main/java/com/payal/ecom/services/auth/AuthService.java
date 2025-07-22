package com.payal.ecom.services.auth;

import com.payal.ecom.dto.SignupRequest;
import com.payal.ecom.dto.UserDto;

public interface AuthService {

    UserDto createUser(SignupRequest signupRequest);

     Boolean hasUserWithEmail(String email);
}
