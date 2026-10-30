package com.cristianarias.citasdental.dto;

import com.cristianarias.citasdental.entity.Servicio;

import java.math.BigDecimal;

public record ServicioPublicoDto(
        Long id,
        String nombre,
        String descripcion,
        Integer duracionMinutos,
        BigDecimal precio
) {
    public static ServicioPublicoDto desde(Servicio servicio) {
        return new ServicioPublicoDto(
                servicio.getId(),
                servicio.getNombre(),
                servicio.getDescripcion(),
                servicio.getDuracionMinutos(),
                servicio.getPrecio()
        );
    }
}