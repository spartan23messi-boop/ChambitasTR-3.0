package com.leonardo.pacientes.controller;

import com.leonardo.commons.controller.CrudController;
import com.leonardo.commons.docs.ProblemaDoc;
import com.leonardo.commons.dto.pacientes.PacienteRequest;
import com.leonardo.commons.dto.pacientes.PacienteResponse;
import com.leonardo.pacientes.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
@Validated
@Tag(name = "API pacientes", description = "Gestión de pacientes")
public class PacienteController extends CrudController<PacienteRequest, PacienteResponse, PacienteService> {

    public PacienteController(PacienteService service) {
        super(service);
    }

    @GetMapping
    @Operation(summary = "Listar pacientes activos")
    public ResponseEntity<List<PacienteResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/id-paciente/{id}")
    @Operation(summary = "Consultar paciente por ID sin filtrar estado")
    public ResponseEntity<PacienteResponse> obtenerPorIdSinEstado(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id) {
        return ResponseEntity.ok(service.obtenerPacientePorIdSinEstado(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un paciente activo por ID")
    @ApiResponse(responseCode = "404", description = "Paciente activo no encontrado",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<PacienteResponse> obtenerPorId(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar un paciente")
    @ApiResponse(responseCode = "201", description = "Paciente registrado")
    @ApiResponse(responseCode = "409", description = "Email o teléfono ya registrado")
    public ResponseEntity<PacienteResponse> registrar(@Valid @RequestBody PacienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar los datos de un paciente activo")
    @ApiResponse(responseCode = "409", description = "Email o teléfono ya registrado")
    public ResponseEntity<PacienteResponse> actualizar(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id,
            @Valid @RequestBody PacienteRequest request) {
        return ResponseEntity.ok(service.actualizar(request, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar lógicamente un paciente activo")
    @ApiResponse(responseCode = "204", description = "Paciente eliminado")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
