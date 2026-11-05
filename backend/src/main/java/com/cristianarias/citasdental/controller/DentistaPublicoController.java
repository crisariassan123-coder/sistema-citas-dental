package com.cristianarias.citasdental.controller;

import com.cristianarias.citasdental.dto.DentistaPublicoDto;
import com.cristianarias.citasdental.service.DentistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/dentistas")
@RequiredArgsConstructor
public class DentistaPublicoController {

    private final DentistaService dentistaService;

    @GetMapping
    public List<DentistaPublicoDto> listar() {
        return dentistaService.listarActivos();
    }
}
