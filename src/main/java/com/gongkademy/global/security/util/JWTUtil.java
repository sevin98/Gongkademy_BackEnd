package com.gongkademy.global.security.util;

import javax.crypto.SecretKey;
import java.util.Map;

import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.global.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Log4j2
@Component
public class JWTUtil {

    private static String JWT_KEY;

    @Value("${JWT_KEY}")
    public void setJwtKey(String jwtKey) {
        JWT_KEY = jwtKey;
    }

    public static String generateToken(Map<String, Object> valueMap, int min) {
        SecretKey key = null;
        try {
            log.info("JWT_KEY " + JWT_KEY);
            key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(valueMap)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact();
        return jwtStr;
    }

    // jwt토큰을 검증하는 역할
    public static Map<String, Object> validateToken(String token) {
        Map<String, Object> claim = null;
        try {
            SecretKey key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));
            claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                    .getBody();
        } catch (MalformedJwtException malformedJwtException) {
            throw new CustomException(ErrorCode.JWT_MALFORMED);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomException(ErrorCode.JWT_EXPIRED);
        } catch (InvalidClaimException invalidClaimException) {
            throw new CustomException(ErrorCode.JWT_INVALID);
        } catch (JwtException jwtException) {
            throw new CustomException(ErrorCode.JWT_ERROR);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.COMMON_ERROR);
        }
        return claim;
    }
}
