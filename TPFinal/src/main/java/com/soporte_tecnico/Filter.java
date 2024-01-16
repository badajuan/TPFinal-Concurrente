package com.soporte_tecnico;

import java.util.concurrent.TimeUnit;

public class Filter extends Task {
    
    public Filter(String name) {
        super(name);
    }

    protected void doTask() {
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            this.setStop(true);
        }   
    }
}
