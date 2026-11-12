package com.leonardo.citas.controller;

import com.leonardo.citas.dto.CitaRequest;
import com.leonardo.citas.dto.CitaResponse;
import com.leonardo.citas.service.CitaService;
import com.leonardo.commons.controller.CrudController;
import com.leonardo.commons.docs.ProblemaDoc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "API Citas", description = "Métodos para la gestión de citas")
public class CitaController extends CrudController<CitaRequest, CitaResponse, CitaService> {

    public CitaController(CitaService service) {
        super(service);
    }

    @Operation(
            summary = "Actualizar estado de la cita",
            description = "Actualiza el estado de una cita utilizando " +
                    "el identificador de la cita y el identificador del nuevo estado."
    )
    @ApiResponse(responseCode = "204", description = "Estado de la cita actualizado")
    @ApiResponse(responseCode = "409", description = "El estado de la cita no puede " +
            "actualizarse por regla de negocio",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    @PatchMapping("/{idCita}/estado/{idEstado}")
    public ResponseEntity<Void> actualizarEstadoCita(
            @PathVariable @Positive(message = "El idCita debe ser positivo") Long idCita,
            @PathVariable @Positive(message = "El idEstado debe ser positivo") Long idEstado) {

        service.actualizarEstadoCita(idCita, idEstado);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/id-paciente/{idPaciente}/citas-asignadas")
    @Operation(summary = "Verificar si un paciente tiene citas confirmadas o en curso")
    public ResponseEntity<Void> pacienteTieneCitasAsignadas(
            @PathVariable @Positive Long idPaciente) {
        service.pacienteTieneCitasAsignadas(idPaciente);
        return ResponseEntity.noContent().build();
    }

    @ApiResponse(responseCode = "204",description = "Medico no tiene citas asignadas")
    @ApiResponse(responseCode = "409", description = "Medico tiene citas asignadas y no se puede " +
    "modificar ni eliminar", content = @Content(mediaType = "application/problem+json",
    schema = @Schema(implementation = ProblemaDoc.class)))
    @GetMapping("/id-medico/{idMedico}/citas-asignadas")
    public ResponseEntity<Void> medicoTieneCitasAsignadas(
            @Parameter(description = "Id del medico", example = "1")
            @PathVariable @Positive(message = "El idMedico debe ser positivo") Long idMedico){
        service.medicoTieneCitasAsignadas(idMedico);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/id-medico/{idMedico}/validar-eliminacion")
    @Operation(summary = "Verificar si un médico puede eliminarse lógicamente")
    @ApiResponse(responseCode = "204", description = "El médico no tiene citas que impidan eliminarlo")
    @ApiResponse(responseCode = "409", description = "El médico tiene citas pendientes, confirmadas o en curso")
    public ResponseEntity<Void> validarEliminacionMedico(
            @PathVariable @Positive(message = "El idMedico debe ser positivo") Long idMedico) {
        service.validarEliminacionMedico(idMedico);
        return ResponseEntity.noContent().build();
    }

}
