package com.soporte_tecnico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        PetriNet petriNet = PetriNet.getInstance(200);
        Monitor monitor = Monitor.getInstance(petriNet);
        TaskFactory taskFactory = TaskFactory.getInstance(2, 2, 2, 1);
        int nThreads = 7;

        ArrayList<int[]> transitions = new ArrayList<int[]>(Arrays.asList(new int[]{1, 3}, 
                                                                          new int[]{2, 4},
                                                                          new int[]{5, 7, 9},
                                                                          new int[]{6, 8, 10},
                                                                          new int[]{11, 13},
                                                                          new int[]{12, 14},
                                                                          new int[]{15, 16}));

        ArrayList<String> taskTypes = new ArrayList<String>(Arrays.asList("Loader", "Loader", "Filter", "Filter", "Resizer", "Resizer", "Exporter"));
        
        for (int i = 0; i < nThreads; i++) {
            Thread task = taskFactory.newTask(taskTypes.get(i), transitions.get(i), monitor);
            task.start();
        }

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<Task, Thread> tasks = taskFactory.getTasks();

        tasks.forEach((task, thread) -> {
           thread.interrupt(); 
           task.setStop(true);
        });

        monitor.endExecution();

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
