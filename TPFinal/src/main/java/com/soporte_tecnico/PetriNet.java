package com.soporte_tecnico;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

import com.soporte_tecnico.exceptions.InvalidMarkingException;

public class PetriNet {
    
    private static volatile PetriNet instance;                               // Puntero a la instancia PetriNet.
    private final RealMatrix incidenceMatrix;                                // Matriz de incidencia de la red de petri .
    private RealVector marking;                                              // Vector de marcado de la red.
    private int[] enabledByTokens;                                           // Vector de transiciones habilitadas por tokens.
    private int proseccedTokensCounter;

    private long[] transitionsTimeStamps;                                    // Marca de tiempo de cuando una transición fue habilitada por tokens.
    private  ArrayList<Pair<Long, Long>> alphaBeta;                          // Par alfa-beta de tiempos de intervalo de cada transicion.

    public enum Status {ENABLED, NO_TOKENS, BEFORE_WINDOW, AFTER_WINDOW}     // Flags de status que indican si una transición se puede disparar o porque no.

    private Status[] transitionsStatus;                                      // Vector de status para cada transición.


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


        // Inicializa la red como no temporalizada.
        alphaBeta = new ArrayList<>(Arrays.asList(new Pair<>(0L,0L), new Pair<>(0L,0L), new Pair<>(0L,0L),       // T0, T1, T2
                                                  new Pair<>(0L,0L), new Pair<>(0L,0L), new Pair<>(0L,0L),       // T3, T4, T5
                                                  new Pair<>(0L,0L), new Pair<>(0L,0L), new Pair<>(0L,0L),       // T6, T7, T8
                                                  new Pair<>(0L,0L), new Pair<>(0L,0L), new Pair<>(0L,0L),       // T9, T10, T11
                                                  new Pair<>(0L,0L), new Pair<>(0L,0L), new Pair<>(0L,0L),       // T12, T13, T14
                                                  new Pair<>(0L,0L), new Pair<>(0L,0L)));                            // T15, T16

                                                          // 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20  
        marking = MatrixUtils.createRealVector(new double[] {0, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1});
        marking.setEntry(0, p0);

        enabledByTokens = new int[incidenceMatrix.getColumnDimension()];
        Arrays.fill(enabledByTokens, 0);

        enabledByTokens[0] = 1;
        if (marking.getEntry(0) > 0) {
            enabledByTokens[1] = 1;
            enabledByTokens[2] = 1;
        }

        transitionsTimeStamps = new long[incidenceMatrix.getColumnDimension()];
        transitionsStatus = new Status[incidenceMatrix.getColumnDimension()];

        updateEnabledTransitions();

        proseccedTokensCounter = 0;
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
     * Devuelve el stado de disparo de una transición.
     * @param transition transición que se desea analizar.
     * @return estado de la transición.
     */
    public Status getTransitionStatus(int transition) {
        return transitionsStatus[transition];
    }


    /**
     * Obtiene el intervalo de tiempo [alfa,beta] de una transición.
     * @param transition transición que se desea analizar.
     * @return par de tiempos alfa y beta.
     */
    public Pair<Long, Long> getTransitionTimes(int transition) {
        return alphaBeta.get(transition);
    }


    /**
     * Obtiene la marca de tiempo de cuando una transición fue habilitada por tokens.
     * @param transition transición que se desea analizar.
     * @return time stamp de la transición.
     */
    public long getTransitionTimeStamp(int transition) {
        return transitionsTimeStamps[transition];
    }


    /**
     * Setea el time stamp de una transición.
     * @param transition transición a configurar.
     */
    private void setTransitionTimeStamp(int transition) {
        transitionsTimeStamps[transition] = System.currentTimeMillis();
    }


