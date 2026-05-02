package com.example.dcim.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dcim.entity.PlanoSalaElemento;

public interface PlanoSalaElementoRepository extends JpaRepository<PlanoSalaElemento, Long> {

    List<PlanoSalaElemento> findByPlanoSalaIdOrderByIdAsc(Long planoSalaId);

    void deleteByPlanoSalaId(Long planoSalaId);
}
