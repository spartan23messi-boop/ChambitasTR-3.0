package com.leonardo.medicos.service;

import com.leonardo.commons.dto.medicos.MedicoRequest;
import com.leonardo.commons.dto.medicos.MedicoResponse;
import com.leonardo.commons.service.CrudServices;

public interface MedicoServices extends CrudServices <MedicoRequest, MedicoResponse> {

    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad, Long idCitaExcluida);


}
