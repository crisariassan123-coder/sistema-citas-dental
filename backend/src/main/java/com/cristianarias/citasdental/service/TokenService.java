package com.cristianarias.citasdental.service;

import com.cristianarias.citasdental.config.JwtConfig;
import com.cristianarias.citasdental.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long minutosExpiracion;

    public TokenService(JwtEncoder jwtEncoder,
                        @Value("${app.jwt.minutos-expiracion:60}") long minutosExpiracion) {
        this.jwtEncoder = jwtEncoder;
        this.minutosExpiracion = minutosExpiracion;
    }

    public TokenGenerado generar(Usuario usuario) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(minutosExpiracion, ChronoUnit.MINUTES);

        // En el token solo se incluyen datos necesarios para autorizar; nunca la contraseña ni su hash
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(JwtConfig.EMISOR)
                .issuedAt(ahora)
                .expiresAt(expira)
                .subject(usuario.getEmail())
                .claim("nombre", usuario.getNombre())
                .claim("roles", List.of(usuario.getRol().name()))
                .build();

        JwsHeader encabezado = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(encabezado, claims)).getTokenValue();

        return new TokenGenerado(token, expira);
    }

    public record TokenGenerado(String valor, Instant expiraEn) {
    }
}
