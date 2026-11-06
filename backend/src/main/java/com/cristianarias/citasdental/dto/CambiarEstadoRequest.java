package com.cristianarias.citasdental.dto;

import com.cristianarias.citasdental.entity.EstadoCita;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(
        @NotNull EstadoCita estado
) {
}
