package com.leonardo.medicos.mapper;

import com.leonardo.commons.dto.medicos.MedicoRequest;
import com.leonardo.commons.dto.medicos.MedicoResponse;
import com.leonardo.commons.enums.EspecialidadMedico;
import com.leonardo.commons.mapper.CommonMapper;
import com.leonardo.medicos.entity.Medico;
import org.springframework.stereotype.Component;

@Component
public class MedicoMapper implements CommonMapper<MedicoRequest, MedicoResponse, Medico> {
    @Override
    public Medico requestAEntidad(MedicoRequest request) {
        return request == null ? null : Medico.crear(
                request.nombre(), request.apellidoPaterno(), request.apellidoMaterno(),
                request.edad(), request.email(), request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));
    }

    @Override
    public MedicoResponse entidadAResponse(Medico entidad) {
        return entidad == null ? null : new MedicoResponse(
                entidad.getIdMedico(), entidad.obtenerNombreCompleto(),
                entidad.getEdad(), entidad.getEmail(), entidad.getTelefono(),
                entidad.getCedulaProfesional(), entidad.getEspecialidad().getDescripcion(),
                entidad.getDisponibilidad().getDescripcion(),
                entidad.getDisponibilidad().getCodigo());
    }
}
