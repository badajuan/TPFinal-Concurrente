package com.soporte_tecnico;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Politic {
    
    private final int nTransitions;         // Numero de transiciones
    private final String mode;              // Modo prioridad o balance
    private final Integer highPriorityT;    // Transicion inicial de segmento de maxima prioridad
    private final Integer lowPriorityT;     // Transicion inicial de segmento de minima prioridad
    private final double load;              // Carga del segmento de maxima prioridad
    
    
    // Mapa de segmentos de la red asociados a su transicion inicial
    private final Map<String, Integer> segmentTransition = new HashMap<String, Integer>() {{put("A", 1);
                                                                                            put("B", 2);
                                                                                            put("C", 5);
                                                                                            put("D", 6);
                                                                                            put("E", 11);
                                                                                            put("F", 12);}};

    // Mapa de transiciones iniciales de segmentos que pueden competir por prioridad
    private final Map<Integer, Integer> priorityPairs = new HashMap<Integer, Integer>() {{put(1, 2);
                                                                                          put(2, 1);
                                                                                          put(5, 6);
                                                                                          put(6, 5);
                                                                                          put(11, 12);
                                                                                          put(12, 11);}};

    /**
     * Constructor.
     * @param nTransitions numero de transiciones de la red de petri del monitor que implementa la politica.
     */
    Politic(int nTransitions) {
        this.nTransitions = nTransitions;
        this.mode = "Balance";
        this.highPriorityT = null;
        this.lowPriorityT = null;
        this.load = 0.0;
    }


    /**
     * Constructor. Implementa dar prioridad a un segmento sobre otro.
     * @param nTransitions numero de transiciones de la red de petri del monitor que implementa la politica.
     * @param mode modo balanceado o prioridad de segmento.
     * @param segment segmendo a priorizar.
     * @param laod carga del segmento a priorizar.
     * @throws RuntimeException
     */
    Politic(int nTransitions, String segment, double load) throws RuntimeException {
        this.nTransitions = nTransitions;
        this.load = load;
        this.highPriorityT = segmentTransition.get(segment);
        this.lowPriorityT = priorityPairs.get(this.highPriorityT);

        if (load == 0.0) {
            this.mode = "Balance";
        }
        else {
            this.mode = "Priority";
        }

        if (this.highPriorityT == null) {
            throw new RuntimeException("Segmento invalido. segment: " + segment);
        }   
    }


    /**
     * Selecciona una transicion para ser disparada. Es el algoritmo de planificacion/politica implementado por el monitor.
     * @param enabledTransitions array de transiciones habilitadas.
     * @param counterList lista con la cuenta de disparos por transicion.
     * @return transicion seleccionada para disparar.
     */
    public int selectTransition(int[] enabledTransitions, List<Double> counterList) {

        int min = Integer.MAX_VALUE;
        int selectedTransition = -1;

        for (int i = 1; i < nTransitions; i++) {
            if (enabledTransitions[i] == 1) {
                double occurrences = counterList.get(i);

                if (occurrences < min) {
                    if ("Priority".equals(mode) && lowPriorityT == i) {
                        double balance = (counterList.get(highPriorityT) - counterList.get(lowPriorityT)) / counterList.get(highPriorityT);
                        if (balance < this.load) {
                            continue;
                        }
                    }

                    min = (int)occurrences;
                    selectedTransition = i;
                }
            }
        }  

        return selectedTransition;
    }

}
