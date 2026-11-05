package com.cristianarias.citasdental.service;

import com.cristianarias.citasdental.dto.DentistaPublicoDto;
import com.cristianarias.citasdental.repository.DentistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DentistaService {

    private final DentistaRepository dentistaRepository;

    @Transactional(readOnly = true)
    public List<DentistaPublicoDto> listarActivos() {
        return dentistaRepository.findByActivoTrueOrderByNombreAsc()
                .stream()
                .map(DentistaPublicoDto::desde)
                .toList();
    }
}
