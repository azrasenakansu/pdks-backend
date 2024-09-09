package com.proven.pdks.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    public static final String SECRET = "01f7894a8b89dd99a5a5e45427da1863e310e20accc0d7499294f590c915f641d45c587603929ee275e9c68d8faa2a84aaaf1816085b80fdca41c2c40a6700b46d394ed24e96b65c25658ac27535d82b14d9632cfe64d831af706a1114fffd8fdb4e2c112f7f8f5c8ac8f84a517641468bd035cc2c6f3098d56af88ad77cbdd71ef3a1c380d6fe7f7352e6158ee4d232ab798427adb42fecb60d5cc4ea7ac99230caede2c301eb581f59c095db5ca32944643a33c9983f34d1e17050282bb3ef299702ad9ff428572ce5c4fd298acebbc27209147eccad9db11960b5f4f8e80cf60729dec5acc1bb6da5ebf5ca4b4716ae78567ad3bccbda05c27e42cd02b00e";
    public static final long HOUR = 3600 * 1000;

    public String extractUsername(String token) {
        try{
            return extractClaim(token, Claims::getSubject);
        }
        catch (Exception e){
            return null;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String GenerateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + HOUR * 72))
                .signWith(SignatureAlgorithm.HS256, getSignKey()).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}