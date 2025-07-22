package com.payal.ecom.services.auth;

import com.payal.ecom.dto.SignupRequest;
import com.payal.ecom.dto.UserDto;
import com.payal.ecom.entity.User;
import com.payal.ecom.enums.UserRole;
import com.payal.ecom.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDto createUser(SignupRequest signupRequest){
        User user = new User();

        user.setEmail(signupRequest.getEmail());
        user.setName(signupRequest.getName());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()) );
        user.setRole(UserRole.CUSTOMER);
        User createdUser = userRepo.save(user);

        UserDto userDto = new UserDto();
        userDto.setId(createdUser.getId());

        return userDto;
    }

   public Boolean hasUserWithEmail(String email){
       return  userRepo.findFirstByEmail(email).isPresent();
   }

   @PostConstruct
   public void createAdminAccount(){
        User adminAccount = userRepo.findByRole(UserRole.ADMIN);
        if(null == adminAccount){
            User user = new User();
            user.setEmail("payalsingh.1110@gmail.com");
            user.setName("Payal");
            user.setRole(UserRole.ADMIN);
            user.setPassword(new BCryptPasswordEncoder().encode("payal123"));
            userRepo.save(user);
        }
   }
}
