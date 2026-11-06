package com.cristianarias.citasdental.controller;

import com.cristianarias.citasdental.dto.CambiarEstadoRequest;
import com.cristianarias.citasdental.dto.CitaAgendaDto;
import com.cristianarias.citasdental.service.AgendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** Agenda del consultorio. Solo el personal con sesión iniciada tiene acceso (rutas /api/personal/**). */
@RestController
@RequestMapping("/api/personal/citas")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @GetMapping
    public List<CitaAgendaDto> citasDelDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return agendaService.citasDelDia(fecha);
    }

    @PatchMapping("/{id}/estado")
    public CitaAgendaDto cambiarEstado(@PathVariable Long id,
                                       @Valid @RequestBody CambiarEstadoRequest request) {
        return agendaService.cambiarEstado(id, request.estado());
    }
}
