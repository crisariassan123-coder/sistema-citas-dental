package com.cristianarias.citasdental.repository;

import com.cristianarias.citasdental.entity.HorarioLaboral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioLaboralRepository extends JpaRepository<HorarioLaboral, Long> {

    List<HorarioLaboral> findByDentistaIdAndDiaSemanaOrderByHoraInicioAsc(Long dentistaId, Short diaSemana);
}