package com.soporte_tecnico;

import java.util.HashMap;
import java.util.Map;

public class TaskFactory {
    
    private static volatile TaskFactory instance;
    
    private int importersCounter;        // Cuenta de hilos importers
    private int loadersCounter;          // Cuenta de hilos loaders
    private int filtersCounter;          // Cuenta de hilos filters
    private int resizersCounter;         // Cuenta de hilos resizers
    private int exportersCounter;        // Cuenta de hilos exporters
    
    private int maxImporters;            // Maxima cantidad de importers
    private int maxLoaders;              // Maxima cantidad de loaders
    private int maxFilters;              // Maxima cantidad de filters
    private int maxResizers;             // Maxima cantidad de resizers
    private int maxExporters;            // Maxima cantidad de exporters

    private Map<Task, Thread> tasks;     // Contenedor de tareas y sus hilos


    /**
     * Constructor. Privado para garantizar singleton.
     * @param maxImporters Maxima cantidad de importers permitidos.
     * @param maxLoaders Maxima cantidad de loaders permitidos.
     * @param maxFilters Maxima cantidad de filters permitidos.
     * @param maxResizers Maxima cantidad de resizers permitidos.
     * @param maxExporters Maxima cantidad de exporters permitidos.
     */
    private TaskFactory(int maxImporters, int maxLoaders, int maxFilters, int maxResizers, int maxExporters) {
        this.maxImporters = maxImporters;
        this.maxLoaders = maxLoaders;
        this.maxFilters = maxFilters;
        this.maxResizers = maxResizers;
        this.maxExporters = maxExporters;

        this.importersCounter = 0;
        this.loadersCounter = 0;
        this.filtersCounter = 0;
        this.resizersCounter = 0;
        this.exportersCounter = 0;

        this.tasks = new HashMap<Task,Thread>();
     
        };


    /**
     * Devuelve una unica instancia de clase TaskFactory. Si no existe instancia, crea una.
     * @param maxImporters Maxima cantidad de importers permitidos.
     * @param maxLoaders Maxima cantidad de loaders permitidos.
     * @param maxFilters Maxima cantidad de filters permitidos.
     * @param maxResizers Maxima cantidad de resizers permitidos.
     * @param maxExporters Maxima cantidad de exporters permitidos.
     * @return puntero a la instancia de TaskFactory.
     */
    public static TaskFactory getInstance(int maxImporters, int maxLoaders, int maxFilters, int maxResizers, int maxExporters) {
        
        TaskFactory result = instance;
        if (result != null) {
            return result;
        }

        synchronized(TaskFactory.class) {
            if (instance == null) {
                instance = new TaskFactory(maxImporters, maxLoaders, maxFilters, maxResizers, maxExporters);
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
        if (taskType.equals("Importer") && this.importersCounter < this.maxImporters) {
            task = new Importer(taskType + " " + ++this.importersCounter, transitions, monitor);
        }        
        else if (taskType.equals("Loader") && this.loadersCounter < this.maxLoaders) {
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
