package com.soporte_tecnico;

public abstract class Task implements Runnable {
    
    private String name;
    protected boolean stop;

    public Task(String name) {
        this.name = name;
        this.stop = false;
    }

    protected abstract void doTask();

    protected void setStop(boolean s) {
        this.stop = s;
    }

    public String getName() {
        return this.name;
    }

}