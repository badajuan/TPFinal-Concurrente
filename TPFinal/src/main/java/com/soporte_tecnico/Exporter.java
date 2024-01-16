package com.soporte_tecnico;

import java.util.concurrent.TimeUnit;

public class Exporter extends Task {

    public Exporter(String name) {
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
