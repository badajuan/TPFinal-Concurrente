package com.soporte_tecnico;

public abstract class Task implements Runnable {
    
    private String name;
    protected boolean stop;
    protected final long taskTime;
    protected final int[] transitions;
    protected final Monitor monitor;
    

    /**
     * Constructor.
     * @param name tarea a realizar.
     * @param transitions transiciones disparadas por la tarea.
     * @param monitor monitor de la ejecución.
     */
    public Task(String name, int[] transitions, long taskTime, Monitor monitor) {
        this.name = name;
        this.stop = false;
        this.transitions = transitions;
        this.taskTime = taskTime;
        this.monitor = monitor;
    }

    
    /**
     * Tarea realizada.
     */
    protected abstract void doTask();


    /**
     * Detiene la ejecucion de la tarea.
     * @param s true si se quiere detener la ejecución.
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