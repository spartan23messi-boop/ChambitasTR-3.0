package com.leonardo.commons.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "citas")
public interface CitaClient {

    @GetMapping("/id-paciente/{idPaciente}/citas-asignadas")
    void pacienteTieneCitasAsignadas(@PathVariable("idPaciente") Long idPaciente);

    @GetMapping("/id-medico/{idMedico}/citas-asignadas")
    void medicoTieneCitasAsignadas(@PathVariable Long idMedico);

    // Exclusión usada por el flujo interno de transición de una cita.
    @GetMapping("/id-medico/{idMedico}/citas-asignadas")
    void medicoTieneCitasAsignadasExcluyendo(
            @PathVariable Long idMedico,
            @RequestParam("idCitaExcluida") Long idCitaExcluida);
}
