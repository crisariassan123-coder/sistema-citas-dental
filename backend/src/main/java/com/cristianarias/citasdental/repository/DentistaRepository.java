package com.cristianarias.citasdental.repository;

import com.cristianarias.citasdental.entity.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DentistaRepository extends JpaRepository<Dentista, Long> {

    List<Dentista> findByActivoTrueOrderByNombreAsc();
}
