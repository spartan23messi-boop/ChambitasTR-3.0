package com.leonardo.commons.clients;

import com.leonardo.commons.dto.pacientes.PacienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pacientes")
public interface PacienteClient {

    @GetMapping("/pacientes/{id}")
    PacienteResponse obtenerPacienteActivoPorId(@PathVariable("id") Long id);

    @GetMapping("/pacientes/id-paciente/{id}")
    PacienteResponse obtenerPacienteSinEstadoPorId(@PathVariable("id") Long id);
}
