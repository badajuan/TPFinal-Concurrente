package com.soporte_tecnico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        boolean stopProgram = false;
        final int initialImages = 6;
        final Integer nThreads = 8;
        final Integer maxTinvatiants = 200;
        //final Monitor monitor = Monitor.getInstance(initialImages);
        final Monitor monitor = Monitor.getInstance(initialImages, "Priority", "E", 0.8);
        final TaskFactory taskFactory = TaskFactory.getInstance(1,
                                                                2, 
                                                                2, 
                                                                2, 
                                                                1);

        final ArrayList<int[]> transitions = new ArrayList<int[]>(Arrays.asList(new int[]{0},
                                                                          new int[]{1, 3}, 
                                                                          new int[]{2, 4},
                                                                          new int[]{5, 7, 9},
                                                                          new int[]{6, 8, 10},
                                                                          new int[]{11, 13},
                                                                          new int[]{12, 14},
                                                                          new int[]{15, 16}));

        final ArrayList<String> taskTypes = new ArrayList<String>(Arrays.asList("Importer", "Loader", "Loader", "Filter", "Filter", "Resizer", "Resizer", "Exporter"));
        
        for (int i = 0; i < nThreads; i++) {
            Thread task = taskFactory.newTask(taskTypes.get(i), transitions.get(i), monitor);
            task.start();
        }

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
}
