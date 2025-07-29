package com.payal.ecom.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    // str is not Base64 encoded, Not safe for Base64 decoding
// public static final String SECRET="Qw8$GvB@zL!7nT#Kp9YfXp2*RmZs6Wd4^AsJ1UoE3Vh&NmCx0";

    public static final String SECRET="aWUyM0A1blZkIzEyKkZsQnV6V1dDU2RmSkhCZGlUY2xDdU01c0g";

    public String generateToken(String userName, String role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);                       // Add role to the token
        return createToken(claims, userName);

    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }



    private String createToken(Map<String ,Object> claims, String userName){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact(); // why use .HS256 only why not other?

    }

    private Key getSignKey(){
        byte[] keyBytes = Base64.getDecoder().decode(SECRET);    // here we use Base64 decoder
        return Keys.hmacShaKeyFor(keyBytes);

    }

    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
