package com.cristianarias.citasdental.controller;

import com.cristianarias.citasdental.dto.UsuarioActualDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personal")
public class PersonalController {

    /** Se devuelven los datos del usuario que inició sesión, tomados del token JWT. */
    @GetMapping("/yo")
    public UsuarioActualDto yo(@AuthenticationPrincipal Jwt jwt) {
        return new UsuarioActualDto(
                jwt.getSubject(),
                jwt.getClaimAsString("nombre"),
                jwt.getClaimAsStringList("roles"),
                jwt.getExpiresAt()
        );
    }
}
