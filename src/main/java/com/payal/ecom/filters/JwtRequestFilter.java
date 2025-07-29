package com.payal.ecom.filters;

import com.payal.ecom.services.jwt.UserDetailsServiceImpl;
import com.payal.ecom.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;


@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
         String authHeader = request.getHeader("Authorization");
         String token = null;
         String username = null;

        //  Extract token and username
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUserName(token); //  Now correct
        }

        //  Authenticate user
        if(username !=null && SecurityContextHolder.getContext().getAuthentication()== null){
             UserDetails userDetails= userDetailsService.loadUserByUsername(username);

             if(jwtUtil.validateToken(token, userDetails)){
                 String role = jwtUtil.extractRole(token);   // e.g., "ADMIN"

                 //  Add ROLE_ prefix
                 UsernamePasswordAuthenticationToken authToken =
                         new UsernamePasswordAuthenticationToken(
                                 userDetails,
                                 token,
                                 Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                         );



                 //  Set it in the security context — YOU MISSED THIS STEP
                 SecurityContextHolder.getContext().setAuthentication(authToken);

             }
         }
        //  Continue filter chain
         filterChain.doFilter(request, response);
    }

}
