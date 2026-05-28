package com.infotact.wms.wms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private final String SECRET_STRING="your-custom-very-long-secret-key-32-bytes-minimum";

    private final SecretKey SIGNING_KEY= Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    private final long EXPIRATION_TIME_MS=86400000;

    public String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME_MS))
                .signWith(SIGNING_KEY)
                .compact();
    }
    public String extractUsername(String token){
        return getClaims(token).getSubject();
    }
    public boolean validateToken(String token,String username){
        final String extractedUser=extractUsername(token);
        return (extractedUser.equals(username)&& !isTokenExpired(token));
    }


    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(SIGNING_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
