package com.leonardo.pacientes.service;

import com.leonardo.commons.clients.CitaClient;
import com.leonardo.commons.dto.pacientes.PacienteRequest;
import com.leonardo.commons.dto.pacientes.PacienteResponse;
import com.leonardo.commons.enums.EstadoRegistro;
import com.leonardo.commons.exceptions.ConflictoException;
import com.leonardo.commons.exceptions.DatoInvalidoException;
import com.leonardo.commons.exceptions.RecursoNoEncontradoException;
import com.leonardo.pacientes.entity.Paciente;
import com.leonardo.pacientes.mapper.PacienteMapper;
import com.leonardo.pacientes.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService {

    private final CitaClient citaClient;
    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        log.info("Consultando pacientes activos");
        return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(pacienteMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {
        log.info("Consultando paciente activo {}", id);
        return pacienteMapper.entidadAResponse(obtenerPacienteActivo(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
        log.info("Consultando paciente {} sin filtrar estado", id);
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un paciente con el ID: " + id));
        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        validarRequest(request);
        Paciente paciente = pacienteMapper.requestAEntidad(request);
        validarUnicidad(request, null);

        log.info("Registrando paciente {}", paciente.obtenerNombreCompleto());
        return guardarYResponder(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        validarRequest(request);
        Paciente paciente = obtenerPacienteActivo(id);
        citaClient.pacienteTieneCitasAsignadas(id);
        validarUnicidad(request, id);

        paciente.actualizar(
                request.nombre(), request.apellidoPaterno(), request.apellidoMaterno(),
                request.edad(), request.peso(), request.estatura(), request.email(),
                request.telefono(), request.direccion());

        log.info("Actualizando paciente {}", id);
        return guardarYResponder(paciente);
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = obtenerPacienteActivo(id);
        citaClient.pacienteTieneCitasAsignadas(id);


        paciente.eliminar();
        pacienteRepository.saveAndFlush(paciente);
        log.info("Paciente {} eliminado lógicamente", id);
    }

    private Paciente obtenerPacienteActivo(Long id) {
        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un paciente activo con el ID: " + id));
    }

    private void validarRequest(PacienteRequest request) {
        if (request == null) {
            throw new DatoInvalidoException("Los datos del paciente son requeridos");
        }
    }

    private void validarUnicidad(PacienteRequest request, Long id) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String telefono = request.telefono().trim();
        boolean emailDuplicado = id == null
                ? pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(email, EstadoRegistro.ACTIVO)
                : pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                        email, EstadoRegistro.ACTIVO, id);
        if (emailDuplicado) {
            throw new ConflictoException("Ya existe un paciente activo con el email: " + email);
        }

        boolean telefonoDuplicado = id == null
                ? pacienteRepository.existsByTelefonoAndEstadoRegistro(telefono, EstadoRegistro.ACTIVO)
                : pacienteRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                        telefono, EstadoRegistro.ACTIVO, id);
        if (telefonoDuplicado) {
            throw new ConflictoException("Ya existe un paciente activo con el teléfono: " + telefono);
        }
    }

    private PacienteResponse guardarYResponder(Paciente paciente) {
        Paciente guardado = pacienteRepository.saveAndFlush(paciente);
        log.info("Paciente {} guardado", guardado.getId());
        return pacienteMapper.entidadAResponse(guardado);
    }
}
