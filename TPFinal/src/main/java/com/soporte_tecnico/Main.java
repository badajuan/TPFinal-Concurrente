package com.soporte_tecnico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.util.Pair;

public class Main {

    static int initialImages = 0;                                             // Cantidad de imagenes iniciales en p0.
    static Integer maxTinvariants = 200;                                      // Invariantes de transicion a cumplir para finalizar el programa.
    static boolean priority = false;                                          // Flag de prioridad de segmento.
    static String segment = "";                                               // Hilo a priorizar.
    static float setLoad = 0.0f;                                              // Porcentaje de carga extra en el hilo con prioridad.
    static ArrayList<Pair<Long, Long>> transitionTimes = new ArrayList<>();   //Lista con intervalos de tiempo [alfa,beta].
    static ArrayList<Long> taskTimes = new ArrayList<>();                     // Tiempos de duracion de tareas.

    public static void main(String[] args) {
             
        final Integer nThreads = 8;                                           // Cantidad de hilos.                  
        final Monitor monitor;                                                // Monitor.
        boolean stopProgram = false;                                          // Flag de finalizacion de programa. 
        
        // Factory de hilos/tareas.
        final TaskFactory taskFactory = TaskFactory.getInstance(1, 2,  2, 2, 1);

        // Lista de transiciones ejecutada por cada hilo.
        final ArrayList<int[]> transitions = new ArrayList<>(Arrays.asList(new int[]{0},
                                                                                new int[]{1, 3}, 
                                                                                new int[]{2, 4},
                                                                                new int[]{5, 7, 9},
                                                                                new int[]{6, 8, 10},
                                                                                new int[]{11, 13},
                                                                                new int[]{12, 14},
                                                                                new int[]{15, 16}));

        // Tipo de tarea ejecutada por cada hilo.
        final ArrayList<String> taskTypes = new ArrayList<>(Arrays.asList("Importer", "Loader", "Loader", "Filter", "Filter", "Resizer", "Resizer", "Exporter"));
        
        // Obtiene los intervalos de tiempo de cada transicion del archivo de configuración y configura la red de petri del monitor.
        if(args.length!=1){
            System.out.println("ERROR: Cantidad de argumentos incorrecta");
            usage();
        }
        
        parseConfigFile(args);
        
        if(!priority){ //No había parametros de prioridad en el archivo de configuración
            // El programa se ejecuta sin argumentos de prioridades y se invoca este constructor de monitor.
            monitor = Monitor.getInstance(initialImages);
        } // Chequeo que los parametros de prioridad sean validos
        else if (segment.length() == 1 && segment.charAt(0) >= 'B' && segment.charAt(0) <= 'G' && (setLoad == 0 || (setLoad >= 0.5 && setLoad <= 1))) {
            System.out.printf("	- Archivo de configuración incluye política de procesamiento prioritario para el segmento %s con una carga del %.0f%% -\n",segment,setLoad*100);
            monitor = Monitor.getInstance(initialImages, segment, setLoad);
        }
        else {    
            monitor = null;
            System.out.println("ERROR: Parametros invalidos en el archivo de configuración invalidos");
            usage();
        }

        monitor.setTransitionsTime(transitionTimes);


        // Pide al factory la creación de las tareas.
        for (int i = 0; i < nThreads; i++) {
            Thread task = taskFactory.newTask(taskTypes.get(i), transitions.get(i), taskTimes.get(i), monitor, maxTinvariants);
            task.start();
        }
        
        System.out.printf("\nRdP cargada con exito. La ejecución de la red intentará alcanzar %d invariantes de transición con %d Threads.\n",maxTinvariants,nThreads);

        long startTime = System.nanoTime();
        // Ejecuta el programa hasta que se cumpla la cantidad de invariantes establecida.
        while (!stopProgram) {
            if (monitor.getCounterList().get(16) >= maxTinvariants) {
                stopProgram = true;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.nanoTime();
        Map<Task, Thread> tasks = taskFactory.getTasks();

        // Detiene todos los hilos.
        tasks.forEach((task, thread) -> {
           thread.interrupt(); 
           task.setStop(true);
        });

        tasks.forEach((task, thread) -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        monitor.getLog().writeLog();
        
        System.out.printf("\nRdP ejecutada con éxito. Duración de la ejecución de la red: %dms.\n\nCerrando programa...",TimeUnit.NANOSECONDS.toMillis(endTime-startTime));
    }


    /**
     * Función que parsea el archivo de configuración con los tiempos de las transiciones temporales.
     * @param args String de argumentos.
     */
    private static void parseConfigFile(String[] args) {
        String filePath = args[0]; 
        boolean temporized = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) { //Leo todo el archivo
                switch (line) {
                    case "[Parametros]":        //Sobreescribe los parametros iniciales
                        for (int i = 0; i < 2; i++) {
                            line = br.readLine();
                            String[] keyValue = line.split("=");
                            if (keyValue.length != 2) {
                                System.out.println("Formato invalido: " + line);
                                System.exit(1);
                            }
                            if ("tokens".equals(keyValue[0])) {
                                try {
                                    initialImages = Integer.parseInt(keyValue[1]);
                                } catch (NumberFormatException e) {
                                    System.out.println("Formato invalido: " + line);
                                    System.exit(1);
                                }
                            }
                            if ("maxTinvariantes".equals(keyValue[0])) {
                                try {
                                    maxTinvariants = Integer.parseInt(keyValue[1]);
                                } catch (NumberFormatException e) {
                                    System.out.println("Formato invalido: " + line);
                                    System.exit(1);
                                }
                            }                            
                        }
                        break;
                    case "[TiempoTareas]":
                    for (int i = 0; i < 8; i++) {
                        Long sleepTime = 1L;
                        line = br.readLine();
                        String[] keyValue = line.split("=");
                        if (keyValue.length != 2) {
                            System.out.println("Formato invalido: " + line);
                            System.exit(1);
                        }
                        if (!("A".equals(keyValue[0]) || "B".equals(keyValue[0]) || "C".equals(keyValue[0]) || "D".equals(keyValue[0]) || 
                        "E".equals(keyValue[0]) || "F".equals(keyValue[0]) || "G".equals(keyValue[0]) || "H".equals(keyValue[0]))) {
                            System.out.println("Formato invalido: " + line);
                            System.exit(1);
                        }
                        try {
                            sleepTime = Long.parseLong(keyValue[1]);
                        } catch (NumberFormatException e) {
                            System.out.println("Formato invalido: " + line);
                            System.exit(1);
                        }
                        taskTimes.add(sleepTime);
                    }
                    break;
                    case "[Transiciones]":
                        temporized = true;
                        for(int i=0;i<=16;i++){     //Leo los valores de alfa y beta para cada transición
                            line = br.readLine();
                            String[] parts = line.split("=");
                            if (parts.length != 2) {
                                System.out.println("Formato invalido: " + line);
                                System.exit(1);
                            }
                            String[] values = parts[1].split(",");
                            if (values.length != 2) {
                                System.out.println("Formato invalido: " + line);
                                System.exit(1);
                            }
                            long key = Long.parseLong(values[0].trim());
                            long value = Long.parseLong(values[1].trim());
                            transitionTimes.add(new Pair<>(key, value));
                        }
                        break;
                    case "[Prioridad]":
                        for (int i = 0; i < 2; i++) {
                            line = br.readLine();
                            String[] keyValue = line.split("=");
                            if (keyValue.length != 2) {
                                System.out.println("Formato invalido: " + line);
                                System.exit(1);
                            }
                            if ("segmento".equals(keyValue[0])) {
                                if ("none".equals(keyValue[1])) {
                                    priority = false;
                                }
                                else if ("B".equals(keyValue[1]) || "C".equals(keyValue[1]) || "D".equals(keyValue[1]) || 
                                "E".equals(keyValue[1]) || "F".equals(keyValue[1]) || "G".equals(keyValue[1])) {
                                    priority = true;
                                    segment = keyValue[1];   
                                }
                                else {
                                    System.out.println("Formato invalido: " + line);
                                    System.exit(1);
                                }
                            }
                            if ("carga".equals(keyValue[0])) {
                                try {// Si el segundo argumento no es un float, termina.
                                    setLoad = Float.parseFloat(keyValue[1]);        
                                } catch (NumberFormatException e) {
                                    System.out.println("ERROR: Parametros invalidos en el archivo de configuración");
                                    usage();
                                }
                            }
                        }
                        break;
                    default:
                        System.out.println("Formato invalido: " + line);
                        System.exit(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!temporized){ //No se encontraron los parametros de temporización de la red
            System.out.println("ERROR: Parametros en el archivo de configuración insuficientes");
            usage();
        }
    }

    
    /**
     * Funcion que imprime como ejecutar el programa con argumentos.
     */
    private static void usage() {
        System.out.println("Uso: java TPFinal.jar configFile.txt");
        System.out.printf("configFile.txt: Archivo de configuracion.");
        System.out.println("Este archivo debe contener las secciones:");
        System.out.println("    [Parametros]    para indicar marcado inicial y cantidad máxima de invariantes de transición a ejecutar.");
        System.out.println("    [TiempoTareas]  para indicar el tiempo de sleep de cada hilo.");
        System.out.println("    [Transiciones]  para indicar los tiempos alfa y beta de la Rdp Temporizada.");
        System.out.println("    [Prioridad]     para indicar cuanto priorizar un determinado segmento de la red.");
        System.out.println("");
        System.out.println("Formato a seguir:");
        System.out.println("    [Parametros]  : (tokens)=(marcado inicial en p0) y (maxTinvariantes)=(Máxima cantidad de invariantes)");
        System.out.println("    [Transición]  : (Número de Transición)=(Tiempo Alfa),(Tiempo Beta)");
        System.out.println("    [TiempoTareas]: (Segmento)=(Tiempo de sleep)");
        System.out.println("    [Prioridad]   : (Segmento)=(segmento a priorizar: B al G) y (carga)=(valor: 0 (sin prioridad) o un valor mayor o igual que 0.5 y menor o igual que 1.0)");
        
        System.exit(1);
    }
}
