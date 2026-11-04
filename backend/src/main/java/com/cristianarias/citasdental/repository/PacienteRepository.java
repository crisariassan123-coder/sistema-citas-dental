package com.cristianarias.citasdental.repository;

import com.cristianarias.citasdental.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findFirstByTelefono(String telefono);
}
