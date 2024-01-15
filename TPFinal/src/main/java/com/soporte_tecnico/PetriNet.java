package com.soporte_tecnico;

import java.util.Arrays;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class PetriNet {
    
    private static volatile PetriNet instance;
    private final int nTransitions = 16;
    private final RealMatrix incidenceMatrix;
    private RealVector marking;
    private int enabledByTokens[];


    /**
     * 
     */
    private PetriNet() {
        incidenceMatrix = MatrixUtils.createRealMatrix(new double[][] {
               //T0 T1 	T2 	T3 	T4 	T5 	T6 	T7 	T8 	T9 	T10 T11 T12 T13 T14 T15 T16
                {1, -1, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, //P0 	
                {0, -1,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, //P1 	
                {0,  1,  0, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, //P2 	
                {0, -1, -1,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, -1,  1}, //P3 	
                {0,  0,  1,  0, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, //P4 	
                {0,  0, -1,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, //P5 	
                {0,  0,  0,  1,  1, -1, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, //P6 	
                {0,  0,  0,  0,  0, -1,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0}, //P7 	
                {0,  0,  0,  0,  0,  1,  0, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0}, //P8 	
                {0,  0,  0,  0,  0, -1, -1,  0,  0,  1,  1,  0,  0,  0,  0,  0,  0}, //P9 	
                {0,  0,  0,  0,  0,  0,  1,  0, -1,  0,  0,  0,  0,  0,  0,  0,  0}, //P10 
                {0,  0,  0,  0,  0,  0, -1,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0}, //P11 
                {0,  0,  0,  0,  0,  0,  0,  1,  0, -1,  0,  0,  0,  0,  0,  0,  0}, //P12 
                {0,  0,  0,  0,  0,  0,  0,  0,  1,  0, -1,  0,  0,  0,  0,  0,  0}, //P13 
                {0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1, -1, -1,  0,  0,  0,  0}, //P14 
                {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, -1, -1,  1,  1,  0,  0}, //P15 
                {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0, -1,  0,  0,  0}, //P16 
                {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0, -1,  0,  0}, //P17 
                {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1, -1,  0}, //P18 
                {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1, -1}, //P19 
                {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, -1,  1}  //P20 
                });

        marking = MatrixUtils.createRealVector(new double[] {6, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1});

        enabledByTokens = new int[] {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    /**
     * 
     * @return
     */
    public static PetriNet getInstance() {
        
        PetriNet result = instance;
        if (result != null) {
            return result;
        }
        
        synchronized(TaskFactory.class) {
            if (instance == null) {
                instance = new PetriNet();
            }
            return instance;
        }
    }

    /**
     * 
     * @param transition
     * @return
     */
    private RealVector createTransitionVector(int transition) {
        double transitions[] = new double[nTransitions];
        Arrays.fill(transitions, 0);

        return MatrixUtils.createRealVector(transitions);
    }

    /**
     * 
     * @return
     */
    private boolean holdsMarkingInvariants() {
        return false;
    }

    /**
     * 
     * @return
     */
    public boolean fire(int transition) {
        if (enabledByTokens[transition] == 1) {
            marking = marking.add(incidenceMatrix.operate(createTransitionVector(transition)));
            //TO DO: Actualizar array de transiciones sensibilizadas.
            //TO DO: Verificar si se cumplen los invariantes de plaza. Crear un metodo que retorne true o false. Si es false, lanzar exepcion y terminar todo.
            return true;
        }
        else
            return false;
    }
}
