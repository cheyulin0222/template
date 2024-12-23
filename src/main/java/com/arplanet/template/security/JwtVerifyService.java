package com.arplanet.template.security;

import com.arplanet.template.log.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.arplanet.template.exception.ErrorType.AUTH;
import static com.arplanet.template.exception.ErrorType.SYSTEM;
import static com.arplanet.template.log.enums.JwtActionType.INIT_PUBLIC_KEY;
import static com.arplanet.template.log.enums.JwtActionType.VERIFY_JWT;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtVerifyService {

    @Value("${jwt.public-key1}")
    private Resource publicKeyResource1;

    @Value("${jwt.public-key2}")
    private Resource publicKeyResource2;

    private Map<String, PublicKey> publicKeys;

    private final ObjectMapper mapper;
    private final Logger logger;

    @PostConstruct
    public void init() {
        try {
            publicKeys = new HashMap<>();
            publicKeys.put("key1", loadPublicKey(publicKeyResource1));
            publicKeys.put("key2", loadPublicKey(publicKeyResource2));
        } catch (Exception e) {
            logger.error("Failed to initialize JWT public keys", INIT_PUBLIC_KEY, e, SYSTEM);
            throw new BeanInitializationException("Failed to initialize JWT public keys", e);

        }
    }

    public Claims verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String keyId = signedJWT.getHeader().getKeyID();

            PublicKey publicKey = publicKeys.get(keyId);

            if (publicKey == null) {
                HashMap<String, Object> context = new HashMap<>();
                context.put("key_id", keyId);
                logger.error("Invalid key ID", VERIFY_JWT, context, AUTH);
                throw new JwtException("Invalid key ID");
            }

            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (MalformedJwtException e) {
            logger.error("Invalid token format", VERIFY_JWT, AUTH);
            throw new JwtException("Invalid token format");
        } catch (SignatureException e) {
            log.error("JWT signature verification failed", e);
            throw new JwtException("Invalid token signature");
        } catch (ExpiredJwtException e) {
            log.error("JWT token expired", e);
            throw new JwtException("Token expired");
        } catch (Exception e) {
            log.error("Error verifying JWT token", e);
            throw new JwtException("Token verification failed");
        }
    }

    public Claims extractPayloadWithoutVerification(String token) {
        // 檢查 Token 是否有三部分
        if (token == null || token.split("\\.").length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }

        // 將 token 分成三部分
        String[] chunks = token.split("\\.");

        // base64 decode payload (第二部分)
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload;
        try {
            payload = new String(decoder.decode(chunks[1]));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to decode JWT token payload", e);
        }

        try {
            // 將 Map 轉換為 Claims 物件
            Map<String, Object> map = mapper.readValue(payload, Map.class);

            // 使用 Jwts.builder() 手動建構 Claims 物件
            Claims claims = Jwts.claims(map);
            return claims;

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

    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
