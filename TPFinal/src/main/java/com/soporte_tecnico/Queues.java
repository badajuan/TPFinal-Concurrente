package com.soporte_tecnico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import com.soporte_tecnico.exceptions.TaskInterruptedException;


public class Queues {
    private ArrayList<Semaphore> queues;
    private int[] blockedList;
    private final int nQueues;

    /**
     * 
     * @param n
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
     * 
     * @return
     */
    public int[] getBlockedList() {
        return this.blockedList;
    }


    /**
     * 
     * @param transition
     */
    public void acquire(int transition) {
        synchronized (this) {
            this.blockedList[transition] = 1;
        }
        try {
            queues.get(transition).acquire();
        } catch (InterruptedException e) {
            throw new TaskInterruptedException("Tarea interrumpida");
        }
    }


    /**
     * 
     * @param transition
     */
    public void release(int transition) {
        queues.get(transition).release();

        synchronized (this) {
            blockedList[transition] = 0;
        }
    }


    public void releaseAll() {
        for (Semaphore queue : queues) {
            queue.release();
        }
    }
}
