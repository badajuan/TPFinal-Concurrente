package com.soporte_tecnico;

import java.util.List;

public class Politic {
    
    private final int nTransitions;  // Numero de transiciones

    
    /**
     * Constructor.
     * @param nTransitions numero de transiciones de la red de petri del monitor que
     * implementa la politica.
     */
    Politic(int nTransitions) {
        this.nTransitions = nTransitions;
    }


    /**
     * Selecciona una transicion para ser disparada. Es el algoritmo de planificacion/politica
     * implementado por el monitor.
     * @param enabledTransitions array de transiciones habilitadas.
     * @param counterList lista con la cuenta de disparos por transicion.
     * @return transicion seleccionada para disparar.
     */
    public int selectTransition(int[] enabledTransitions, List<Integer> counterList) {

        int min = Integer.MAX_VALUE;
        int selectedTransition = -1;

        for (int i = 1; i < nTransitions; i++) {
            if (enabledTransitions[i] == 1) {
                int occurrences = counterList.get(i);

                if (occurrences < min) {
                    min = occurrences;
                    selectedTransition = i;
                }
            }
        }  

        return selectedTransition;
    }

}
