package com.soporte_tecnico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Politic {
    
    private final int nTransitions;
    private List<Integer> counterList;
    
    Politic(int nTransitions) {
        this.nTransitions = nTransitions;
        this.counterList = new ArrayList<>(Collections.nCopies(this.nTransitions, 0));
    }


    public int selectTransition(int[] enabledTransitions) {

        int min = Integer.MAX_VALUE;
        int selectedTransition = -1;


        for (int i = 0; i < nTransitions; i++) {
            if (enabledTransitions[i] == 1) {
                int occurrences = counterList.get(i);

                if (occurrences < min) {
                    min = occurrences;
                    selectedTransition = i;
                }
            }
        }

        counterList.set(selectedTransition, counterList.get(selectedTransition) + 1);

        return selectedTransition;
    }

}
