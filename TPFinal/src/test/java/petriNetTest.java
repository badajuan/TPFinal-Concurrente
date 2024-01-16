import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Precision;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.soporte_tecnico.PetriNet;

public class petriNetTest {
    
    @Test
    public void fireTest() {
        PetriNet petriNet = PetriNet.getInstance();

        petriNet.fire(1);
        petriNet.fire(3);

        RealVector marking = petriNet.getMarking();

        RealVector testMarking = MatrixUtils.createRealVector(new double[] {5, 1, 0, 3, 0, 1, 1, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1});

        int n = marking.getDimension();

        for (int i = 0; i < n; i++) {
            assertEquals(marking.getEntry(i), testMarking.getEntry(i), Precision.EPSILON);
        }

        System.out.println("fireTest: Test Passed");
    }
}
