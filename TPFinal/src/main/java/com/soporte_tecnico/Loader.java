package com.soporte_tecnico;

import java.util.concurrent.TimeUnit;

import com.soporte_tecnico.exceptions.TaskInterruptedException;

public class Loader extends Task {

    /**
     * Constructor.
     * @param name tarea a realizar.
     * @param transitions transiciones disparadas por la tarea.
     * @param monitor monitor de la ejecucion.
     */
    public Loader(String name, int[] transitions, Monitor monitor) {
        super(name, transitions, monitor);
    }


    /**
     * Tarea realizada.
     */
    protected void doTask() {
        int index = 0;
        while (!stop) {
            try {
                this.monitor.fireTransition(transitions[index]);
                index = (index + 1) % transitions.length;
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException | TaskInterruptedException e) {
                this.setStop(true);
            }   
        }
    }
}
