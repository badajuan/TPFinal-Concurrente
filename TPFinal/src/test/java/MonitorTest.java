import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Precision;
import org.junit.Test;

import com.soporte_tecnico.Monitor;

public class MonitorTest {
    
    /**
     * Test que prueba metodo fireTransition de Monitor, en forma secuencial. Se le pide al monitor disparar una secuencia
     * de transiciones. Se debe llegar al estado correcto.
     */
    @Test
    public void fireTransitionTest1() {

        Monitor monitor = Monitor.getInstance(0);

        monitor.fireTransition(0);
        monitor.fireTransition(0);

        for (int i = 1; i < 11; i++) {
            monitor.fireTransition(i);
        }

        monitor.fireTransition(11);
        monitor.fireTransition(13);
        monitor.fireTransition(12);
        monitor.fireTransition(14);
        
        for (int i = 0; i < 2; i++) {
            monitor.fireTransition(15);
            monitor.fireTransition(16);
        }

        RealVector marking = monitor.getPetriNet().getMarking();
        RealVector testMarking = MatrixUtils.createRealVector(new double[] {0, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1});
        int n = marking.getDimension();

        for (int i = 0; i < n; i++) {
            assertEquals(marking.getEntry(i), testMarking.getEntry(i), Precision.EPSILON);
        }        
    }
}
