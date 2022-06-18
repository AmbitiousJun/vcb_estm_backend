package com.ambitious.vcbestm.util;

import cn.hutool.core.util.IdUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * JWT 工具类
 * @author Ambitious
 * @date 2022/6/18 9:54
 */
public class JwtUtils {

    /**
     * 生成登录用户的 token
     * @param userId 用户 id
     * @param privateKey 私钥
     * @return token
     */
    public static String generateToken(Long userId, PrivateKey privateKey) {
        return Jwts.builder()
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .setId(IdUtil.simpleUUID())
                .setExpiration(TimeUtils.dateAfterDays(1))
                .claim("userId", userId)
                .compact();
    }

    /**
     * 从 token 中解析用户信息
     * @param token token
     * @param publicKey 公钥
     */
    public static Long getUserIdFromToken(String token, PublicKey publicKey) {
        Jws<Claims> claimsJws = parseToken(token, publicKey);
        return claimsJws.getBody().get("userId", Long.class);
    }

    /**
     * 解析 Token
     * @param token jwt token
     * @param publicKey 公钥
     */
    private static Jws<Claims> parseToken(String token, PublicKey publicKey) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token);
    }
}
