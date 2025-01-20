package com.arplanets.template.security;

import com.arplanets.template.log.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
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

import static com.arplanets.template.exception.ErrorType.AUTH;
import static com.arplanets.template.exception.ErrorType.SYSTEM;
import static com.arplanets.template.log.enums.JwtActionType.*;

/**
 * 處理 Jwt 驗證
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtVerifyService {

    @Value("${jwt.public-key1}")
    private Resource publicKeyResource1;

    @Value("${jwt.public-key2}")
    private Resource publicKeyResource2;

    private Map<String, PublicKey> publicKeys;

    private final ObjectMapper objectMapper;

    /**
     * 應用程式啟動時，載入 public key
     */
    @PostConstruct
    public void init() {
        try {
            publicKeys = new HashMap<>();
            publicKeys.put("key1", loadPublicKey(publicKeyResource1));
            publicKeys.put("key2", loadPublicKey(publicKeyResource2));
        } catch (Exception e) {
            Logger.error("Failed to initialize JWT public keys", INIT_PUBLIC_KEY, SYSTEM, e);
            throw new BeanInitializationException("Failed to initialize JWT public keys", e);
        }
    }

    public Claims verifyToken(String token) {
        try {
            // 解析 jwt
            SignedJWT signedJWT = SignedJWT.parse(token);
            // 取得 key id
            String keyId = signedJWT.getHeader().getKeyID();
            // 由 key id 找到對應的 public key
            PublicKey publicKey = publicKeys.get(keyId);

            // 若對應的 public key 不存在，拋出異常
            if (publicKey == null) {
                HashMap<String, Object> context = new HashMap<>();
                context.put("key_id", keyId);
                Logger.error("Invalid key ID", VERIFY_JWT, AUTH, context);
                throw new JwtException("Invalid key ID");
            }

            // 取得 payload 資訊
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (MalformedJwtException e) {
            Logger.error("Invalid token format", VERIFY_JWT, AUTH);
            throw new JwtException("Invalid token format");
        } catch (SignatureException e) {
            Logger.error("JWT signature verification failed", VERIFY_JWT, AUTH);
            throw new JwtException("Invalid token signature");
        } catch (ExpiredJwtException e) {
            Logger.error("JWT token expired", VERIFY_JWT, AUTH);
            throw new JwtException("Token expired");
        } catch (JwtException e) {
            Logger.error("Unexpected JWT error occurred", VERIFY_JWT, AUTH);
            throw new JwtException("Unexpected JWT error");
        } catch (Exception e) {
            Logger.error("Unexpected error occurred", VERIFY_JWT, SYSTEM, e);
            throw new JwtException("Unexpected error");
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

    public Claims extractPayloadWithoutVerification(String token) {
        // 檢查 Token 是否有三部分
        if (token == null || token.split("\\.").length != 3) {
            Logger.error("Invalid JWT token format", EXTRACT_PAYLOAD_WITHOUT_VERIFICATION, SYSTEM);
            return null;
        }

        // 將 token 分成三部分
        String[] chunks = token.split("\\.");

        // base64 decode payload (第二部分)
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload;
        try {
            payload = new String(decoder.decode(chunks[1]));
        } catch (IllegalArgumentException e) {
            Logger.error("Failed to decode JWT token payload", DECODE_PAYLOAD, SYSTEM);
            return null;
        }

        try {
            // 將 Map 轉換為 Claims 物件
            Map<String, Object> map = objectMapper.readValue(payload, new TypeReference<>() {
            });

            return new DefaultClaims(map);

        } catch (Exception e) {
            Logger.error("Failed to parse token payload", PARSE_PAYLOAD, SYSTEM);
            return null;
        }
    }
}
