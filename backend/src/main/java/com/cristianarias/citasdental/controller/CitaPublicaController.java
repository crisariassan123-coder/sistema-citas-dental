package com.cristianarias.citasdental.controller;

import com.cristianarias.citasdental.dto.CitaCreadaDto;
import com.cristianarias.citasdental.dto.CrearCitaRequest;
import com.cristianarias.citasdental.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/citas")
@RequiredArgsConstructor
public class CitaPublicaController {

    private final CitaService citaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitaCreadaDto crear(@Valid @RequestBody CrearCitaRequest request) {
        return citaService.crear(request);
    }

    @GetMapping("/{token}")
    public CitaCreadaDto consultar(@PathVariable String token) {
        return citaService.consultarPorToken(token);
    }

    @PatchMapping("/{token}/cancelar")
    public CitaCreadaDto cancelar(@PathVariable String token) {
        return citaService.cancelar(token);
    }
}
