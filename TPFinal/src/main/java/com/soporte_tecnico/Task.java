package com.soporte_tecnico;

public abstract class Task implements Runnable {
    
    private String name;
    protected boolean stop;
    protected final int[] transitions;
    protected final Monitor monitor;
    
    /**
     * Constructor.
     * @param name Tipo de tarea a crear.
     */
    public Task(String name, int[] transitions, Monitor monitor) {
        this.name = name;
        this.stop = false;
        this.transitions = transitions;
        this.monitor = monitor;
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
        doTask();
    }
}