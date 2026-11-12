package com.leonardo.commons.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitaClient {

    @GetMapping("/id-paciente/{idPaciente}/citas-asignadas")
    void pacienteTieneCitasAsignadas(@PathVariable("idPaciente") Long idPaciente);

    @GetMapping("/id-medico/{idMedico}/citas-asignadas")
    void medicoTieneCitasAsignadas(@PathVariable Long idMedico);

    @GetMapping("/id-medico/{idMedico}/validar-eliminacion")
    void validarEliminacionMedico(@PathVariable("idMedico") Long idMedico);

}
