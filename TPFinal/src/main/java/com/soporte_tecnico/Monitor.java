package com.soporte_tecnico;

import java.util.concurrent.Semaphore;

import com.soporte_tecnico.exceptions.InvalidMarkingException;

public class Monitor {
    
    private static volatile Monitor instance;
    private final Semaphore mutex;
    private final PetriNet petriNet;


    /**
     * Constructor. Privado para garantizar singleton.
     */
    private Monitor(PetriNet petriNet) {

        this.mutex = new Semaphore(1);
        this.petriNet = petriNet;
    }


    /**
     * Devuelve una unica instancia de clase Monitor. Si no existe instancia, crea una.
     * @return puntero a la instancia de Monitor.
     */
    public static Monitor getInstance(PetriNet petriNet) {
        
        Monitor result = instance;
        if (result != null) {
            return result;
        }
        
        synchronized(Monitor.class) {
            if (instance == null) {
                instance = new Monitor(petriNet);
            }
            return instance;
        }
    }


    /**
     * 
     * @param transition
     */
    public void fireTransition(int transition) {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        boolean k = true;
        
        while (k) {
            try {
               k = petriNet.fire(transition);
            } catch (InvalidMarkingException e) {
               System.err.println(e);
               System.exit(1);
            }

            if (k) {
                int[] enabledTransitions = petriNet.getEnabledTransitions();
                //TO DO: toda esta parte...
            }
            else {
                mutex.release();
                //TO DO: entrar a la cola de la transicion
            }
        }

    }


}
