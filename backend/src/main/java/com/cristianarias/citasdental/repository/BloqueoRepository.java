package com.cristianarias.citasdental.repository;

import com.cristianarias.citasdental.entity.Bloqueo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BloqueoRepository extends JpaRepository<Bloqueo, Long> {

    /** Bloqueos que se enciman con el rango [desde, hasta) */
    List<Bloqueo> findByDentistaIdAndInicioLessThanAndFinGreaterThan(
            Long dentistaId, LocalDateTime hasta, LocalDateTime desde);
}