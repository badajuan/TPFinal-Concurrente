package com.soporte_tecnico;

import java.util.concurrent.TimeUnit;

public class Resizer extends Task {

    /**
     * Constructor.
     * @param name tarea a realizar.
     * @param transitions transiciones disparadas por la tarea.
     * @param monitor monitor de la ejecución.
     */
    public Resizer(String name, int[] transitions, Monitor monitor) {
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
                TimeUnit.MILLISECONDS.sleep(2);
            } catch (InterruptedException e) {
                this.setStop(true);
            }   
        } 
    }

}
