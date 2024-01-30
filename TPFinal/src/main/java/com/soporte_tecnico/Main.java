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
        final Integer maxTinvatiants = 200;                   // Invariantes de transicion a cumplir para finalizar el programa.
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
        final ArrayList<Long> taskTimes = new ArrayList<Long>(Arrays.asList(new Long(1L), 
                                                                            new Long(1L),
                                                                            new Long(1L),
                                                                            new Long(1L),
                                                                            new Long(1L),
                                                                            new Long(1L),
                                                                            new Long(1L),
                                                                            new Long(1L)));

        monitor = parseArgs(args, initialImages);
        // Si llega hasta aqui, se pasaron los argumentos correctos o no se pasaron argumentos (sin prioridad).

        // Obtiene los intervalos de tiempo de cada transicion del archivo de configuración y configura la red de petri del monitor.
        transitionTimes = parseConfigFile(args[0]);
        monitor.setTransitionsTime(transitionTimes);

        // Pide al factory la creación de las tareas.
        for (int i = 0; i < nThreads; i++) {
            Thread task = taskFactory.newTask(taskTypes.get(i), transitions.get(i), taskTimes.get(i), monitor, maxTinvatiants);
            task.start();
        }

        // Ejecuta el programa hasta que se cumpla la cantidad de invariantes establecida.
        while (!stopProgram) {
            if (monitor.getCounterList().get(16) >= maxTinvatiants) {
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
    private static Monitor parseArgs(String[] args, int initialImages) {
        
        Monitor monitor;
        if (args.length == 1) {
            // El programa se ejecuta sin argumentos de prioridades y se invoca este constructor de monitor.
            monitor = Monitor.getInstance(initialImages);
        }
        else if (args.length == 3) {
            float setLoad = 0.0f;        // Primer argumento: Porcentaje de carga extra en el hilo con prioridad.
            String segment = args[2];    // Seguendo argumento: Hilo a priorizar.
            
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
        }
        else {
            monitor = null;
            usage();
        }

        return monitor;
    }


    /**
     * Función que parsea el archivo de configuración con los tiempos de las transiciones temporales.
     * @param filePath path del archivo de configuración.
     * @return Lista con intervalos de tiempo [alfa,beta].
     */
    private static ArrayList<Pair<Long, Long>> parseConfigFile(String filePath) {
        
        ArrayList<Pair<Long, Long>> transitionTimes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Skip the header line
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("-");
                if (parts.length == 2) {
                    String[] values = parts[1].split(",");
                    if (values.length == 2) {
                        Long key = Long.parseLong(values[0].trim());
                        Long value = Long.parseLong(values[1].trim());
                        transitionTimes.add(new Pair<>(key, value));
                    } else {
                        System.out.println("Formato invalido: " + line);
                        System.exit(1);
                    }
                } else {
                    System.out.println("Formato invalido: " + line);
                    System.exit(1);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return transitionTimes;
    }

    
    /**
     * Funcion que imprime como ejecutar el programa con argumentos.
     */
    private static void usage() {
        System.out.println("Uso: java TPFinal.jar timesConfig.txt [Opcion1 Opcion2]");
        System.out.println("timesConfig.txt: Archivo de configuracion de tiempos de transiciones");
        System.out.println("Opcion1:         Relacion de prioridad: 0 (sin prioridad) o un valor mayor o igual que 0.5 y menor o igual que 1.0");
        System.out.println("Opcion2:         Segmento a priorizar: B al G");

        System.exit(1);
    }
}
