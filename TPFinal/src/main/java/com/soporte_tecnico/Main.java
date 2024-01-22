package com.soporte_tecnico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        boolean stopProgram = false;             // Flag de finalizacion de programa
        final int initialImages = 6;             // Cantidad de imagenes iniciales en p0
        final Integer nThreads = 8;              // Cantidad de hilos
        final Integer maxTinvatiants = 200;      // Invariantes de transicion a cumplir para finalizar el programa
        final Monitor monitor;                   // Monitor

        // Factory de hilos/tareas
        final TaskFactory taskFactory = TaskFactory.getInstance(1,
                                                                2, 
                                                                2, 
                                                                2, 
                                                                1);

        // Lista de transiciones ejecutada por cada hilo
        final ArrayList<int[]> transitions = new ArrayList<int[]>(Arrays.asList(new int[]{0},
                                                                                new int[]{1, 3}, 
                                                                                new int[]{2, 4},
                                                                                new int[]{5, 7, 9},
                                                                                new int[]{6, 8, 10},
                                                                                new int[]{11, 13},
                                                                                new int[]{12, 14},
                                                                                new int[]{15, 16}));

        // Tipo de tarea ejecutada por cada hilo
        final ArrayList<String> taskTypes = new ArrayList<String>(Arrays.asList("Importer", "Loader", "Loader", "Filter", "Filter", "Resizer", "Resizer", "Exporter"));
        

        if (args.length == 0) {
            // Si el programa se ejecuta sin argumentos, no hay prioridades y se invoca este constructor de monitor
            monitor = Monitor.getInstance(initialImages);
        }
        else if (args.length == 3) {
            String mode = args[0];       // Primer argumento: modo Balance o Priority
            String segment = args[1];    // Seguendo argumento: Hilo a priorizar
            float setLoad = 0.0f;        // Tercedr argumento: Porcentaje de carga extra en el hilo con prioridad

            // Si el tercer argumento no es un float, termina
            try {
                setLoad = Float.parseFloat(args[2]);
            } catch (NumberFormatException e) {
                usage();
            }

            // Verifica que los argumentos sean correctos
            if (("Balance".equals(mode) || "Priority".equals(mode)) && segment.length() == 1 && segment.charAt(0) >= 'A' && segment.charAt(0) <= 'F' && setLoad > 0 && setLoad <= 1) {
                monitor = Monitor.getInstance(initialImages, mode, segment, setLoad);
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
        // Si llega hasta aqui, se pasaron los argumentos correctos o no se pasaron argumentos (sin prioridad)

        // Pide al factory la creacion de las tareas
        for (int i = 0; i < nThreads; i++) {
            Thread task = taskFactory.newTask(taskTypes.get(i), transitions.get(i), monitor);
            task.start();
        }

        // Ejecuta el programa hasta que se cumpla la cantidad de invariantes establecida
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

        // Detiene todos los hilos
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
        
        System.out.println("Programa Finalizado");
    }

    /**
     * Funcion que imprime como ejecutar el programa con argumentos.
     */
    private static void usage() {
        System.out.println("Usage: java TPFinal [Argument1 Argument2 Argument3]");
        System.out.println("Argument1: Balance or Priority");
        System.out.println("Argument2: A to F");
        System.out.println("Argument3: Float number (greater than 0 and less or equal than 1)");

        System.exit(1);
    }
}
