package com.soporte_tecnico;

import java.util.Arrays;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.soporte_tecnico.exceptions.InvalidMarkingException;

public class PetriNet {
    
    private static volatile PetriNet instance;
    private final RealMatrix incidenceMatrix;
    private RealVector marking;
    private int enabledByTokens[];


    /**
     * Constructor. Privado para garantizar singleton.
     * @param p0 Marcado inicial de P0.
     */
    private PetriNet(int p0) {
        incidenceMatrix = MatrixUtils.createRealMatrix(new double[][] {
               //T0 T1  T2  T3  T4  T5  T6  T7  T8  T9  T10 T11 T12 T13 T14 T15 T16
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

        marking = MatrixUtils.createRealVector(new double[] {0, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1});
        marking.setEntry(0, p0);

        enabledByTokens = new int[] {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        if (marking.getEntry(0) > 0) {
            enabledByTokens[1] = 1;
            enabledByTokens[2] = 1;
        }
    }


    /**
     * Devuelve una unica instancia de clase PetriNet. Si no existe instancia, crea una.
     * @param p0 Marcado inicial de P0.
     * @return puntero a la instancia de PetriNet.
     */
    public static PetriNet getInstance(int p0) {
        
        PetriNet result = instance;
        if (result != null) {
            return result;
        }
        
        synchronized(PetriNet.class) {
            if (instance == null) {
                instance = new PetriNet(p0);
            }
            return instance;
        }
    }


    /**
     * Devuelve el vector de marcado.
     * @return vector de marcado.
     */
    public RealVector getMarking() {
        return marking;
    }


    /**
     * Devuelve el vector de transiciones habilitadas.
     * @return vector de transiciones habilitadas.
     */
    public int[] getEnabledTransitions() {
        return enabledByTokens;
    }


    /**
     * Devuelve el numero de transiciones.
     * @return Numero de transiciones.
     */
    public int getNtransitions() {
        return this.incidenceMatrix.getColumnDimension();
    }


    /**
     * Crea un vector de disparo a partir del numero de la transicion que se desea disparar.
     * @param transition Numero de transicion que se desea disparar.
     * @return RealVector que es el vector de disparo.
     */
    private RealVector createTransitionVector(int transition) {
        double transitions[] = new double[incidenceMatrix.getColumnDimension()];
        Arrays.fill(transitions, 0);
        transitions[transition] = 1;

        return MatrixUtils.createRealVector(transitions);
    }


    /**
     * Verifica si se cumplen los invariantes de plaza de la red.
     * @return true si cumple los invariantes o false si no los cumple.
     */
    private boolean holdsPlaceInvariants() {
        if ((marking.getEntry(1) + marking.getEntry(2)) != 1) {
            return false;
        }
        if ((marking.getEntry(4) + marking.getEntry(5)) != 1) {
            return false;
        }
        if ((marking.getEntry(2) + marking.getEntry(3) + marking.getEntry(4) + marking.getEntry(19)) != 3) {
            return false;
        }
        if ((marking.getEntry(7) + marking.getEntry(8) + marking.getEntry(12)) != 1) {
            return false;
        }
        if ((marking.getEntry(10) + marking.getEntry(11) + marking.getEntry(13)) != 1) {
            return false;
        }
        if ((marking.getEntry(8) + marking.getEntry(9) + marking.getEntry(10) + marking.getEntry(12) + marking.getEntry(13)) != 2) {
            return false;
        }
        if ((marking.getEntry(15) + marking.getEntry(16) + marking.getEntry(17)) != 1) {
            return false;
        }
        if ((marking.getEntry(19) + marking.getEntry(20)) != 1) {
            return false;
        }
        return true;
    }


    /**
     * Actualiza el vector de transiciones habilitadas por marcado.
     */
    private void updateEnabledTransitions() {
        int places = incidenceMatrix.getRowDimension();
        int transitions = incidenceMatrix.getColumnDimension();
 
        for (int t = 1; t < transitions; t++) {
            enabledByTokens[t] = 1;
            for (int p = 0; p < places; p++) {
                if ((incidenceMatrix.getEntry(p, t) == -1) && (marking.getEntry(p) < 1)) {
                    enabledByTokens[t] = 0;
                    break;
                }
            }
        }
    }


    /**
     * Dispara una transicion, si la misma esta habilitada.
     * @param transition Numero de transicion que se desea disparar.
     * @return true si la transicion fue disparada, en caso contrario false.
     * @throws InvalidMarkingException
     */
    public boolean fire(int transition) throws InvalidMarkingException {
        if (enabledByTokens[transition] == 1) {
            marking = marking.add(incidenceMatrix.operate(createTransitionVector(transition)));
            if (!holdsPlaceInvariants()) {
                throw new InvalidMarkingException("Marcado Invalido. No se cumplen los invariantes de plaza.");
            }
            updateEnabledTransitions();
            return true;
        }
        else
            return false;
    }
}
