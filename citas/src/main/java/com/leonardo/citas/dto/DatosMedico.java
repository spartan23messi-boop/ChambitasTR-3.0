package com.leonardo.citas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un medico asociado a una cita")
public record DatosMedico(

        @Schema(description = "Nombre completo del medico", example = "Leonardo Hernandez Serrano")
        String nombre,

        @Schema(description = "Cedula profesional del medico", example = "123456789012")
        String cedulaProfesional,

        @Schema(description = "Nombre de la especialidad medica del medico",example = "Cardiologia")
        String especialidad
) {}
