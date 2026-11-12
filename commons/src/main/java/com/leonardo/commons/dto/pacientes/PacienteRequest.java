package com.leonardo.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para registrar o actualizar un paciente")
public record PacienteRequest(
        @NotBlank @Size(max = 50) @Schema(example = "Juan") String nombre,
        @NotBlank @Size(max = 50) @Schema(example = "Pérez") String apellidoPaterno,
        @NotBlank @Size(max = 50) @Schema(example = "Gómez") String apellidoMaterno,
        @NotNull @Min(1) @Max(100) @Schema(example = "30") Short edad,
        @NotNull @DecimalMin("0.1") @DecimalMax("200.0") @Schema(example = "75.5") Double peso,
        @NotNull @DecimalMin("1.0") @DecimalMax("2.0") @Schema(example = "1.75") Double estatura,
        @NotBlank @Email @Size(max = 100) @Schema(example = "juan.perez@gmail.com") String email,
        @NotBlank @Pattern(regexp = "[0-9]{10}") @Schema(example = "5512345678") String telefono,
        @NotBlank @Size(max = 150) @Schema(example = "Av. Reforma 123") String direccion
) {}
