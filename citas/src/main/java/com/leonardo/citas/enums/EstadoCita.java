package com.leonardo.citas.enums;

import com.leonardo.commons.enums.EstadoRegistro;
import com.leonardo.commons.exceptions.DatoInvalidoException;
import com.leonardo.commons.utils.ValoresNumericosUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum EstadoCita {

    PENDIENTE(1L, "Pendiente de confirmar", true, true) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of(CONFIRMADA, CANCELADA);
        }
    },

    CONFIRMADA(2L, "Confirmada por el paciente", true, false) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of(EN_CURSO, CANCELADA);
        }
    },

    EN_CURSO(3L, "Paciente llego a su cita", true, false) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of(FINALIZADA);
        }
    },

    FINALIZADA(4L, "Cita finalizada", false, true) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of();
        }
    },

    CANCELADA(5L, "Cita cancelada", false, true){

    @Override
    public Set<EstadoCita> puedeCambiar() {
        return Set.of();
    }
    };

    private final Long codigo;

    private final String descripcion;

    private final boolean actualizable;

    private final boolean eliminable;

    public  abstract Set<EstadoCita>puedeCambiar();

    public static EstadoCita obtenerEstadoCitaPorCodigo(Long codigo) {


        ValoresNumericosUtils.validadLongPositivo(codigo, "El codigo es requerido y debe ser positivo");

        for (EstadoCita e : values()) {
            if (Objects.equals(e.codigo, codigo)) {
                return e;
            }
        }
        throw new DatoInvalidoException("Código de estado de cita no válido: " + codigo);

    }
}
