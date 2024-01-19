package com.soporte_tecnico;

import java.util.List;

public class Politic {
    
    private final int nTransitions;
    
    Politic(int nTransitions) {
        this.nTransitions = nTransitions;
    }

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
