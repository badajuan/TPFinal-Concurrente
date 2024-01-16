package com.soporte_tecnico;

public abstract class Task implements Runnable {
    
    private String name;
    protected boolean stop;

    
    /**
     * Constructor.
     * @param name Tipo de tarea a crear.
     */
    public Task(String name) {
        this.name = name;
        this.stop = false;
    }

    
    /**
     * Tarea realizada.
     */
    protected abstract void doTask();


    /**
     * 
     * @param s
     */
    protected void setStop(boolean s) {
        this.stop = s;
    }


    /**
     * Devuelve el tipo de tarea.
     * @return nombre/tipo de la tarea.
     */
    public String getName() {
        return this.name;
    }


    @Override
    public void run() {
        while (!this.stop) {
            try {
                doTask();
            } catch (RuntimeException e) {
                this.setStop(true);
            }
        }
    }
}