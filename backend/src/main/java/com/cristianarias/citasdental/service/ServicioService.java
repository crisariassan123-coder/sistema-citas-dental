package com.cristianarias.citasdental.service;

import com.cristianarias.citasdental.dto.ServicioPublicoDto;
import com.cristianarias.citasdental.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepository;

    @Transactional(readOnly = true)
    public List<ServicioPublicoDto> listarReservablesEnLinea() {
        return servicioRepository.findByActivoTrueAndReservableEnLineaTrueOrderByNombreAsc()
                .stream()
                .map(ServicioPublicoDto::desde)
                .toList();
    }
}