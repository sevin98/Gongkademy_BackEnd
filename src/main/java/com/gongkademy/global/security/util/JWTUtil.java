package com.gongkademy.global.security.util;

import javax.crypto.SecretKey;
import java.util.Map;

import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.redis.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class JWTUtil {

    private static String JWT_KEY;

    private static final String ACCESS_HEADER = "AccessToken";
    private static final String REFRESH_HEADER = "AccessToken";
    private static final Long ACCESS_TOKEN_EXPIRATION_PERIOD = 1800000L; // 30분(임의 설정)
    private static final Long REFRESH_TOKEN_EXPIRATION_PERIOD = 604800000L; // 7일(임의 설정)
    private static final String PK_CLAIM = "pk";
    private static final String BEARER = "Bearer ";

    private static RedisUtil redisUtil;

    @Value("${JWT_KEY}")
    public void setJwtKey(String jwtKey) {
        JWT_KEY = jwtKey;
    }

    /**
     * AccessToken 생성 메서드
     * */
    public static String createAccessToken(long id) {
        Date now = new Date();
        SecretKey key = null;
        try {
            log.info("JWT_KEY " + JWT_KEY);
            key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_PERIOD))
                .claim(PK_CLAIM, id)
                .signWith(key)
                .compact();
    }

    /**
     * RefreshToken 생성 메서드
     * 생성 후 redis에 저장
     */
    public static String createRefreshToken(long id) {
        Date now = new Date();
        SecretKey key = null;
        try {
            log.info("JWT_KEY " + JWT_KEY);
            key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_PERIOD))
                .claim(PK_CLAIM, id)
                .signWith(key)
                .compact();

        redisUtil.setData(String.valueOf(id), refreshToken);

        return refreshToken;
    }

    /**
     * RefreshToken redis에 업데이트
     */
    public void updateRefreshToken(long id, String refreshToken) {
        redisUtil.deleteData(String.valueOf(id));
        redisUtil.setData(String.valueOf(id), refreshToken);
    }

    private void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_HEADER, accessToken);
    }

    private void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(REFRESH_HEADER, refreshToken);
    }

    /**
     * 헤더에서 Token 추출
     */
    public Optional<String> extractToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * refreshToken 검증
     * 1. 토큰 자체 검증
     * 2. redis에 있는 토큰과 비교
    */
    public static boolean isRefreshTokenValid(String refreshToken) {
        if (!isTokenValid(refreshToken)) return false;
        int id = Integer.parseInt(validateToken(refreshToken).get(PK_CLAIM).toString());
        String redisRefreshToken = redisUtil.getData(String.valueOf(id));

        return refreshToken.equals(redisRefreshToken);
    }

    /**
    * token 검증
    */
    public static boolean isTokenValid(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));
            Map<String, Object> claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
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
