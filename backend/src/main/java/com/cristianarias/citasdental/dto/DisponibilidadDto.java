package com.cristianarias.citasdental.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record DisponibilidadDto(
        LocalDate fecha,
        Long dentistaId,
        Long servicioId,
        int duracionMinutos,
        List<LocalTime> horariosDisponibles
) {
}