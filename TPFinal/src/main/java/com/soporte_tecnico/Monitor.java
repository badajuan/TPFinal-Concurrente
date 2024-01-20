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
    
    private static volatile Monitor instance;          // Puntero a la instancia monitor
    private final Semaphore mutex;                     // Mutex de exclusion mutua del monitor
    private List<Integer> counterList;                 // Lista con conteo de disparos por transicion
    private final PetriNet petriNet;                   // Red de petri del monitor
    private final Queues transitionQueues;             // Colas de condicion
    private final Politic politic;                     // Politica del monitor
    private final Log log;                             // Log de disparos


    /**
     * Constructor. Privado para garantizar singleton.
     * @param p0 Cantidad inicial de tokens en la plaza p0 de la red de petri.
     */
    private Monitor(int p0) {
        this.mutex = new Semaphore(1);
        this.petriNet = PetriNet.getInstance(p0);
        this.transitionQueues = new Queues(this.petriNet.getNtransitions());
        this.politic = new Politic(this.petriNet.getNtransitions());
        this.counterList = new ArrayList<>(Collections.nCopies(this.petriNet.getNtransitions(), 0));
        this.log = Log.getInstance();
    }


    /**
     * Devuelve una unica instancia de clase Monitor. Si no existe instancia, crea una.
     * @param p0 Cantidad inicial de tokens en la plaza p0 de la red de petri.
     * @return puntero a la instancia de Monitor.
     */
    public static Monitor getInstance(int p0) {
        
        Monitor result = instance;
        if (result != null) {
            return result;
        }
        
        synchronized(Monitor.class) {
            if (instance == null) {
                instance = new Monitor(p0);
            }
            return instance;
        }
    }


    /**
     * Devuelve la red de petri del monitor.
     * @return red de petri del monitor.
     */
    public PetriNet getPetriNet() {
        return this.petriNet;
    }


    /**
     * Devuelve la lista con la cuenta de disparos realizados por transicion.
     * @return lista de disparos.
     */
    public List<Integer> getCounterList() {
        return this.counterList;
    }

    
    /**
     * Calcula que transicione estan habilitadas y bloqueadas al mismo tiempo y devuelve un array donde un 1 representa una 
     * transicion bloqueada y habilitada.
     * @param enabledTransitions array de transiciones bloqueadas.
     * @param blockedList array de transiciones habilitadas.
     * @return array de transiciones bloqueadas y habilitadas.
     * @throws TransitionsMismatchException
     */
    private int[] getEnabledBlockedTransitions(int[] enabledTransitions, int[] blockedList) throws TransitionsMismatchException {
        if (enabledTransitions.length != blockedList.length) {
            throw new TransitionsMismatchException("Numero de transiciones incorrecto. Transiciones habilitadas: " + enabledTransitions.length + 
                                                   " - Colas de transiciones: " +blockedList.length);
        }

        return IntStream.range(0, enabledTransitions.length).map(i -> enabledTransitions[i] & blockedList[i]).toArray();
    }


    /**
     * Toma la desicion sobre que transicion disparar y que hilo debe realizar su tarea.
     * Implementa una politica Signal and Continue. 
     * @param transition transicion que un hilo solicita disparar.
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
