package com.cristianarias.citasdental.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Configuración de la firma y validación de los tokens JWT (algoritmo HS256).
 * El secreto se toma de la variable JWT_SECRET del archivo .env, nunca del código.
 */
@Configuration
public class JwtConfig {

    public static final String EMISOR = "citas-dental";

    @Bean
    public SecretKey claveJwt(@Value("${app.jwt.secreto}") String secreto) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        // HS256 requiere una clave de al menos 256 bits (32 bytes); si es más corta, el arranque se detiene
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey claveJwt) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(claveJwt));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey claveJwt) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(claveJwt)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        // Se valida la firma, la fecha de expiración y que el token haya sido emitido por este sistema
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(EMISOR));
        return decoder;
    }
}
