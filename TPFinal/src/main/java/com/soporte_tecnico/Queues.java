package com.soporte_tecnico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;


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
        try {
            queues.get(transition).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.blockedList[transition] = 1;
    }


    /**
     * 
     * @param transition
     */
    public void release(int transition) {
        queues.get(transition).release();

        if (!queues.get(transition).hasQueuedThreads()) {
            blockedList[transition] = 0;
        }
    }
}
