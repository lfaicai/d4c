package org.faicai.d4c.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.faicai.d4c.constant.ResponseCode;
import org.faicai.d4c.exception.BusinessException;
import org.faicai.d4c.pojo.vo.TokenPair;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


public class TokenJwtUtil {

    public static final String AUTH_HEADER_KEY = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";


    // 生成密钥
    private static final SecretKey ACCESS_KEY = Keys.hmacShaKeyFor("MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY=".getBytes());

    private static final SecretKey REFRESH_KEY = Keys.hmacShaKeyFor("ADk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY=".getBytes());


    // 过期时间:秒
    private static final int ACCESS_EXPIRATION = 10 * 60 * 60 * 1000;

    private static final int REFRESH_EXPIRATION = 3 * 24 * 60 * 60 * 1000;


    public static TokenPair createTokenPair(String account) {
        String token = createToken(account);
        String refreshToken = createToken(REFRESH_KEY, REFRESH_EXPIRATION, token);
        return TokenPair.builder().token(token).refreshToken(refreshToken).build();
    }



    /**
     * Create token.
     *
     * @param account account
     * @return token
     */
    public static String createToken(String account) {
        return createToken(ACCESS_KEY, ACCESS_EXPIRATION, account);
    }


    /**
     * Create token.
     *
     * @param account account
     * @return token
     */
    public static String createToken(Key key, int expire, String account) {
        Claims claims = Jwts.claims()
                .subject(account)
                .build();
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(key)
                .compact();
    }

    /**
     * Get account.
     *
     * @param token token
     * @return auth info
     */
    public static String getAccount(String token) {
        return validateToken(token, ACCESS_KEY).getSubject();
    }



    /**
     * validate token
     *
     * @param token token
     * @return auth info
     */
    public static boolean validateToken(String token) {
        try {
            validateToken(token, ACCESS_KEY);
            return true;
        }catch (Exception e){
            return false;
        }

    }



    /**
     * validate token
     *
     * @param token token
     * @return auth info
     */
    public static Claims validateToken(String token, SecretKey key) {
        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ResponseCode.TOKEN_EXPIRED);
        }catch (JwtException e){
            throw new BusinessException(ResponseCode.JWT_ILLEGAL);
        }
        return claims;
    }


    /**
     * refresh token
     * @param token token
     * @param refreshToken refresh token
     * @return token pair
     */
    public static TokenPair refreshToken(String token, String refreshToken) {
        String account = getAccount(token);
        Claims claims = validateToken(refreshToken, REFRESH_KEY);
        if (!claims.getSubject().equals(token)) {
            throw new BusinessException(ResponseCode.REFRESH_TOKEN_ERROR);
        }
        return createTokenPair(account);
    }

    public static Claims parseAccessToken(String token) {
        return validateToken(token, ACCESS_KEY);
    }
}
