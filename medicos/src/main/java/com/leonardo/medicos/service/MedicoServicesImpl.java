package com.leonardo.medicos.service;

import com.leonardo.commons.clients.CitaClient;
import com.leonardo.commons.dto.medicos.MedicoRequest;
import com.leonardo.commons.dto.medicos.MedicoResponse;
import com.leonardo.commons.enums.DisponibilidadMedico;
import com.leonardo.commons.enums.EspecialidadMedico;
import com.leonardo.commons.enums.EstadoRegistro;
import com.leonardo.commons.exceptions.ConflictoException;
import com.leonardo.commons.exceptions.RecursoNoEncontradoException;
import com.leonardo.medicos.entity.Medico;
import com.leonardo.medicos.mapper.MedicoMapper;
import com.leonardo.medicos.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MedicoServicesImpl implements MedicoServices {

    private final MedicoRepository medicoRepository;

    private final MedicoMapper medicoMapper;

    private final CitaClient citaClient;

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {

        log.info("Listando todos los médicos activos");

        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(Long id) {
        return medicoMapper.entidadAResponse(obtenerMedicoActivoPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {

        log.info("Buscando médico sin estado con id {}", id);

        return medicoMapper.entidadAResponse(medicoRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "Médico sin estado no encontrado con id: " + id)));
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {

        if (request == null) throw new com.leonardo.commons.exceptions.DatoInvalidoException("Los datos del médico son requeridos");
        log.info("Registrando nuevo médico: {}", request.nombre());

        validarDatosUnicos(request);

        Medico medico = medicoMapper.requestAEntidad(request);

        medicoRepository.saveAndFlush(medico);

        log.info("Nuevo médico registrado: {}", medico.obtenerNombreCompleto());

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {

        if (request == null) throw new com.leonardo.commons.exceptions.DatoInvalidoException("Los datos del médico son requeridos");
        Medico medico = obtenerMedicoActivoPorId(id);

        validarMedicoTieneCitasAgendadas(id);

        validarCambiosUnicos(request, id);

        log.info("Actualizando médico con id: {}", id);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        medicoRepository.saveAndFlush(medico);

        log.info("Médico con id {} actualizado correctamente", id);

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {

        Medico medico = obtenerMedicoActivoPorId(idMedico);

        log.info("Actualizando disponibilidad del médico con id: {}", idMedico);

        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico
                .obtenerDisponibilidadPorCodigo(idDisponibilidad);

        DisponibilidadMedico disponibilidadAnterior = medico.getDisponibilidad();

        medico.actualizarDisponibilidad(nuevaDisponibilidad);

        medicoRepository.saveAndFlush(medico);

        log.info("Disponibilidad del médico con id {} cambió de {} a {}",
                idMedico, disponibilidadAnterior, nuevaDisponibilidad);
    }

    @Override
    public void eliminar(Long id) {

        Medico medico = obtenerMedicoActivoPorId(id);

        log.info("Eliminando al médico con id: {}", id);

        citaClient.validarEliminacionMedico(id);

        medico.eliminar();

        medicoRepository.saveAndFlush(medico);

        log.info("Médico eliminado lógicamente");
    }

    private Medico obtenerMedicoActivoPorId(Long id) {

        log.info("Buscando médico activo con id {}", id);

        return medicoRepository.findByIdMedicoAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Médico activo no encontrado con id: " + id));
    }

    private void validarDatosUnicos(MedicoRequest request) {

        log.info("Validando email único...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException(
                    "Ya existe un médico activo registrado con el email: " + request.email());

        log.info("Validando teléfono único...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException(
                    "Ya existe un médico activo registrado con el teléfono: " + request.telefono());

        log.info("Validando cédula profesional única...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException(
                    "Ya existe un médico activo registrado con la cédula profesional: "
                            + request.cedulaProfesional());
    }

    private void validarCambiosUnicos(MedicoRequest request, Long id) {

        log.info("Validando cambio en email único...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdMedicoNot(
                request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException(
                    "Ya existe un médico activo registrado con el email: " + request.email());

        log.info("Validando cambio en teléfono único...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistroAndIdMedicoNot(
                request.telefono().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException(
                    "Ya existe un médico activo registrado con el teléfono: " + request.telefono());

        log.info("Validando cambio en cédula profesional única...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdMedicoNot(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException(
                    "Ya existe un médico activo registrado con la cédula profesional: "
                            + request.cedulaProfesional());
    }

    private void validarMedicoTieneCitasAgendadas(Long id) {

        log.info("Validando si el médico con id {} " +
                "tiene citas agendadas en servicio remoto...", id);

        citaClient.medicoTieneCitasAsignadas(id);
    }
}
