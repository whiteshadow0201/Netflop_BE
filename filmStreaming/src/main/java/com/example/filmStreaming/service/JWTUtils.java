package com.example.filmStreaming.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.ref.PhantomReference;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.*;


@Component
public class JWTUtils {
    private final SecretKey Key;
    private static final long EXPIRATION_TIME = 43200000; // 12 hours
//    private static final long REFRESH_EXPIRATION_TIME = 43200000; // 12 hours

    public JWTUtils() {
        String secreteString = "172409127047129740912740917209712093471029734091274092650912609561787GSHGSG787871871978";
        byte[] keyBytes = Base64.getDecoder().decode(secreteString.getBytes(StandardCharsets.UTF_8));
        this.Key = new SecretKeySpec(keyBytes, "HmacSHA256");

    }

    public String generateToken(UserDetails userDetails) {
        return builder().
                setSubject(userDetails.getUsername()).
                setIssuedAt(new java.util.Date()).
                setExpiration(new java.util.Date(System.currentTimeMillis() + EXPIRATION_TIME)).
                signWith(Key).
                compact();

    }
    public  String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails){
        return builder().
                setClaims(claims).
                setSubject(userDetails.getUsername()).
                setIssuedAt(new java.util.Date()).
                setExpiration(new java.util.Date(System.currentTimeMillis() + EXPIRATION_TIME )).
                signWith(Key).
                compact();
    }
    public String extractUsername(String token) {

        return extractClaims(token,Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parserBuilder().setSigningKey(Key).build().parseClaimsJws(token).getBody());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new java.util.Date());
    }
}