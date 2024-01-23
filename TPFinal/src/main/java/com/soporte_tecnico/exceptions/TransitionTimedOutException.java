package com.soporte_tecnico.exceptions;

/**
 * Excepcion utilizada cuando se intenta disparar una transicion cuyo intervalo de tiempo pasó.
 */
public class TransitionTimedOutException extends RuntimeException {
    public TransitionTimedOutException(String str) {
        super(str);
    }    
}
