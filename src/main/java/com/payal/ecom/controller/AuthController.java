package com.payal.ecom.controller;
import com.payal.ecom.dto.AuthenticationRequest;
import com.payal.ecom.dto.SignupRequest;
import com.payal.ecom.dto.UserDto;
import com.payal.ecom.entity.User;
import com.payal.ecom.repository.UserRepository;
import com.payal.ecom.services.auth.AuthService;
import com.payal.ecom.utils.JwtUtil;
// import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.json.JSONException;
//import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


// import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;


    public static final String TOKEN_PREFIX ="Bearer";
    public static final String HEADER_STRING ="Authorization";

    private final AuthService authService;


    @PostMapping("/auth/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        log.info("login detail: {}",authenticationRequest);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        Optional<User> optionalUser = userRepo.findFirstByEmail(userDetails.getUsername());
        User user = userRepo.findFirstByEmail(userDetails.getUsername()).orElseThrow();

       // String token = jwtUtil.generateToken(user.getEmail(), user.getRole()); //  add role in token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name()); // Converts enum to String



        if (optionalUser.isPresent()) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("userId", optionalUser.get().getId());
            responseBody.put("role", optionalUser.get().getRole());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            headers.add("Access-Control-Expose-Headers", "Authorization");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(responseBody);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found");
    }

    @PostMapping("/auth/sign-up")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest){
        log.info("signup Request,email :{},name: {}", signupRequest.getEmail(),signupRequest.getName());
        if(authService.hasUserWithEmail(signupRequest.getEmail())){
            return new ResponseEntity<>("User already exits", HttpStatus.NOT_ACCEPTABLE);
        }

        UserDto userDto = authService.createUser(signupRequest);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
