package com.leonardo.pacientes.mapper;

import com.leonardo.commons.dto.pacientes.PacienteRequest;
import com.leonardo.commons.dto.pacientes.PacienteResponse;
import com.leonardo.commons.mapper.CommonMapper;
import com.leonardo.pacientes.entity.Paciente;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteResponse, Paciente> {
    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        return request == null ? null : Paciente.crear(
                request.nombre(), request.apellidoPaterno(), request.apellidoMaterno(),
                request.edad(), request.peso(), request.estatura(),
                request.email(), request.telefono(), request.direccion());
    }

    @Override
    public PacienteResponse entidadAResponse(Paciente entidad) {
        return entidad == null ? null : new PacienteResponse(
                entidad.getId(), entidad.obtenerNombreCompleto(), entidad.getEdad(),
                entidad.getPeso(), entidad.getEstatura(),
                entidad.getImc(), entidad.getEmail(), entidad.getTelefono(),
                entidad.getDireccion(), entidad.getNumExpediente());
    }
}
