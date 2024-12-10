package com.arplanet.template.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Slf4j
public class JwtAuthService {

    @Value("${jwt.private-key1}")
    private Resource privateKeyResource1;

    @Value("${jwt.private-key2}")
    private Resource privateKeyResource2;

    @Value("${jwt.expiration}")
    private Long expiration;

    private final Random random = new Random();

    public String generateToken(String username, List<String> roles) {
        try {
            // 隨機選擇一個私鑰
            String keyId = random.nextBoolean() ? "key1" : "key2";
            PrivateKey privateKey = keyId.equals("key1") ?
                    loadPrivateKey(privateKeyResource1) :
                    loadPrivateKey(privateKeyResource2);

            JWSSigner signer = new RSASSASigner(privateKey);

            // 建立JWT
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID(keyId)
                    .build();

            String loginSessionId = UUID.randomUUID().toString();  // 登入時產生的唯一識別
            Date loginTime = new Date();

            // 設置payload
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(new Date())
                    .claim("roles", roles)
                    .claim("login_session_id", loginSessionId)  // 加入登入會話ID
                    .claim("initial_login_time", loginTime.getTime())
                    .expirationTime(new Date(System.currentTimeMillis() + expiration))
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("Error generating JWT token", e);
            throw new RuntimeException("Could not generate token", e);
        }
    }

    private PrivateKey loadPrivateKey(Resource resource) throws Exception {
        String key = new String(Files.readAllBytes(resource.getFile().toPath()))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }
}