    /**
     * Establece las ventanas de tiempo para cada transicion.
     * @param times ArrayList con pares [alfa.beta]
     */
    public void setTransitionsTime(ArrayList<Pair<Long, Long>> times) {
        alphaBeta = times;
    }
    
    
    /**
     * Verifica si una transición esta habilitada. Para estar habilitada debe estar
     * habilitada por marcado (tokens) y por tiempo, es decir estar dentro de la
     * ventana temporal [alfa,beta].
     * @param transition transición a verificar.
     * @return true si la transición esta habilitada, false en caso contrario.
     */
    private boolean isEnabled(int transition) {
        // Si no está habilitada por marcado, devuelve false.
        if (enabledByTokens[transition] == 0) {
            transitionsStatus[transition] = Status.NO_TOKENS;
            return false;
        }
        // Si está habilitada por marcado, hay que verificar si está habilitada por tiempo.
        else if (enabledByTokens[transition] == 1){
            // Si alfa y beta son 0, no es una transición temporal.
            if (alphaBeta.get(transition).getKey() == 0L && alphaBeta.get(transition).getValue() == 0L) {
                transitionsStatus[transition] = Status.ENABLED;
                return true;
            }

            long time = System.currentTimeMillis();

            // Verifica si se intenta disparar antes del intervalo de tiempo de habilitación.
            if (time < (transitionsTimeStamps[transition] + alphaBeta.get(transition).getKey())) {
                transitionsStatus[transition] = Status.BEFORE_WINDOW;
                return false;
            }
            // Verifica si pasó el tiempo de habilitación.
            else if (time > (transitionsTimeStamps[transition] + alphaBeta.get(transition).getValue())) {
                transitionsStatus[transition] = Status.AFTER_WINDOW;
                return false;
            }
            // El disparo está dentro del intervalo de habilitación y la transición se dispara.
            else {
                transitionsStatus[transition] = Status.ENABLED;
                return true;
            }

        }
        else {
            throw new RuntimeException("Vector enabledByToken invalido. enabledByToken[" + transition + "] = " + enabledByTokens[transition]);
        }
    }


    /**
     * Crea un vector de disparo a partir del numero de la transición que se desea disparar.
     * @param transition Numero de transición que se desea disparar.
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
        if ((marking.getEntry(0) + marking.getEntry(2) + marking.getEntry(4) + marking.getEntry(6) + marking.getEntry(8)
        + marking.getEntry(10) + marking.getEntry(12) + marking.getEntry(13) + marking.getEntry(14) + marking.getEntry(16)
        + marking.getEntry(17) + marking.getEntry(18) + marking.getEntry(19)) != proseccedTokensCounter) {
            return false;
        }
        return true;
    }


    /**
     * Actualiza el vector de transiciones habilitadas por marcado. Utiliza la matriz de incidencia para obtener los
     * arcos que van hacia cada transición y el vector de marcado para corroborar si las plazas tienen tokens.
     */
    private void updateEnabledTransitions() {
        int places = incidenceMatrix.getRowDimension();
        int transitions = incidenceMatrix.getColumnDimension();
 
        for (int t = 0; t < transitions; t++) {
            enabledByTokens[t] = 1;
            for (int p = 0; p < places; p++) {
                if ((incidenceMatrix.getEntry(p, t) == -1) && (marking.getEntry(p) < 1)) {
                    enabledByTokens[t] = 0;
                    break;
                }
            }
            setTransitionTimeStamp(t);
        }
    }


    /**
     * Dispara una transición, si la misma esta habilitada.
     * @param transition Numero de transición que se desea disparar.
     * @return true si la transición fue disparada, en caso contrario false.
     * @throws InvalidMarkingException
     */
    public boolean fire(int transition) throws InvalidMarkingException {
        // Verifica si la transición está habilitada.
        if (isEnabled(transition)) {
            // Utiliza la ecuación fundamental para actualizar el estado.
            marking = marking.add(incidenceMatrix.operate(createTransitionVector(transition)));
            if (transition == 0) {
                proseccedTokensCounter++;
            }
            if (transition == 16) {
                proseccedTokensCounter--;
            }
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
