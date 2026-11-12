package com.leonardo.medicos.repository;

import com.leonardo.commons.enums.EstadoRegistro;
import com.leonardo.medicos.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    List<Medico> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    Optional<Medico> findByIdMedicoAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);

    boolean existsByEmailIgnoreCaseAndEstadoRegistro(String email, EstadoRegistro estadoRegistro);

    boolean existsByTelefonoAndEstadoRegistro(String telefono, EstadoRegistro estadoRegistro);

    boolean existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(String cedula, EstadoRegistro estadoRegistro);

    boolean existsByEmailIgnoreCaseAndEstadoRegistroAndIdMedicoNot(String email, EstadoRegistro estadoRegistro, Long id);

    boolean existsByTelefonoAndEstadoRegistroAndIdMedicoNot(String telefono, EstadoRegistro estadoRegistro, Long id);

    boolean existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdMedicoNot(String cedula, EstadoRegistro estadoRegistro, Long id);
}