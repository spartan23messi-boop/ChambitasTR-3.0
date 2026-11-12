package com.leonardo.citas.repository;

import com.leonardo.citas.entity.Cita;
import com.leonardo.citas.enums.EstadoCita;
import com.leonardo.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRespository extends JpaRepository<Cita, Long> {

    List<Cita> findByEstadoRegistro (EstadoRegistro estadoRegistro);

    /*


    */

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
            Long idPaciente, EstadoRegistro estadoRegistro, List<EstadoCita> estados);

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
            Long idPaciente, EstadoRegistro estadoRegistro, List<EstadoCita> estados, Long id);

    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
            Long idMedico, EstadoRegistro estadoRegistro, List<EstadoCita> estadoCitas);

    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaInAndIdNot(
            Long idMedico, EstadoRegistro estadoRegistro, List<EstadoCita> estados, Long idExcluido);

}
