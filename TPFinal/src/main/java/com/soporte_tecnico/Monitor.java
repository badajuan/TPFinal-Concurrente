package com.soporte_tecnico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

import com.soporte_tecnico.exceptions.InvalidMarkingException;
import com.soporte_tecnico.exceptions.TaskInterruptedException;
import com.soporte_tecnico.exceptions.TransitionsMismatchException;

public class Monitor {
    
    private static volatile Monitor instance;
    private final Semaphore mutex;
    private List<Integer> counterList;
    private final PetriNet petriNet;
    private final Queues transitionQueues;
    private final Politic politic;
    private final Log log;


    /**
     * Constructor. Privado para garantizar singleton.
     */
    private Monitor(PetriNet petriNet) {
        this.mutex = new Semaphore(1);
        this.petriNet = petriNet;
        this.transitionQueues = new Queues(this.petriNet.getNtransitions());
        this.politic = new Politic(this.petriNet.getNtransitions());
        this.counterList = new ArrayList<>(Collections.nCopies(this.petriNet.getNtransitions(), 0));
        this.log = Log.getInstance();
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
     * Devuelve la red de petri del monitor. Para uso en testing.
     * @return red de petri del monitor.
     */
    public PetriNet getPetriNet() {
        return this.petriNet;
    }

    
    /**
     * 
     * @param enabledTransitions
     * @param blockedList
     * @return
     * @throws TransitionsMismatchException
     */
    private int[] getEnabledBlockedTransitions(int[] enabledTransitions, int[] blockedList) throws TransitionsMismatchException {
        if (enabledTransitions.length != blockedList.length) {
            throw new TransitionsMismatchException("Numero de transiciones incorrecto. Transiciones habilitadas: " + enabledTransitions.length + 
                                                   " - Colas de transiciones: " +blockedList.length);
        }

        return IntStream.range(0, enabledTransitions.length).map(i -> enabledTransitions[i] & blockedList[i]).toArray();
    }


    public void endExecution() {
        transitionQueues.releaseAll();
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
                counterList.set(transition, counterList.get(transition) + 1);
                log.logTransition(transition);
                int[] enabledTransitions = petriNet.getEnabledTransitions();
                int[] blockedList = transitionQueues.getBlockedList();
                int[] enabledBlockedTransitions = new int[petriNet.getNtransitions()];

                try {
                    enabledBlockedTransitions = getEnabledBlockedTransitions(enabledTransitions, blockedList);
                } catch (TransitionsMismatchException e) {
                    System.err.println(e);
                    System.exit(1);
                }

                boolean allQueuesEmpty = Arrays.stream(enabledBlockedTransitions).allMatch(value -> value == 0);

                if (!allQueuesEmpty) {
                    int transitionToFire = politic.selectTransition(enabledBlockedTransitions, this.counterList);
                    transitionQueues.release(transitionToFire);
                    return;
                }
                else {
                    k = false;
                }

            }
            else {
                mutex.release();
                try {
                    transitionQueues.acquire(transition);
                } catch (TaskInterruptedException e) {
                    k = false;
                    return;
                }
                
                k = true;
            }
        }
        mutex.release();
    }
}
