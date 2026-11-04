package com.cristianarias.citasdental.repository;

import com.cristianarias.citasdental.entity.Cita;
import com.cristianarias.citasdental.entity.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    @Query("""
            SELECT c FROM Cita c
            WHERE c.dentista.id = :dentistaId
              AND c.estado <> :estadoExcluido
              AND c.inicio < :hasta
              AND c.fin > :desde
            """)
    List<Cita> findActivasEntre(@Param("dentistaId") Long dentistaId,
                                @Param("estadoExcluido") EstadoCita estadoExcluido,
                                @Param("desde") LocalDateTime desde,
                                @Param("hasta") LocalDateTime hasta);
}