package com.example.demo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

@Service
public class JWTService {

    private static String SECRET_KEY = "E2322C26DADA627911251CBFEBF65";



    public JWTService(){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGenerator.generateKey();
            SECRET_KEY = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decode the secret key by converting it to bytes
     * @return the Sign-In key
     */
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * Generating the token with the info from the param, and from the current time
     * @param username It gives us info that we use to make the JWT
     * @return The token that have been generated
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 minutes
                .and()
                .signWith(getKey())
                .compact();

        /*
        return Jwts
                .builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 90))
                .signWith(getKey())
                .compact();

         */
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extract the username from the JWT
     * @param token token from which we extract the username
     * @return username from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    /**
     * Checking if the expiration time is before current time
     * @param token token from which we extract the time
     * @param userDetails maybe we can exclude this !!!!
     * @return if the token is valid or not
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /*
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        final Date tokenExpiration = extractExpiration(token);
        return username.equals(userDetails.getUsername()) && tokenExpiration.before(new Date());
    }
     */
}
