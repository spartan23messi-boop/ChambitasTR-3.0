package com.leonardo.commons.clients;

import com.leonardo.commons.dto.medicos.MedicoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "medicos")
public interface MedicoClient {

    @GetMapping("/{id}")
    MedicoResponse obtenerMedicoActivoPorId(@PathVariable Long id);

    @GetMapping("/id-medico/{id}")
    MedicoResponse obtenerMedicoSinEstadoPorId(@PathVariable Long id);

    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    void actualizarDisponibilidadMedico(
            @PathVariable Long idMedico,
            @PathVariable Long idDisponibilidad,
            @RequestParam("idCitaExcluida") Long idCitaExcluida
    );
}
