
package com.leonardo.medicos.entity;

import com.leonardo.commons.enums.DisponibilidadMedico;
import com.leonardo.commons.enums.EspecialidadMedico;
import com.leonardo.commons.enums.EstadoRegistro;
import com.leonardo.commons.exceptions.ConflictoException;
import com.leonardo.commons.exceptions.DatoInvalidoException;
import com.leonardo.commons.utils.StringCustomUtils;
import com.leonardo.commons.utils.ValoresNumericosUtils;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEDICOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICO")
    private Long idMedico;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column(name = "CEDULA_PROFESIONAL", length = 12, nullable = false)
    private String cedulaProfesional;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIALIDAD", nullable = false)
    private EspecialidadMedico especialidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISPONIBILIDAD", nullable = false)
    private DisponibilidadMedico disponibilidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    // Obtener nombre completo
    public String obtenerNombreCompleto() {
        return String.join(" ",
                this.nombre,
                this.apellidoPaterno,
                this.apellidoMaterno);
    }

    // Validar datos
    public static void validarDatos(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            Short edad,
            String email,
            String telefono,
            String cedulaProfesional) {

        StringCustomUtils.validarTamanio(
                nombre, 1, 50,
                "El nombre es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(
                apellidoPaterno, 1, 50,
                "El apellido paterno es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(
                apellidoMaterno, 1, 50,
                "El apellido materno es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(
                email, 1, 100,
                "El email es requerido y debe contener entre 1 y 100 caracteres");

        StringCustomUtils.validarTamanio(
                telefono, 10, 10,
                "El teléfono es requerido y debe contener exactamente 10 dígitos");

        StringCustomUtils.validarTamanio(
                cedulaProfesional, 12, 12,
                "La cédula profesional es requerida y debe contener exactamente 12 caracteres");

        if (!telefono.matches("[0-9]{10}")) {
            throw new DatoInvalidoException("El teléfono debe contener 10 dígitos numéricos");
        }
        if (email.isBlank() || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new DatoInvalidoException("El email debe tener un formato válido");
        }
        ValoresNumericosUtils.validadRangoShort(
                edad, (short)18, (short)100,
                "La edad es requerida y debe estar entre 18 y 100 años");
    }

    // Validar estado
    private void validarNoEliminado() {
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO) {
            throw new ConflictoException("El médico ya está eliminado");
        }
    }

    // Eliminar médico
    public void eliminar() {
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    // Actualizar especialidad
    public void actualizarEspecialidad(EspecialidadMedico especialidad) {
        validarNoEliminado();

        if (especialidad == null) {
            throw new DatoInvalidoException("La especialidad es requerida");
        }

        this.especialidad = especialidad;
    }

    // Actualizar disponibilidad
    public void actualizarDisponibilidad(DisponibilidadMedico disponibilidad) {
        validarNoEliminado();

        if (disponibilidad == null) {
            throw new DatoInvalidoException("La disponibilidad es requerida");
        }

        this.disponibilidad = disponibilidad;
    }

    // Actualizar médico
    public void actualizar(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            Short edad,
            String email,
            String telefono,
            String cedulaProfesional,
            EspecialidadMedico especialidad) {

        validarNoEliminado();

        validarDatos(
                nombre, apellidoPaterno, apellidoMaterno,
                edad, email, telefono, cedulaProfesional);

        actualizarEspecialidad(especialidad);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.email = email.trim().toLowerCase();
        this.telefono = telefono.trim();
        this.cedulaProfesional = cedulaProfesional.trim();
    }

    // Crear médico
    public static Medico crear(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            Short edad,
            String email,
            String telefono,
            String cedulaProfesional,
            EspecialidadMedico especialidad) {

        validarDatos(
                nombre, apellidoPaterno, apellidoMaterno,
                edad, email, telefono, cedulaProfesional);

        if (especialidad == null) {
            throw new DatoInvalidoException("La especialidad es requerida");
        }

        return Medico.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .edad(edad)
                .email(email.trim().toLowerCase())
                .telefono(telefono.trim())
                .cedulaProfesional(cedulaProfesional.trim())
                .especialidad(especialidad)
                .disponibilidad(DisponibilidadMedico.DISPONIBLE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }
}
