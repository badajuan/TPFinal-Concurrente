package com.soporte_tecnico;


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

    
    /**
     * Constructor. Privado para asegurar singleton.
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
    }

    /**
     * Devuelve una unica instancia de clase TaskFactory. Si no existe instancia, crea una.
     * @param maxLoaders Maxima cantidad de loaders permitidos.
     * @param maxFilters Maxima cantidad de filters permitidos.
     * @param maxResizers Maxima cantidad de resizers permitidos.
     * @param maxExporters Maxima cantidad de exporters permitidos.
     * @return
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
     * Crea un hilo que ejecuta una tarea. Verifica que no se puedan crear mas tareas de las especificadas en el constructor.
     * @param taskType String con el tipo de tarea.
     * @return Hilo que ejecuta la tarea solicitada al factory.
     */
    public Thread newTask(String taskType) {

        Thread t;
        if (taskType.equals("Loader") && this.loadersCounter < this.maxLoaders) {
            t = new Thread(new Loader(taskType + " " + ++this.loadersCounter));
        }
        else if (taskType.equals("Filter") && this.filtersCounter < this.maxFilters) {
            t = new Thread(new Filter(taskType + " " + ++this.filtersCounter));
        }
        else if (taskType.equals("Resizer") && this.resizersCounter < this.maxResizers) {
            t = new Thread(new Resizer(taskType + " " + ++this.resizersCounter));
        }
        else if (taskType.equals("Exporter") && this.exportersCounter < this.maxExporters) {
            t = new Thread(new Exporter(taskType + " " + ++this.exportersCounter));
        }
        else {
            t = null;
        }

        return t;
    }
}
