package com.leonardo.medicos.client;

import com.leonardo.commons.dto.medicos.MedicoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "medico")
public interface MedicoClient {

    @GetMapping("/{id}")
    MedicoResponse obtenerMedicoActivoPorId(@PathVariable Long id);

    @GetMapping("/id-medico/{id}")
    MedicoResponse obtenerMedicoSinEstadoPorId(@PathVariable Long id);

    @PutMapping("/{idMedico}/disponibilidad/{Disponibilidad}")
    void actualizarDisponibilidadMedico(
            @PathVariable Long idMedico,
            @PathVariable Long idDisponibilidad
    );
}
