package com.soporte_tecnico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

import com.soporte_tecnico.PetriNet.Status;
import com.soporte_tecnico.exceptions.InvalidMarkingException;
import com.soporte_tecnico.exceptions.TaskInterruptedException;
import com.soporte_tecnico.exceptions.TransitionTimedOutException;
import com.soporte_tecnico.exceptions.TransitionsMismatchException;

public class Monitor {
    
    private static volatile Monitor instance;          // Puntero a la instancia monitor.
    private final Semaphore mutex;                     // Mutex de exclusion mutua del monitor.
    private List<Double> counterList;                  // Lista con conteo de disparos por transicion.
    private final PetriNet petriNet;                   // Red de petri del monitor.
    private final Queues transitionQueues;             // Colas de condicion.
    private final Politic politic;                     // Politica del monitor.
    private final Log log;                             // Log de disparos.


    /**
     * Constructor. Privado para garantizar singleton.
     * @param p0 Cantidad inicial de tokens en la plaza p0 de la red de petri.
     */
    private Monitor(int p0) {
        this.mutex = new Semaphore(1);
        this.petriNet = PetriNet.getInstance(p0);
        this.transitionQueues = new Queues(this.petriNet.getNtransitions());
        this.politic = new Politic(this.petriNet.getNtransitions());
        this.counterList = new ArrayList<>(Collections.nCopies(this.petriNet.getNtransitions(), 0.0));
        this.log = Log.getInstance();
    }


    /**
     * Constructor. Privado para garantizar singleton. Implementa politica donde 
     * se da prioridad a un segmento sobre otro.
     * @param p0
     * @param mode modo balanceado o prioridad de segmento.
     * @param segment segmendo a priorizar.
     * @param laod carga del segmento a priorizar.
     */
    private Monitor(int p0, String segment, double load) {
        this.mutex = new Semaphore(1);
        this.petriNet = PetriNet.getInstance(p0);
        this.transitionQueues = new Queues(this.petriNet.getNtransitions());
        this.counterList = new ArrayList<>(Collections.nCopies(this.petriNet.getNtransitions(), 0.0));
        this.log = Log.getInstance();
        this.politic = new Politic(this.petriNet.getNtransitions(), segment, load);

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
     * Devuelve una unica instancia de clase Monitor. Si no existe instancia, crea una.
     * @param p0 Cantidad inicial de tokens en la plaza p0 de la red de petri.
     * @param mode modo balanceado o prioridad de segmento.
     * @param segment segmendo a priorizar.
     * @param laod carga del segmento a priorizar.
     * @return puntero a la instancia de Monitor.
     */
    public static Monitor getInstance(int p0, String segment, double load) {
        
        Monitor result = instance;
        if (result != null) {
            return result;
        }
        
        synchronized(Monitor.class) {
            if (instance == null) {
                instance = new Monitor(p0, segment, load);
            }
            return instance;
        }
    }


    /**
     * Obtiene el marcado actual de la red de petri del monitor.
     * @return marcado de la red.
     */
    public RealVector getCurrentState() {
        return petriNet.getMarking();
    }


    /**
     * Devuelve la lista con la cuenta de disparos realizados por transición.
     * @return lista de disparos.
     */
    public List<Double> getCounterList() {
        return this.counterList;
    }

    
    /**
     * Calcula que transiciones estan habilitadas y bloqueadas al mismo tiempo y devuelve un array donde un 1 representa una 
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
     * Establece las ventanas de tiempo para cada transicion.
     * @param times ArrayList con pares [alfa.beta]
     */
    public void setTransitionsTime(ArrayList<Pair<Long, Long>> times) {
        petriNet.setTransitionsTime(times);
    } 


    /**
     * Toma la desicion sobre que transición disparar y que hilo debe realizar su tarea.
     * Implementa una politica Signal and Continue. 
     * @param transition transicion que un hilo solicita disparar.
     */
    public void fireTransition(int transition) {
        // Adquiere el mutex del monitor.
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        boolean k = true;
        
        while (k) {
            // Intenta disparar una transición.
            try {
               k = petriNet.fire(transition);
            } catch (InvalidMarkingException e) {
               System.err.println(e);
               System.exit(1);
            }
            
            // Si k = true, la transición fue disparada.
            if (k) {
                log.addTransition(transition);                                           // Registra transición disparada.
                counterList.set(transition, counterList.get(transition) + 1.0);          // Incrementa la cuenta de disparos de la transición.
                int[] enabledTransitions = petriNet.getEnabledTransitions();             // Obtiene listado de transiciones habilitadas por token.
                int[] blockedList = transitionQueues.getBlockedList();                   // Obtiene listado de colas de condicion con hilos esperando.
                int[] enabledBlockedTransitions = new int[petriNet.getNtransitions()];

                // Obtiene un listado de transiciones habilitadas y que corresponden con colas de condicion con hilos esperando.
                try {
                    enabledBlockedTransitions = getEnabledBlockedTransitions(enabledTransitions, blockedList);
                } catch (TransitionsMismatchException e) {
                    System.err.println(e);
                    System.exit(1);
                }

                boolean allQueuesEmpty = Arrays.stream(enabledBlockedTransitions).allMatch(value -> value == 0);   // true si no hay hilos esperando en transiciones habilitadas.

                // Si hay hilos esperando en transiciones habilitadas, la politica despierta un hilo para que intente disparar.
                if (!allQueuesEmpty) {
                    int transitionToFire = politic.selectTransition(enabledBlockedTransitions, this.counterList);
                    if (transitionToFire == -1) {
                        k = false;
                    }
                    transitionQueues.release(transitionToFire);
                    return;
                }
                // Si no hay hilos esperando en transiciones habilitadas, sale del monitor.
                else {
                    k = false;
                }

            }
            // k = false. Se revisan las causas de porque no se disparo la transicion.
            else {
                // La transicion no esta habilitada por tokens. El hilo entra a cola de condicion.
                if (petriNet.getTransitionStatus(transition) == Status.NO_TOKENS) {
                    mutex.release();
                    try {
                        transitionQueues.acquire(transition);
                    } catch (TaskInterruptedException e) {
                        k = false;
                        return;
                    }
                    
                    k = true;    
                }
                // El hilo intentó disparar antes de que se cumpla el tiepo alfa.
                else if (petriNet.getTransitionStatus(transition) == Status.BEFORE_WINDOW) {
                    mutex.release();
                    long time = System.currentTimeMillis();
                    long transitionTimeStamp = petriNet.getTransitionTimeStamp(transition);
                    Pair<Long, Long> transitionTimePair = petriNet.getTransitionTimes(transition);
                    long sleepTime = transitionTimeStamp + transitionTimePair.getKey() - time;

                    try {
                        TimeUnit.MILLISECONDS.sleep(sleepTime);
                        mutex.acquire();
                    } catch (InterruptedException e) {
                        throw new TaskInterruptedException("Tarea interrumpida");
                    }

                    k = true;
                }
                // El hilo intentó disparar pasado el intervalo de tiempo de la transicion (despues de beta).
                else {
                    throw new TransitionTimedOutException("Intento de disparar transicion pasada la ventana temporal. transición: " + transition);
                }
                
            }
        }
        mutex.release();
    }


    Log getLog() {
        return this.log;
    }
}
