package com.soporte_tecnico.exceptions;

/**
 * Excepcion utilizada cuando no coincide el numero de transiciones entre vectores que contienen
 * el listado de transiciones.
 */
public class TransitionsMismatchException extends RuntimeException{
    public TransitionsMismatchException(String str) {
        super(str);
    }    
}
