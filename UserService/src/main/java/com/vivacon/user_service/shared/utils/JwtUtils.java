package com.vivacon.user_service.shared.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.lang.String.format;

@Component
public class JwtUtils {

    @Value("${ojt.jwt.secret_salt}")
    private String jwtSecret;

    @Value("${ojt.jwt.jwt_validity}")
    private long JWT_VALIDITY;

    @Value("${ojt.jwt.jwt_issuer}")
    private String JWT_ISSUER;

    private Jws<Claims> currentJMS;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(format("%s", userDetails.getUsername()))
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsername(String token) {
        if (currentJMS == null) {
            this.validate(token);
        }
        Claims claims = currentJMS.getBody();
        return claims.getSubject().split(",")[0];
    }

    public Date getExpirationDate(String token) {
        if (currentJMS == null) {
            this.validate(token);
        }
        Claims claims = currentJMS.getBody();
        return claims.getExpiration();
    }

    public boolean validate(String token) {
        try {
            currentJMS = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature - {}", ex.getMessage());
            throw ex;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token - {}", ex.getMessage());
            throw ex;
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token - {}", ex.getMessage());
            throw ex;
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token - {}", ex.getMessage());
            throw ex;
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty - {}", ex.getMessage());
            throw ex;
        }
    }
}