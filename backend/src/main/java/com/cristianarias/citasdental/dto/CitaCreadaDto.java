package com.cristianarias.citasdental.dto;

import com.cristianarias.citasdental.entity.Cita;
import com.cristianarias.citasdental.entity.EstadoCita;

import java.time.LocalDateTime;

public record CitaCreadaDto(
        String token,
        String dentista,
        String servicio,
        LocalDateTime inicio,
        LocalDateTime fin,
        EstadoCita estado
) {
    public static CitaCreadaDto desde(Cita cita) {
        return new CitaCreadaDto(
                cita.getTokenPublico(),
                cita.getDentista().getNombre(),
                cita.getServicio().getNombre(),
                cita.getInicio(),
                cita.getFin(),
                cita.getEstado()
        );
    }
}
