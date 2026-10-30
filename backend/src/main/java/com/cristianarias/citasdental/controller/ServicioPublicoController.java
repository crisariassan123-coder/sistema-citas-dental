package com.cristianarias.citasdental.controller;

import com.cristianarias.citasdental.dto.ServicioPublicoDto;
import com.cristianarias.citasdental.service.ServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/servicios")
@RequiredArgsConstructor
public class ServicioPublicoController {

    private final ServicioService servicioService;

    @GetMapping
    public List<ServicioPublicoDto> listar() {
        return servicioService.listarReservablesEnLinea();
    }
}