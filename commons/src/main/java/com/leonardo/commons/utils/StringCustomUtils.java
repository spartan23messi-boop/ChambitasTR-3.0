package com.leonardo.commons.utils;

import com.leonardo.commons.exceptions.DatoInvalidoException;

public class StringCustomUtils {
    public static void validarNoVacio(String texto, String mensaje){
        if(texto==null || texto.isBlank())
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarTamanio(String texto,Integer min, Integer max, String mensaje){
        validarNoVacio(texto,mensaje);

        if(texto.length()<min || texto.length()>max)
            throw new DatoInvalidoException(mensaje);
    }

    public static String normalizarTexto(String texto){
        return texto.toLowerCase()
                .replace("á","a")
                .replace("é","e")
                .replace("í","i")
                .replace("ó","o")
                .replace("ú","u")
                .replace("ñ","n")
                .replace("ü","u");
    }


}
