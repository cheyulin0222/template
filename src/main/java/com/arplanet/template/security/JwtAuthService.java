package com.arplanet.template.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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

            JWSSigner signer = new RSASSASigner((RSAPrivateKey) privateKey);

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

//    public Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    public Claims extractPayload(String token) {
//        // 將 token 分成三部分
//        String[] chunks = token.split("\\.");
//
//        // base64 decode payload (第二部分)
//        Base64.Decoder decoder = Base64.getUrlDecoder();
//        String payload = new String(decoder.decode(chunks[1]));
//
//        // 轉換成 Claims 對象
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return mapper.readValue(payload, Claims.class);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to parse token payload", e);
//        }
//    }
//
//    public PublicKey getSigningKey() {
//        try {
//            String key = new String(Files.readAllBytes(publicKeyResource.getFile().toPath()))
//                    .replace("-----BEGIN PUBLIC KEY-----", "")
//                    .replace("-----END PUBLIC KEY-----", "")
//                    .replaceAll("\\s", "");
//
//            byte[] decoded = Base64.getDecoder().decode(key);
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
//            KeyFactory kf = KeyFactory.getInstance("RSA");
//            return kf.generatePublic(keySpec);
//        } catch (Exception e) {
//            throw new RuntimeException("Error loading public key", e);
//        }
//    }

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
