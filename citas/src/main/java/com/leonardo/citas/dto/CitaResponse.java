package com.leonardo.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leonardo.commons.dto.pacientes.DatosPaciente;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Informacion de una cita medica registrada")
public record CitaResponse(

        @Schema(description = "Identificador de la cita")
        Long id,

        @Schema(description = "Informacion del paciente asociado a la cita")
        DatosPaciente paciente,

        @Schema(description = "Informacion del medico asociado a la cita")
        DatosMedico medico,

        @Schema(description = "Fecha y hora programada para la cita", example = "25/09/2027 10:30",
        type = "string",
        format = "date-time"

        )
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,

        @Schema(description = "Descripcion de los sintomas indicados por el paciente",
                example = "El paciente presenta dolor de cabeza intenso y fiebre desde hace dos dias"

        )
        String sintomas,
        @Schema(description = "Estado actual de la cita", example = "Confirmada por el paciente")
        String estadoCita

){}
