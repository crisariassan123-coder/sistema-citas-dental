package com.cristianarias.citasdental.dto;

import com.cristianarias.citasdental.entity.Rol;

import java.time.Instant;

public record LoginResponse(
        String token,
        String tipo,
        Instant expiraEn,
        String nombre,
        Rol rol
) {
}
