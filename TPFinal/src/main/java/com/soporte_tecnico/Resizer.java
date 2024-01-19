package com.soporte_tecnico;

import java.util.concurrent.TimeUnit;

public class Resizer extends Task {

    public Resizer(String name, int[] transitions, Monitor monitor) {
        super(name, transitions, monitor);
    }

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
