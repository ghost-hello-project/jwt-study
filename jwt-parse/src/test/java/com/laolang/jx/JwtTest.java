package com.laolang.jx;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Maps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.testng.annotations.Test;

public class JwtTest {

    /**
     * 过期时间: 1800 秒
     */
    private static final Integer ACCESS_EXPIRE = 10;

    /**
     * jwt 主题
     */
    private static final String SUBJECT = "jx";

    /**
     * jwt 签发人
     */
    private static final String JWT_ISS = "laolang";

    /**
     * jwt 签名算法, 默认 HS256
     */
    private static final String JWT_HEADER_ALG = "HS256";
    /**
     * 令牌类型, jwt 令牌统一为 JWT
     */
    private static final String JWT_HEADER_TYPE = "JWT";

    /**
     * 加密算法
     */
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;

    /**
     * 私钥. 不能太短, 否则会报错:
     * io.jsonwebtoken.security.WeakKeyException: The specified key byte array is 72 bits which is not secure enough for any JWT HMAC-SHA algorithm
     */
    private final static String SECRET = "secretKeysecretKeysecretKeysecretKeysecretKey";

    /**
     * 秘钥实例
     */
    public static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    private String token;


    @Test
    public void genarateToken() {
        // uuid
        String uuid = IdUtil.fastSimpleUUID();
        // 过期时间
        Date exprireDate = Date.from(Instant.now().plusSeconds(ACCESS_EXPIRE));

        // calim
        Map<String, Object> calim = Maps.newHashMap();
        calim.put("id", 1L);
        calim.put("username", "superAdmin");
        calim.put("nickname", "超级管理员");

        token = Jwts.builder()
                .header()
                .add("typ", JWT_HEADER_TYPE)
                .add("alg", JWT_HEADER_ALG)
                .and()
                // paload 私有声明, 如果有, 必须放在前面
                .claims(calim)
                // 令牌 id
                .id(uuid)
                // 过期时间
                .expiration(exprireDate)
                // 主题
                .subject(SUBJECT)
                // 签发人
                .issuer(JWT_ISS)
                // 签名
                .signWith(KEY, ALGORITHM)
                .compact();
        System.out.println(token);
    }

    @Test(dependsOnMethods = "genarateToken")
    public void parseToken() {
        try {
            JwtParser jwtParser = Jwts.parser().verifyWith(KEY).build();
            Jws<Claims> jws = jwtParser.parseSignedClaims(token);
            Claims claims = jws.getPayload();
            System.out.println(claims.get("id", Long.class));
            System.out.println(claims.get("username", String.class));
            System.out.println(claims.get("nickname", String.class));

        } catch (SignatureException e) {
            System.out.println("jwt 解析报错, 例如 jwt 结构不正确");
            System.out.println(ExceptionUtils.getMessage(e));
        } catch (ExpiredJwtException e) {
            System.out.println("jwt 过期");
            System.out.println(ExceptionUtils.getMessage(e));
        }catch (JwtException e){
            System.out.println("其他 jwt 异常");
            System.out.println(ExceptionUtils.getMessage(e));
        }
        catch (Exception e) {
            System.out.println(ExceptionUtils.getMessage(e));
        }


    }
}
