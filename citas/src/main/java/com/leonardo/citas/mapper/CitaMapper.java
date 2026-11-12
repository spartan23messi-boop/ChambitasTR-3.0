package com.leonardo.citas.mapper;

import com.leonardo.citas.dto.CitaRequest;
import com.leonardo.citas.dto.CitaResponse;
import com.leonardo.citas.dto.DatosMedico;
import com.leonardo.citas.entity.Cita;
import com.leonardo.commons.dto.medicos.MedicoResponse;
import com.leonardo.commons.dto.pacientes.DatosPaciente;
import com.leonardo.commons.dto.pacientes.PacienteResponse;
import com.leonardo.commons.mapper.CommonMapper;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper implements CommonMapper<CitaRequest, CitaResponse, Cita> {
    @Override
    public Cita requestAEntidad(CitaRequest request) {
        return request == null ? null : Cita.crear(
                request.idPaciente(), request.idMedico(),
                request.fechaCita(), request.sintomas());
    }

    @Override
    public CitaResponse entidadAResponse(Cita entidad) {
        return entidad == null
                ? null
                : new CitaResponse(
                entidad.getId(),
                null,
                null,
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion());
    }
    public CitaResponse entidadAResponse(Cita entidad, PacienteResponse paciente, MedicoResponse medico) {
        return entidad == null
                ? null
                : new CitaResponse(
                entidad.getId(),
                pacienteResponseADatosPaciente(paciente),
                medicoResponseADatosMedico(medico),
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion());
    }

    private DatosPaciente pacienteResponseADatosPaciente(PacienteResponse paciente) {
        return paciente == null ? null : new DatosPaciente(
                paciente.nombre(), paciente.numExpediente(),
                paciente.edad() + " años", paciente.peso() + " kg",
                paciente.estatura() + " m",
                String.format(java.util.Locale.ROOT, "%.2f %s", paciente.imc(), clasificacionIMC(paciente.imc())),
                paciente.telefono());
    }

    private String clasificacionIMC(double imc) {
        if (imc < 18.5) return "Bajo peso";
        if (imc < 25) return "Peso normal";
        if (imc < 30) return "Sobrepeso";
        if (imc < 35) return "Obesidad grado I";
        if (imc < 40) return "Obesidad grado II";
        return "Obesidad grado III";
    }

    private DatosMedico medicoResponseADatosMedico(MedicoResponse medico) {
        return medico == null ? null : new DatosMedico(
                medico.nombre(), medico.cedulaProfesional(), medico.especialidad());
    }
}
