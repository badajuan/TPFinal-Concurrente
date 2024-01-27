package com.soporte_tecnico;

import java.util.concurrent.TimeUnit;

import com.soporte_tecnico.exceptions.TaskInterruptedException;

public class Importer extends Task{

    private int maxImages;

    /**
     * Constructor.
     * @param name tarea a realizar.
     * @param transitions transiciones disparadas por la tarea.
     * @param monitor monitor de la ejecucion.
     */
    public Importer(String name, int[] transitions, Monitor monitor, int maxImages) {
        super(name, transitions, monitor);
        this.maxImages = maxImages;
    }


    /**
     * Tarea realizada.
     */
    protected void doTask() {
        int i = 0;
        int index = 0;
        while (!stop && i<maxImages) {
            try {
                this.monitor.fireTransition(transitions[index]);
                index = (index + 1) % transitions.length;
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException | TaskInterruptedException e) {
                this.setStop(true);
            }
            i++;
        }
    }
}
