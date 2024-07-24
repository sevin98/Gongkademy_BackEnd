package com.gongkademy.global.security.util;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.redis.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
@Getter
public class JWTUtil {

    private static String JWT_KEY;

    private static final String ACCESS_TOKEN = "accessToken";
    private static final Long ACCESS_TOKEN_EXPIRATION_PERIOD = 5000L; // 30분(임의 설정)
    private static final Long REFRESH_TOKEN_EXPIRATION_PERIOD = 5000L; // 7일(임의 설정)
    private static final String PK_CLAIM = "pk";

    private final RedisUtil redisUtil;

    private List<String> excludeMethods;
    private List<String> excludeEndpoints;
    @Value("${JWT_KEY}")
    public void setJwtKey(String jwtKey) {
        JWT_KEY = jwtKey;
    }

    @Value("${jwt-filter.exclude-methods}")
    public void setExcludeMethods(List<String> excludeMethods){
        this.excludeMethods = excludeMethods;
    }
    @Value("${jwt-filter.exclude-endpoints}")
    public void setExcludeEndpoints(List<String> excludeEndpoints){
        this.excludeEndpoints = excludeEndpoints;
    }




    /**
     * AccessToken 생성 메서드
     * */
    public String createAccessToken(long id) {
        Date now = new Date();
        SecretKey key = null;
        try {
            log.info("createAccessToken의 JWT_KEY 입니다. " + JWT_KEY);
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
     */
    public String createRefreshToken(long id) {
        Date now = new Date();
        SecretKey key = null;
        try {
            log.info("createRefreshToken의 JWT_KEY입니다. " + JWT_KEY);
            key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_PERIOD))
                .claim(PK_CLAIM, id)
                .signWith(key)
                .compact();
    }

    /**
     * redis에 RefreshToken 저장(저장되어 있으면 새로 업데이트됨)
     */
    public void setRefreshToken(long id, String refreshToken) {
        redisUtil.setDataExpire(String.valueOf(id), refreshToken, REFRESH_TOKEN_EXPIRATION_PERIOD);
    }

    /**
     * RefreshToken 반환 메서드
     * redis에 있는 RefreshToken을 반환
     * 없다면 null 반환
     */
    public Optional<String> getRefreshToken(long id) {
        log.info("== getRefershToken() 메서드 == ");
        return Optional.ofNullable(redisUtil.getData(String.valueOf(id)));
    }

    /**
     * 헤더에서 Token 추출
     */
    public Optional<String> extractToken(HttpServletRequest request) {
        //TODO: 예외상황 고민 필요
        return  Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> ACCESS_TOKEN.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public Optional<Integer> extractMemberId(String accessToken) {
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));
            log.info("extractMemberId의 key: " + key);
            return Optional.ofNullable((Integer) Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody().get(PK_CLAIM));
        } catch (ExpiredJwtException e) {
            return Optional.ofNullable((Integer) e.getClaims().get(PK_CLAIM));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 토큰 만료 검증
     */
    public boolean isExpired(String token) {
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));
            log.info("isExpired의 key: " + key);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
        } catch (ExpiredJwtException e) {
            return true; // 토큰이 만료된 경우 true 반환
        }
        return false;
    }

    /**
    * 토큰 유효성 검증
    */
    public boolean isTokenValid(String token) {
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));
            log.info("isTokenvalid의 key: " + key);
            Map<String, Object> claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException expiredJwtException) {
            return true;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (InvalidClaimException invalidClaimException) {
            return false;
        }
        return true;
    }

    // jwt토큰을 검증하는 역할
    public Map<String, Object> validateToken(String token) {
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
