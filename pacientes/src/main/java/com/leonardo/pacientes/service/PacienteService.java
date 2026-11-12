package com.leonardo.pacientes.service;

import com.leonardo.commons.dto.pacientes.PacienteRequest;
import com.leonardo.commons.dto.pacientes.PacienteResponse;
import com.leonardo.commons.service.CrudServices;


public interface PacienteService extends CrudServices<PacienteRequest, PacienteResponse> {

    PacienteResponse obtenerPacientePorIdSinEstado(Long id);

}
