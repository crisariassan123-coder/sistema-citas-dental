package com.cristianarias.citasdental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CrearCitaRequest(
        @NotNull Long dentistaId,
        @NotNull Long servicioId,
        @NotNull @Future LocalDateTime inicio,
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 100) String apellidos,
        @NotBlank @Pattern(regexp = "\\d{10}", message = "debe tener 10 dígitos") String telefono,
        @Email @Size(max = 150) String email,
        @Size(max = 255) String motivo
) {
}
