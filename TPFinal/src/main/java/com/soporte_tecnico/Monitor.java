package com.soporte_tecnico;

import java.util.concurrent.Semaphore;

public class Monitor {
    
    private final Semaphore mutex;

    public Monitor() {

        this.mutex = new Semaphore(1);
    }

    public void fireTransition(int transition) {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        boolean k = true;
        
        while (k) {
             
        }

    }


}
