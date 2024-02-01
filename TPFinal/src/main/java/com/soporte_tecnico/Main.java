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
    public static void main(String[] args) {

        boolean stopProgram = false;                          // Flag de finalizacion de programa.
        final int initialImages = 0;                          // Cantidad de imagenes iniciales en p0.
        final Integer nThreads = 8;                           // Cantidad de hilos.
        final Integer maxTinvariants = 200;                   // Invariantes de transicion a cumplir para finalizar el programa.
        final Monitor monitor;                                // Monitor.
        final ArrayList<Pair<Long, Long>> transitionTimes;

        // Factory de hilos/tareas.
        final TaskFactory taskFactory = TaskFactory.getInstance(1,
                                                                2, 
                                                                2, 
                                                                2, 
                                                                1);

        // Lista de transiciones ejecutada por cada hilo.
        final ArrayList<int[]> transitions = new ArrayList<int[]>(Arrays.asList(new int[]{0},
                                                                                new int[]{1, 3}, 
                                                                                new int[]{2, 4},
                                                                                new int[]{5, 7, 9},
                                                                                new int[]{6, 8, 10},
                                                                                new int[]{11, 13},
                                                                                new int[]{12, 14},
                                                                                new int[]{15, 16}));

        // Tipo de tarea ejecutada por cada hilo.
        final ArrayList<String> taskTypes = new ArrayList<String>(Arrays.asList("Importer", "Loader", "Loader", "Filter", "Filter", "Resizer", "Resizer", "Exporter"));
        
        // Tiempos de duracion de tareas.
        final ArrayList<Long> taskTimes = new ArrayList<>(Arrays.asList(1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L));

        // Obtiene los intervalos de tiempo de cada transicion del archivo de configuración y configura la red de petri del monitor.
        monitor = parseConfigFile(args, initialImages);
        // Si llega hasta aqui, se pasaron los argumentos correctos de temporización y si había parametros de prioridad se utilizaron en la configuración de la red.

        // Pide al factory la creación de las tareas.
        for (int i = 0; i < nThreads; i++) {
            Thread task = taskFactory.newTask(taskTypes.get(i), transitions.get(i), taskTimes.get(i), monitor, maxTinvariants);
            task.start();
        }

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
        
        System.out.println("Programa Finalizado");
    }


    /**
     * Parsea los argumentos pasados al programa.
     * @param args String de argumentos.
     * @param initialImages cantidades inicial de imagenes (p0).
     * @return instancia del monitor.
     */
    /*
    private static Monitor parseArgs(String[] args, int initialImages) {
        
        Monitor monitor;
        switch (args.length) {
            case 1:
                // El programa se ejecuta sin argumentos de prioridades y se invoca este constructor de monitor.
                monitor = Monitor.getInstance(initialImages);
                break;
            case 3:
                float setLoad = 0.0f;        // Primer argumento: Porcentaje de carga extra en el hilo con prioridad.
                String segment = args[2];    // Segundo argumento: Hilo a priorizar.
            
                // Si el primer argumento no es un float, termina.
                try {
                    setLoad = Float.parseFloat(args[1]);
                } catch (NumberFormatException e) {
                    usage();
                }

                // Verifica que los argumentos sean correctos.
                if (segment.length() == 1 && segment.charAt(0) >= 'B' && segment.charAt(0) <= 'G' && (setLoad == 0 || (setLoad >= 0.5 && setLoad <= 1))) {
                    monitor = Monitor.getInstance(initialImages, segment, setLoad);
                }
                else {
                    monitor = null;
                    usage();
                }
                break;
            default:
                
        }
        return monitor;
    }
    */

    /**
     * Función que parsea el archivo de configuración con los tiempos de las transiciones temporales.
     * @param args String de argumentos.
     * @param initialImages cantidades inicial de imagenes (p0).
     * @return instancia del monitor.
     */
    private static Monitor parseConfigFile(String[] args, int initialImages) {
        if(args.length!=1){
            System.out.println("ERROR: Cantidad de argumentos incorrecta");
            usage();
        }
        String filePath = args[0];
        Monitor monitor;
        ArrayList<Pair<Long, Long>> transitionTimes = new ArrayList<>(); //Lista con intervalos de tiempo [alfa,beta].
        
        boolean temporized = false,priority = false;
        String segment = "";    // Hilo a priorizar.
        float setLoad = 0.0f;    // Porcentaje de carga extra en el hilo con prioridad.

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) { //Leo todo el archivo
                switch (line) {
                    case "[Transiciones]":
                        temporized = true;
                        for(int i=0;i<=16;i++){     //Leo los valores de alfa y beta para cada transición
                            line = br.readLine();
                            String[] parts = line.split("-");
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
                        priority = true;
                        line = br.readLine();
                        if(line == null){
                            usage();
                        }
                        String[] parts = line.split(" - ");
                        if (parts.length != 2) {
                            System.out.println("Formato invalido: " + line);
                            System.exit(1);
                        }
                        for(String part: parts){
                            System.out.println(part);
                        }
                        
                        segment = parts[0];   
                        try {// Si el segundo argumento no es un float, termina.
                            setLoad = Float.parseFloat(parts[1]);        
                        } catch (NumberFormatException e) {
                            System.out.println("ERROR: Parametros invalidos en el archivo de configuración");
                            usage();
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
        
        if(!priority){ //No había parametros de prioridad en el archivo de configuración
            // El programa se ejecuta sin argumentos de prioridades y se invoca este constructor de monitor.
            monitor = Monitor.getInstance(initialImages);
        } // Chequeo que los parametros de prioridad sean validos
        else if (segment.length() == 1 && segment.charAt(0) >= 'B' && segment.charAt(0) <= 'G' && (setLoad == 0 || (setLoad >= 0.5 && setLoad <= 1))) {
            monitor = Monitor.getInstance(initialImages, segment, setLoad);
        }
        else {    
            monitor = null;
            System.out.printf("'%s' - '%s'",segment,String.valueOf(setLoad));
            System.out.println("ERROR: Parametros invalidos en el archivo de configuración invalidos");
            usage();
        }

        monitor.setTransitionsTime(transitionTimes);
        return monitor;
    }

    
    /**
     * Funcion que imprime como ejecutar el programa con argumentos.
     */
    private static void usage() {
        System.out.println("Uso: java TPFinal.jar configFile.txt");
        System.out.printf("configFile.txt: Archivo de configuracion. ");
        System.out.println("Este archivo debe contener dos headers: [Transiciones] (para indicar los tiempos alfa y beta de la Rdp Temporizada) y [Prioridad] para indicar cuanto priorizar una determinada transición. En caso que este segundo header no se encuentre, se asume RdP no temporizada.");
        System.out.println("Formato a seguir:");
        System.out.println("    [Transición]: (Número de Transición) - (Tiempo Alfa),(Tiempo Beta)");
        System.out.println("    [Prioridad]: (Segmento a priorizar: B al G) - (Relacion de prioridad: 0 (sin prioridad) o un valor mayor o igual que 0.5 y menor o igual que 1.0)");

        System.exit(1);
    }
}
