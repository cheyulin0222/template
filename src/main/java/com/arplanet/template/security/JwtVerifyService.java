package com.arplanet.template.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtVerifyService {

    @Value("${jwt.public-key1}")
    private Resource publicKeyResource1;

    @Value("${jwt.public-key2}")
    private Resource publicKeyResource2;

    private Map<String, PublicKey> publicKeys;

    @PostConstruct
    public void init() {
        try {
            publicKeys = new HashMap<>();
            publicKeys.put("key1", loadPublicKey(publicKeyResource1));
            publicKeys.put("key2", loadPublicKey(publicKeyResource2));
        } catch (Exception e) {
            log.error("Error initializing public keys", e);
            throw new RuntimeException("Could not initialize public keys", e);
        }
    }

    public Claims verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String keyId = signedJWT.getHeader().getKeyID();

            PublicKey publicKey = publicKeys.get(keyId);
            if (publicKey == null) {
                throw new RuntimeException("Invalid key ID: " + keyId);
            }

            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Error verifying JWT token", e);
            throw new RuntimeException("Could not verify token", e);
        }
    }

    public Claims extractPayloadWithoutVerification(String token) {
        // 將 token 分成三部分
        String[] chunks = token.split("\\.");

        // base64 decode payload (第二部分)
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        // 轉換成 Claims 對象
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(payload, Claims.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse token payload", e);
        }
    }

    private PublicKey loadPublicKey(Resource resource) throws Exception {
        String key = new String(Files.readAllBytes(resource.getFile().toPath()))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(keySpec);
    }
}
