package com.soporte_tecnico.exceptions;

/**
 * Interrupción para manejar la interrupción de los hilos al finalizar el programa.
 */
public class TaskInterruptedException  extends RuntimeException {
    public TaskInterruptedException (String str) {
        super(str);
    }      
}
