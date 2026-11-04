package com.cristianarias.citasdental.service;

import com.cristianarias.citasdental.dto.DisponibilidadDto;
import com.cristianarias.citasdental.entity.Dentista;
import com.cristianarias.citasdental.entity.EstadoCita;
import com.cristianarias.citasdental.entity.HorarioLaboral;
import com.cristianarias.citasdental.entity.Servicio;
import com.cristianarias.citasdental.repository.BloqueoRepository;
import com.cristianarias.citasdental.repository.CitaRepository;
import com.cristianarias.citasdental.repository.DentistaRepository;
import com.cristianarias.citasdental.repository.HorarioLaboralRepository;
import com.cristianarias.citasdental.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisponibilidadService {

    private static final ZoneId ZONA_CONSULTORIO = ZoneId.of("America/Mexico_City");
    private static final int INTERVALO_MINUTOS = 15;

    private final DentistaRepository dentistaRepository;
    private final ServicioRepository servicioRepository;
    private final HorarioLaboralRepository horarioLaboralRepository;
    private final BloqueoRepository bloqueoRepository;
    private final CitaRepository citaRepository;

    @Transactional(readOnly = true)
    public DisponibilidadDto consultar(Long dentistaId, Long servicioId, LocalDate fecha) {
        Dentista dentista = dentistaRepository.findById(dentistaId)
                .filter(Dentista::isActivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Dentista no encontrado"));

        Servicio servicio = servicioRepository.findById(servicioId)
                .filter(s -> s.isActivo() && s.isReservableEnLinea())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Servicio no disponible para reservar en línea"));

        Duration duracion = Duration.ofMinutes(servicio.getDuracionMinutos());
        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        LocalDateTime finDelDia = fecha.plusDays(1).atStartOfDay();

        // 1. Horario laboral del dentista para ese día de la semana
        short diaSemana = (short) fecha.getDayOfWeek().getValue();
        List<HorarioLaboral> horarios = horarioLaboralRepository
                .findByDentistaIdAndDiaSemanaOrderByHoraInicioAsc(dentista.getId(), diaSemana);

        // 2. Todo lo que ya ocupa tiempo: citas activas y bloqueos
        List<Intervalo> ocupados = new ArrayList<>();
        citaRepository.findActivasEntre(dentista.getId(), EstadoCita.CANCELADA, inicioDelDia, finDelDia)
                .forEach(c -> ocupados.add(new Intervalo(c.getInicio(), c.getFin())));
        bloqueoRepository.findByDentistaIdAndInicioLessThanAndFinGreaterThan(
                        dentista.getId(), finDelDia, inicioDelDia)
                .forEach(b -> ocupados.add(new Intervalo(b.getInicio(), b.getFin())));

        // 3. Recorrer el horario cada 15 minutos y quedarse con los huecos donde cabe el servicio
        LocalDateTime ahora = LocalDateTime.now(ZONA_CONSULTORIO);
        List<LocalTime> disponibles = new ArrayList<>();

        for (HorarioLaboral horario : horarios) {
            LocalDateTime inicio = fecha.atTime(horario.getHoraInicio());
            LocalDateTime limite = fecha.atTime(horario.getHoraFin());

            while (!inicio.plus(duracion).isAfter(limite)) {
                LocalDateTime fin = inicio.plus(duracion);
                // 4. Descartar horas que ya pasaron o que chocan con algo
                if (inicio.isAfter(ahora) && estaLibre(inicio, fin, ocupados)) {
                    disponibles.add(inicio.toLocalTime());
                }
                inicio = inicio.plusMinutes(INTERVALO_MINUTOS);
            }
        }

        return new DisponibilidadDto(
                fecha, dentista.getId(), servicio.getId(), servicio.getDuracionMinutos(), disponibles);
    }

    private boolean estaLibre(LocalDateTime inicio, LocalDateTime fin, List<Intervalo> ocupados) {
        return ocupados.stream()
                .noneMatch(o -> inicio.isBefore(o.fin()) && fin.isAfter(o.inicio()));
    }

    private record Intervalo(LocalDateTime inicio, LocalDateTime fin) {
    }
}