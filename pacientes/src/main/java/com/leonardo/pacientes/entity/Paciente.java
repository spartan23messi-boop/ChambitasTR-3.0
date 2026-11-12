package com.leonardo.pacientes.entity;

import com.leonardo.commons.enums.EstadoRegistro;
import com.leonardo.commons.exceptions.ConflictoException;
import com.leonardo.commons.utils.StringCustomUtils;
import com.leonardo.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.stream.Collectors;

@Entity
@Table(name = "PACIENTES")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType. IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column (name = "NOMBRE", length = 50, nullable = false)
    private String nombre;

    @Column (name = "APELLIDO_PATERNO", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column (name = "APELLIDO_MATERNO", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "PESO", nullable = false)
    private Double peso;

    @Column(name = "ESTATURA", nullable = false)
    private Double estatura;

    @Column(name = "IMC", nullable = false)
    private Double imc;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private  String email;

    @Column(name = "NUM_EXPEDIENTE", length = 20, nullable = false)
    private  String numExpediente;

    @Column (name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column (name = "DIRECCION", length = 150, nullable = false)
    private String direccion;

    /*@Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO",nullable = false)*/
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ESTADO_REGISTRO", nullable = false,
            columnDefinition = "estadoregistro")
    private EstadoRegistro estadoRegistro;


    public static void validarDatos(String nombre,String apelldioPaterno, String apellidoMaterno,Short edad,
                                    Double peso, Double estatura, String email,
                                    String telefono, String direccion){

        StringCustomUtils.validarTamanio(nombre,1,50,
                "El nombre es requerido y debe contener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apelldioPaterno,1,50,
                "El apellido paterno es requerido y debe contener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoMaterno,1,50,
                "El apellido materno es requerido y debe contener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(email,1,100,
                "El email es requerido y debe contener entre 1 y 100 caracteres");
        StringCustomUtils.validarTamanio(telefono,10,10,
                "El teléfono es requerido y debe contener exactamente 10 dígitos");
        StringCustomUtils.validarTamanio(direccion,1,150,
                "La dirección es requerida y debe contener entre 10 y 150 carácteres");



        if (!telefono.matches("[0-9]{10}")) {
            throw new com.leonardo.commons.exceptions.DatoInvalidoException("El teléfono debe tener 10 dígitos numéricos");
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new com.leonardo.commons.exceptions.DatoInvalidoException("El email no es válido");
        }
        ValoresNumericosUtils.validadRangoShort(edad,(short)1,(short)100,
                "La edad es requerida y debe tener entre 1 y 100 años");
        if (peso == null || !Double.isFinite(peso) || peso < 0.1 || peso > 200.0) {
            throw new com.leonardo.commons.exceptions.DatoInvalidoException(
                    "El peso debe estar entre 0.1 y 200 kg");
        }
        if (estatura == null || !Double.isFinite(estatura) || estatura < 1.0 || estatura > 2.0) {
            throw new com.leonardo.commons.exceptions.DatoInvalidoException(
                    "La estatura es requerida y debe estar entre 1.0 y 2.0 metros");
        }

    }

    private void validarNoEliminado(){
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictoException("El paciente ya está eliminado");
    }

    public void eliminar(){
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public String obtenerNombreCompleto(){
        return String.join(" ",
                this.getNombre(),
                this.getApellidoPaterno(),
                this.getApellidoMaterno());
    }

    private static Double calcularImc(Double peso, Double estatura) {
        double resultado = peso / (estatura * estatura);
        return Math.round(resultado * 100.0) / 100.0;
    }

    private static String calcularNumeroExpediente(String telefono){
        return telefono.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining("X", "", "X"));
    }

    public void actualizar(String nombre,String apellidoPaterno, String apellidoMaterno,Short edad,
                           Double peso, Double estatura, String email,
                           String telefono, String direccion){
        validarNoEliminado();

        validarDatos(nombre,apellidoPaterno,apellidoMaterno,edad,peso,estatura,email,telefono,direccion);

        this.nombre = nombre.trim();
        this.apellidoPaterno=apellidoPaterno.trim();
        this.apellidoMaterno=apellidoMaterno.trim();
        this.edad=edad;
        this.peso= peso;
        this.estatura=estatura;
        this.imc= calcularImc(peso,estatura);
        this.email= email.trim().toLowerCase();
        this.numExpediente=calcularNumeroExpediente(telefono);
        this.telefono= telefono.trim();
        this.direccion=direccion.trim();
    }

    public static Paciente crear(String nombre,String apellidoPaterno, String apellidoMaterno,Short edad,
                           Double peso, Double estatura, String email,
                           String telefono, String direccion){

        validarDatos(nombre,apellidoPaterno,apellidoMaterno,edad,peso,estatura,email,telefono,direccion);


        return Paciente.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .edad(edad)
                .peso(peso)
                .estatura(estatura)
                .telefono(telefono.trim())
                .direccion(direccion.trim())
                .numExpediente(calcularNumeroExpediente(telefono))
                .email(email.trim().toLowerCase())
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .imc(calcularImc(peso,estatura))
                .build();
    }
}

