package com.leonardo.citas.service;

import com.leonardo.citas.dto.CitaRequest;
import com.leonardo.citas.dto.CitaResponse;
import com.leonardo.citas.entity.Cita;
import com.leonardo.citas.enums.EstadoCita;
import com.leonardo.citas.mapper.CitaMapper;
import com.leonardo.citas.repository.CitaRespository;
import com.leonardo.commons.clients.MedicoClient;
import com.leonardo.commons.clients.PacienteClient;
import com.leonardo.commons.dto.pacientes.PacienteResponse;
import com.leonardo.commons.dto.medicos.MedicoResponse;
import com.leonardo.commons.enums.DisponibilidadMedico;
import com.leonardo.commons.enums.EstadoRegistro;
import com.leonardo.commons.exceptions.ConflictoException;
import com.leonardo.commons.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService {

    private final CitaRespository citaRepository;

    private final CitaMapper citaMapper;

    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    private static final List<EstadoCita> ESTADOS_PACIENTE_OCUPADO =
            List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO);

    private static final List<EstadoCita> ESTADOS_ACTIVOS_INVALIDOS =
            List.of(EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO);

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {

        log.info("Listando todas las citas activas...");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        pacienteClient.obtenerPacienteSinEstadoPorId(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())
                )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {

        Cita cita = obtenerCitaOException(id);

        return citaMapper.entidadAResponse(cita,
                pacienteClient.obtenerPacienteSinEstadoPorId(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    @Transactional(readOnly = true)
    public void pacienteTieneCitasAsignadas(Long idPaciente) {
        if (citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente, EstadoRegistro.ACTIVO, ESTADOS_ACTIVOS_INVALIDOS)) {
            throw new ConflictoException("No se puede modificar o eliminar el paciente: tiene una cita confirmada o en curso");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void medicoTieneCitasAsignadas(Long idMedico, Long idCitaExcluida) {

        log.info("Validando si el médico tiene una cita activa con los estados: {}",
                ESTADOS_ACTIVOS_INVALIDOS);

        boolean ocupado = idCitaExcluida == null
                ? citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                        idMedico, EstadoRegistro.ACTIVO, ESTADOS_ACTIVOS_INVALIDOS)
                : citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaInAndIdNot(
                        idMedico, EstadoRegistro.ACTIVO, ESTADOS_ACTIVOS_INVALIDOS, idCitaExcluida);
        if (ocupado) {
            throw new ConflictoException("No se puede modificar o eliminar el médico ya que " +
                    "tiene citas en estado: " + ESTADOS_ACTIVOS_INVALIDOS);
        }
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando nueva cita...");

        PacienteResponse paciente = pacienteClient.obtenerPacienteActivoPorId(request.idPaciente());
        validarPacienteSinCitasActivas(request.idPaciente(), null);
        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        validarMedicoActivoDisponible(medico);

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.saveAndFlush(cita);

        log.info("Cita registrada");

        cambiarDisponibilidadMedicoSegunEstadoCita(
                medico.id(), cita.getEstadoCita(), cita.getId());

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico);
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {

        Cita cita = obtenerCitaOException(id);

        if (cita.getEstadoCita() == EstadoCita.EN_CURSO)
            throw new ConflictoException("La cita está en curso y no puede actualizarse");

        PacienteResponse paciente = pacienteClient.obtenerPacienteActivoPorId(request.idPaciente());
        if (!request.idPaciente().equals(cita.getIdPaciente())) {
            validarPacienteSinCitasActivas(request.idPaciente(), id);
        }
        boolean cambiaMedico = !request.idMedico().equals(cita.getIdMedico());
        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        if (cambiaMedico) validarMedicoActivoDisponible(medico);
        Long idMedicoAnterior = cita.getIdMedico();

        log.info("Actualizando cita con id: {}", id);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());

        citaRepository.saveAndFlush(cita);

        log.info("Cita actualizada");
        if (cambiaMedico) {
            actualizarDisponibilidadMedico(idMedicoAnterior, DisponibilidadMedico.DISPONIBLE.getCodigo(), cita.getId());
            cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita(), cita.getId());
        }

        return citaMapper.entidadAResponse(cita, paciente, medico);
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {

        Cita cita = obtenerCitaOException(idCita);

        log.info("Actualizando estado de la cita con id: {}", idCita);

        cita.actualizarEstadoCita(
                EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita));

        citaRepository.saveAndFlush(cita);

        log.info("Estado de la cita {} actualizado", idCita);

        cambiarDisponibilidadMedicoSegunEstadoCita(
                cita.getIdMedico(),
                cita.getEstadoCita(), cita.getId());
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCitaOException(id);

        log.info("Eliminando cita con id: {}", id);

        cita.eliminar();

        citaRepository.saveAndFlush(cita);

        log.info("Cita con id {} ha sido marcada como eliminada", id);

        if (cita.getEstadoCita() == EstadoCita.PENDIENTE)
            actualizarDisponibilidadMedico(
                    cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE.getCodigo(), cita.getId());
    }

    private void validarPacienteSinCitasActivas(Long idPaciente, Long idCitaExcluida) {
        boolean ocupado = idCitaExcluida == null
                ? citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                    idPaciente, EstadoRegistro.ACTIVO, ESTADOS_PACIENTE_OCUPADO)
                : citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
                    idPaciente, EstadoRegistro.ACTIVO, ESTADOS_PACIENTE_OCUPADO, idCitaExcluida);
        if (ocupado) throw new ConflictoException("El paciente ya tiene una cita activa pendiente, confirmada o en curso");
    }

    private Cita obtenerCitaOException(Long id) {

        log.info("Buscando cita con id: {}", id);

        return citaRepository.findById(id)
                .filter(cita -> cita.getEstadoRegistro() == EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cita activa no encontrada con id: " + id));
    }

    private MedicoResponse obtenerMedicoActivo(Long id) {

        log.info("Buscando médico activo con id {} en el servicio remoto...", id);

        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id) {

        log.info("Buscando médico sin estado con id {} en el servicio remoto...", id);

        return medicoClient.obtenerMedicoSinEstadoPorId(id);
    }

    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad, Long idCitaExcluida) {

        log.info("Actualizando disponibilidad del médico en el servicio remoto...");

        medicoClient.actualizarDisponibilidadMedico(idMedico, idDisponibilidad, idCitaExcluida);

        log.info("Disponibilidad del médico actualizada en el servicio remoto");
    }

    private void validarMedicoActivoDisponible(MedicoResponse medico) {

        log.info("Validando si el médico activo está disponible...");

        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw new ConflictoException("El médico activo no está disponible para consulta");
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita, Long idCitaExcluida) {

        switch (estadoCita) {

            case PENDIENTE, CONFIRMADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.NO_DISPONIBLE.getCodigo(), idCitaExcluida);

            case FINALIZADA, CANCELADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo(), idCitaExcluida);

            case EN_CURSO ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo(), idCitaExcluida);
        }
    }
}
