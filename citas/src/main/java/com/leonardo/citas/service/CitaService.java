package com.leonardo.citas.service;

import com.leonardo.citas.dto.CitaRequest;
import com.leonardo.citas.dto.CitaResponse;
import com.leonardo.commons.service.CrudServices;

public interface CitaService extends CrudServices<CitaRequest, CitaResponse> {
    void pacienteTieneCitasAsignadas(Long idPaciente);
    void medicoTieneCitasAsignadas(Long idMedico);
    void validarEliminacionMedico(Long idMedico);
    void actualizarEstadoCita(Long idCita, Long idEstadoCita);
}
