package com.cristianarias.citasdental.service;

import com.cristianarias.citasdental.dto.CitaCreadaDto;
import com.cristianarias.citasdental.dto.CrearCitaRequest;
import com.cristianarias.citasdental.dto.DisponibilidadDto;
import com.cristianarias.citasdental.entity.Cita;
import com.cristianarias.citasdental.entity.Dentista;
import com.cristianarias.citasdental.entity.EstadoCita;
import com.cristianarias.citasdental.entity.Paciente;
import com.cristianarias.citasdental.entity.Servicio;
import com.cristianarias.citasdental.repository.CitaRepository;
import com.cristianarias.citasdental.repository.DentistaRepository;
import com.cristianarias.citasdental.repository.PacienteRepository;
import com.cristianarias.citasdental.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CitaService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String HORARIO_NO_DISPONIBLE = "El horario elegido ya no está disponible";
    private static final ZoneId ZONA_CONSULTORIO = ZoneId.of("America/Mexico_City");

    private final DisponibilidadService disponibilidadService;
    private final DentistaRepository dentistaRepository;
    private final ServicioRepository servicioRepository;
    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;

    @Transactional
    public CitaCreadaDto crear(CrearCitaRequest req) {
        // 1. Se revalida la disponibilidad: horario laboral, bloqueos, citas existentes y horas pasadas
        DisponibilidadDto disponibilidad = disponibilidadService.consultar(
                req.dentistaId(), req.servicioId(), req.inicio().toLocalDate());

        if (!disponibilidad.horariosDisponibles().contains(req.inicio().toLocalTime())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, HORARIO_NO_DISPONIBLE);
        }

        Dentista dentista = dentistaRepository.findById(req.dentistaId()).orElseThrow();
        Servicio servicio = servicioRepository.findById(req.servicioId()).orElseThrow();

        // 2. Se reutiliza al paciente si ya existe con ese teléfono; si no, se crea
        Paciente paciente = pacienteRepository.findFirstByTelefono(req.telefono())
                .orElseGet(() -> {
                    Paciente nuevo = new Paciente();
                    nuevo.setNombre(req.nombre().trim());
                    nuevo.setApellidos(req.apellidos().trim());
                    nuevo.setTelefono(req.telefono());
                    nuevo.setEmail(req.email());
                    return pacienteRepository.save(nuevo);
                });

        // 3. Se arma la cita con un token seguro para el enlace de cancelación
        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setDentista(dentista);
        cita.setServicio(servicio);
        cita.setInicio(req.inicio());
        cita.setFin(req.inicio().plusMinutes(servicio.getDuracionMinutos()));
        cita.setMotivo(req.motivo());
        cita.setTokenPublico(generarToken());

        // 4. Se guarda la cita; si otra persona ocupó el horario un instante antes, PostgreSQL la rechaza
        try {
            citaRepository.saveAndFlush(cita);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, HORARIO_NO_DISPONIBLE);
        }

        return CitaCreadaDto.desde(cita);
    }

    @Transactional(readOnly = true)
    public CitaCreadaDto consultarPorToken(String token) {
        return CitaCreadaDto.desde(buscarPorToken(token));
    }

    @Transactional
    public CitaCreadaDto cancelar(String token) {
        Cita cita = buscarPorToken(token);

        // Solo se cancelan citas activas
        if (cita.getEstado() != EstadoCita.PENDIENTE && cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La cita ya no se puede cancelar");
        }
        // Una cita que ya empezó o ya pasó no se cancela
        if (!cita.getInicio().isAfter(LocalDateTime.now(ZONA_CONSULTORIO))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La cita ya pasó y no se puede cancelar");
        }

        // Al quedar CANCELADA, la cita se ignora en la restricción anti-empalmes y en la disponibilidad: el horario se libera
        cita.setEstado(EstadoCita.CANCELADA);
        return CitaCreadaDto.desde(cita);
    }

    private Cita buscarPorToken(String token) {
        return citaRepository.findByTokenPublico(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    private String generarToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
