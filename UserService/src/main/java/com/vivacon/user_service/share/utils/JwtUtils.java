package com.vivacon.user_service.share.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

    @Value("${jwt.secret_salt}")
    private String jwtSecretSalt;

    @Value("${jwt.expiration_time}")
    private long jwtExpirationTime;

    @Value("${jwt.jwt_issuer}")
    private String jwtIssuer;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public String generateAccessToken(UserDetails userDetails) {

        return Jwts.builder()
                .setSubject(format("%s", userDetails.getUsername()))
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(SignatureAlgorithm.HS512, this.jwtSecretSalt)
                .compact();
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecretSalt).parseClaimsJws(token).getBody();
        return claims.getSubject().split(",")[0];
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecretSalt).parseClaimsJws(token);
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