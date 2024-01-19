package com.soporte_tecnico;

import java.util.HashMap;
import java.util.Map;

public class TaskFactory {
    
    // Ver en https://refactoring.guru/design-patterns/singleton/java/example#example-2 porque es volatile
    private static volatile TaskFactory instance;
    
    private int loadersCounter;
    private int filtersCounter;
    private int resizersCounter;
    private int exportersCounter;
    
    private int maxLoaders;
    private int maxFilters;
    private int maxResizers;
    private int maxExporters;

    private Map<Task, Thread> tasks;


    /**
     * Constructor. Privado para garantizar singleton.
     * @param maxLoaders Maxima cantidad de loaders permitidos.
     * @param maxFilters Maxima cantidad de filters permitidos.
     * @param maxResizers Maxima cantidad de resizers permitidos.
     * @param maxExporters Maxima cantidad de exporters permitidos.
     */
    private TaskFactory(int maxLoaders, int maxFilters, int maxResizers, int maxExporters) {
        this.maxLoaders = maxLoaders;
        this.maxFilters = maxFilters;
        this.maxResizers = maxResizers;
        this.maxExporters = maxExporters;

        this.loadersCounter = 0;
        this.filtersCounter = 0;
        this.resizersCounter = 0;
        this.exportersCounter = 0;

        this.tasks = new HashMap<Task,Thread>();
     
        };


    /**
     * Devuelve una unica instancia de clase TaskFactory. Si no existe instancia, crea una.
     * @param maxLoaders Maxima cantidad de loaders permitidos.
     * @param maxFilters Maxima cantidad de filters permitidos.
     * @param maxResizers Maxima cantidad de resizers permitidos.
     * @param maxExporters Maxima cantidad de exporters permitidos.
     * @return puntero a la instancia de TaskFactory.
     */
    public static TaskFactory getInstance(Integer maxLoaders, Integer maxFilters, Integer maxResizers, Integer maxExporters) {
        
        TaskFactory result = instance;
        if (result != null) {
            return result;
        }

        synchronized(TaskFactory.class) {
            if (instance == null) {
                instance = new TaskFactory(maxLoaders, maxFilters, maxResizers, maxExporters);
            }
            return instance;
        }
    }


    /**
     * Devuelve el mapa de tareas con sus respectivos hilos.
     * @return tasks, mapa de <Task, Thread>
     */
    public Map<Task, Thread> getTasks() {
        return this.tasks;
    }


    /**
     * Crea un hilo que ejecuta una tarea. Verifica que no se puedan crear mas tareas de las especificadas en el constructor.
     * @param taskType String con el tipo de tarea.
     * @return Hilo que ejecuta la tarea solicitada al factory.
     */
    public Thread newTask(String taskType, int[] transitions, Monitor monitor) {

        Task task;
        if (taskType.equals("Loader") && this.loadersCounter < this.maxLoaders) {
            task = new Loader(taskType + " " + ++this.loadersCounter, transitions, monitor);
        }
        else if (taskType.equals("Filter") && this.filtersCounter < this.maxFilters) {
            task = new Filter(taskType + " " + ++this.filtersCounter, transitions, monitor);
        }
        else if (taskType.equals("Resizer") && this.resizersCounter < this.maxResizers) {
            task = new Resizer(taskType + " " + ++this.resizersCounter, transitions, monitor);
        }
        else if (taskType.equals("Exporter") && this.exportersCounter < this.maxExporters) {
            task = new Exporter(taskType + " " + ++this.exportersCounter, transitions, monitor);
        }
        else {
            return null;
        }

        Thread t = new Thread(task);
        tasks.put(task, t);
        return t;
    }
}
