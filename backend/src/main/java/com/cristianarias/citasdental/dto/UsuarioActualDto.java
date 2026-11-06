package com.cristianarias.citasdental.dto;

import java.time.Instant;
import java.util.List;

public record UsuarioActualDto(
        String email,
        String nombre,
        List<String> roles,
        Instant sesionExpiraEn
) {
}
