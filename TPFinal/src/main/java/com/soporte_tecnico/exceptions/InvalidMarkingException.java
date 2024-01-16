package com.soporte_tecnico.exceptions;

/**
 * Excepcion utilizada cuando no se cumplen los invariantes de plaza.
 */
public class InvalidMarkingException extends RuntimeException {
    public InvalidMarkingException(String str) {
        super(str);
    }
}
