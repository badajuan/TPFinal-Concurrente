import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.util.Precision;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;

import com.soporte_tecnico.PetriNet;

public class petriNetTest {

    /**
     * Test que prueba el metodo fire de PetriNet. No hay transiciones habilitadas, por lo que
     * todos los disparos deben devolver false.
     */
    @Test
    public void fireTest1() {
        PetriNet petriNet = PetriNet.getInstance(0);
        boolean result = true;
        int n = petriNet.getNtransitions();

        for (int i = 1; i < n; i++) {
            result = petriNet.fire(i);
            assertFalse(result);   
        }

        System.out.println("petriNetTest: fireTest1 Test Passed");
    }

    /**
     * Test que prueba el metodo fire de PetriNet. Luego de realizar disparos de transicion, se deben
     * cumplir los invariantes de plaza y se debe llegar al estado correcto.
     */
    @Test
    public void fireTest2(){
        
        PetriNet petriNet = PetriNet.getInstance(0);

        ArrayList<Pair<Long, Long>> alphaBeta = new ArrayList<>(Arrays.asList(new Pair<>(0L,0L), new Pair<>(0L,0L), new Pair<>(0L,0L),
                                                                              new Pair<>(0L,0L), new Pair<>(0L,0L), new Pair<>(0L,0L),
                                                                              new Pair<>(0L,0L), new Pair<>(0L,0L), new Pair<>(0L,0L),
                                                                              new Pair<>(0L,0L), new Pair<>(0L,0L), new Pair<>(0L,0L),
                                                                              new Pair<>(0L,0L), new Pair<>(0L,0L), new Pair<>(0L,0L),
                                                                              new Pair<>(0L,0L), new Pair<>(0L,0L)));

        boolean result = false;

        petriNet.setTransitionsTime(alphaBeta);

        result = petriNet.fire(0);
        assertTrue(result);

        for (int i = 1; i < 10; i = i+2) {
            result = petriNet.fire(i);
            assertTrue(result);
        }

        for (int i = 11; i < 16; i = i+2) {
            result = petriNet.fire(i);
            assertTrue(result);
        }        
        
        result = petriNet.fire(16);
        assertTrue(result);

        RealVector marking = petriNet.getMarking();
        RealVector testMarking = MatrixUtils.createRealVector(new double[] {0, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1});
        int n = marking.getDimension();

        for (int i = 0; i < n; i++) {
            assertEquals(marking.getEntry(i), testMarking.getEntry(i), Precision.EPSILON);
        }

        System.out.println("petriNetTest: fireTest1 Test Passed");
        petriNet = null;
    }
}
