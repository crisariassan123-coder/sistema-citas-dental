package com.cristianarias.citasdental.service;

import com.cristianarias.citasdental.dto.CitaCreadaDto;
import com.cristianarias.citasdental.dto.CrearCitaRequest;
import com.cristianarias.citasdental.dto.DisponibilidadDto;
import com.cristianarias.citasdental.entity.Cita;
import com.cristianarias.citasdental.entity.Dentista;
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
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CitaService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String HORARIO_NO_DISPONIBLE = "El horario elegido ya no está disponible";

    private final DisponibilidadService disponibilidadService;
    private final DentistaRepository dentistaRepository;
    private final ServicioRepository servicioRepository;
    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;

    @Transactional
    public CitaCreadaDto crear(CrearCitaRequest req) {
        // 1. Revalidar disponibilidad: horario laboral, bloqueos, citas existentes y horas pasadas
        DisponibilidadDto disponibilidad = disponibilidadService.consultar(
                req.dentistaId(), req.servicioId(), req.inicio().toLocalDate());

        if (!disponibilidad.horariosDisponibles().contains(req.inicio().toLocalTime())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, HORARIO_NO_DISPONIBLE);
        }

        Dentista dentista = dentistaRepository.findById(req.dentistaId()).orElseThrow();
        Servicio servicio = servicioRepository.findById(req.servicioId()).orElseThrow();

        // 2. Reutilizar al paciente si ya existe con ese teléfono, o crearlo
        Paciente paciente = pacienteRepository.findFirstByTelefono(req.telefono())
                .orElseGet(() -> {
                    Paciente nuevo = new Paciente();
                    nuevo.setNombre(req.nombre().trim());
                    nuevo.setApellidos(req.apellidos().trim());
                    nuevo.setTelefono(req.telefono());
                    nuevo.setEmail(req.email());
                    return pacienteRepository.save(nuevo);
                });

        // 3. Armar la cita con un token seguro para el enlace de cancelar
        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setDentista(dentista);
        cita.setServicio(servicio);
        cita.setInicio(req.inicio());
        cita.setFin(req.inicio().plusMinutes(servicio.getDuracionMinutos()));
        cita.setMotivo(req.motivo());
        cita.setTokenPublico(generarToken());

        // 4. Guardar; si otra persona ganó el horario un instante antes, PostgreSQL lo rechaza
        try {
            citaRepository.saveAndFlush(cita);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, HORARIO_NO_DISPONIBLE);
        }

        return CitaCreadaDto.desde(cita);
    }

    private String generarToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
