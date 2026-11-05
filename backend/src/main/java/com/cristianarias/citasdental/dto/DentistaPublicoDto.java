package com.cristianarias.citasdental.dto;

import com.cristianarias.citasdental.entity.Dentista;

public record DentistaPublicoDto(
        Long id,
        String nombre,
        String especialidad
) {
    public static DentistaPublicoDto desde(Dentista dentista) {
        return new DentistaPublicoDto(
                dentista.getId(),
                dentista.getNombre(),
                dentista.getEspecialidad()
        );
    }
}
