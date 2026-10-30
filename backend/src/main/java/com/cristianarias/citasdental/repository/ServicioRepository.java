package com.cristianarias.citasdental.repository;

import com.cristianarias.citasdental.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByActivoTrueAndReservableEnLineaTrueOrderByNombreAsc();
}