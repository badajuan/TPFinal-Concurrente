package com.soporte_tecnico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import com.soporte_tecnico.exceptions.TaskInterruptedException;


public class Queues {
    
    private ArrayList<Semaphore> queues;       // Lista de colas de condición
    private int[] blockedList;                 // Array que representa si hay hilos en alguna cola de condición
    private final int nQueues;                 // Cantidad de dolas de condición


    /**
     * Constructor.
     * @param n cantidad de colas de condición a inicializar.
     */
    public Queues(int n) {
        this.nQueues = n;
        this.queues = new ArrayList<Semaphore>();
        this.blockedList = new int[n];

        for (int i = 0; i < this.nQueues; i++) {
            this.queues.add(new Semaphore(0));          
        }

        Arrays.fill(this.blockedList, 0);
    
    }


    /**
     * Devuelve el array de colas ocupadas.
     * @return array de colas oucpadas.
     */
    public int[] getBlockedList() {
        return this.blockedList;
    }


    /**
     * Manda un hilo a esperar en una cola de condición.
     * @param transition numero de la transicion/cola de condición.
     */
    public void acquire(int transition) {
        this.blockedList[transition] = 1;

        try {
            queues.get(transition).acquire();
        } catch (InterruptedException e) {
            throw new TaskInterruptedException("Tarea interrumpida");
        }
    }


    /**
     * Libera un hilo de una cola de condición.
     * @param transition numero de la transición/cola de condición.
     */
    public void release(int transition) {
        queues.get(transition).release();
        blockedList[transition] = 0;
    }
}
