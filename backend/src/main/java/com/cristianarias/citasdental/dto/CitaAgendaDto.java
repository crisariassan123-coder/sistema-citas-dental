package com.cristianarias.citasdental.dto;

import com.cristianarias.citasdental.entity.Cita;
import com.cristianarias.citasdental.entity.EstadoCita;
import com.cristianarias.citasdental.entity.Paciente;

import java.time.LocalDateTime;

/** Datos de una cita que se muestran en la agenda del personal (incluye los datos de contacto del paciente). */
public record CitaAgendaDto(
        Long id,
        LocalDateTime inicio,
        LocalDateTime fin,
        EstadoCita estado,
        String servicio,
        String dentista,
        String paciente,
        String telefono,
        String email,
        String motivo
) {
    public static CitaAgendaDto desde(Cita cita) {
        Paciente paciente = cita.getPaciente();
        return new CitaAgendaDto(
                cita.getId(),
                cita.getInicio(),
                cita.getFin(),
                cita.getEstado(),
                cita.getServicio().getNombre(),
                cita.getDentista().getNombre(),
                paciente.getNombre() + " " + paciente.getApellidos(),
                paciente.getTelefono(),
                paciente.getEmail(),
                cita.getMotivo()
        );
    }
}
