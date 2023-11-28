package com.colphacy.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${app.jwt.expire-duration}")
    private long expireDuration;
    public static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);
    @Value("${app.jwt.secret}")
    private String secretKey;

    public String generateAccessToken(Long id, String authority, LocalDateTime expirationDate) {
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("authority", authority)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expirationDate.toInstant(ZoneOffset.ofHours(0))))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes(Charset.forName("UTF-8")))
                .compact();
    }

    public boolean validateAccessToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT token is expired: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT token is unsupported: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    public String getUserIdFromAccessToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getAuthorityFromAccessToken(String token) {
        return parseClaims(token).get("authority", String.class);
    }

    private Claims parseClaims(String token) {
        return (Claims)Jwts.parser().
                setSigningKey(secretKey.getBytes(Charset.forName("UTF-8"))).
                parseClaimsJws(token.replace("{", "").replace("}", "")).getBody();
    }
}