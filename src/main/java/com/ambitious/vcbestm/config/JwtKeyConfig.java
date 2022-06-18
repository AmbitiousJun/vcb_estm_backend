package com.ambitious.vcbestm.config;

import com.ambitious.vcbestm.util.RsaUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 存放 JWT 密钥
 * @author Ambitious
 * @date 2022/6/18 9:47
 */
@Configuration
public class JwtKeyConfig {

    private static final String PATH = "rsa_keys/rsa_jwt";

    @Bean
    public PrivateKey jwtPrivateKey() {
        return RsaUtils.readPrivateKey(PATH);
    }

    @Bean
    public PublicKey jwtPublicKey() {
        return RsaUtils.readPublicKey(PATH + ".pub");
    }
}
