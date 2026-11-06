package com.cristianarias.citasdental.service;

import com.cristianarias.citasdental.dto.CitaAgendaDto;
import com.cristianarias.citasdental.entity.Cita;
import com.cristianarias.citasdental.entity.EstadoCita;
import com.cristianarias.citasdental.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private static final ZoneId ZONA_CONSULTORIO = ZoneId.of("America/Mexico_City");

    // Cambios de estado permitidos. ATENDIDA, CANCELADA y NO_ASISTIO son estados finales.
    private static final Map<EstadoCita, Set<EstadoCita>> TRANSICIONES = Map.of(
            EstadoCita.PENDIENTE, Set.of(EstadoCita.CONFIRMADA, EstadoCita.ATENDIDA,
                    EstadoCita.NO_ASISTIO, EstadoCita.CANCELADA),
            EstadoCita.CONFIRMADA, Set.of(EstadoCita.ATENDIDA, EstadoCita.NO_ASISTIO, EstadoCita.CANCELADA),
            EstadoCita.ATENDIDA, Set.of(),
            EstadoCita.CANCELADA, Set.of(),
            EstadoCita.NO_ASISTIO, Set.of()
    );

    private final CitaRepository citaRepository;

    @Transactional(readOnly = true)
    public List<CitaAgendaDto> citasDelDia(LocalDate fecha) {
        return citaRepository.findDelRangoConDetalle(fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay())
                .stream()
                .map(CitaAgendaDto::desde)
                .toList();
    }

    @Transactional
    public CitaAgendaDto cambiarEstado(Long id, EstadoCita nuevoEstado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));

        EstadoCita actual = cita.getEstado();
        if (!TRANSICIONES.get(actual).contains(nuevoEstado)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede cambiar una cita de %s a %s".formatted(actual, nuevoEstado));
        }

        boolean yaEmpezo = !cita.getInicio().isAfter(LocalDateTime.now(ZONA_CONSULTORIO));

        // Solo se registra la asistencia cuando ya llegó la hora de la cita
        if ((nuevoEstado == EstadoCita.ATENDIDA || nuevoEstado == EstadoCita.NO_ASISTIO) && !yaEmpezo) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La asistencia solo se registra cuando ya llegó la hora de la cita");
        }
        // Una cita que ya empezó no se cancela: se registra como atendida o como no asistida
        if (nuevoEstado == EstadoCita.CANCELADA && yaEmpezo) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La cita ya empezó; se debe registrar como atendida o como no asistida");
        }

        cita.setEstado(nuevoEstado);
        return CitaAgendaDto.desde(cita);
    }
}
