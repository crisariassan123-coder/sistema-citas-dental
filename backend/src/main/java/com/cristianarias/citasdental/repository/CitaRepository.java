package com.cristianarias.citasdental.repository;

import com.cristianarias.citasdental.entity.Cita;
import com.cristianarias.citasdental.entity.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    Optional<Cita> findByTokenPublico(String tokenPublico);

    /**
     * Citas de un rango de fechas con su paciente, servicio y dentista.
     * Con JOIN FETCH todo se obtiene en una sola consulta y se evita el problema N+1
     * (una consulta extra por cada cita para traer sus datos relacionados).
     */
    @Query("""
            SELECT c FROM Cita c
            JOIN FETCH c.paciente
            JOIN FETCH c.servicio
            JOIN FETCH c.dentista
            WHERE c.inicio >= :desde
              AND c.inicio < :hasta
            ORDER BY c.inicio
            """)
    List<Cita> findDelRangoConDetalle(@Param("desde") LocalDateTime desde,
                                      @Param("hasta") LocalDateTime hasta);
}
