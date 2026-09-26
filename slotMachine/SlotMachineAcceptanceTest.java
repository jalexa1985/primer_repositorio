import static org.junit.Assert.*;
import javax.swing.JOptionPane;
import org.junit.Test;

/**
 * Pruebas de aceptacion para la presentacion (modo visible).
 * El usuario observa la simulacion y confirma si el resultado es correcto.
 *
 * @author (sus nombres)
 * @version Ciclo 3 - 2026-2
 */
public class SlotMachineAcceptanceTest {

    /**
     * Crea una maquina, fija una rueda, la intercambia, la rota paso a paso
     * y la deja en una configuracion ganadora.
     */
    @Test
    public void shouldManageAndWinAMachine() {
        SlotMachine machine = new SlotMachine();
        machine.makeVisible();
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "gold");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
        machine.spin(new String[]{"red", "blue", "gold"});
        machine.lock(2);
        machine.swap(1, 3);
        machine.unlock(2);
        machine.spin(2, 4);
        machine.spin(new String[]{"gold", "gold", "gold"});
        int answer = JOptionPane.showConfirmDialog(null,
            "¿La maquina quedo con las tres ruedas en gold y el cuerpo dorado?");
        machine.exit();
        assertEquals(JOptionPane.YES_OPTION, answer);
    }

    /**
     * Simula la solucion de la maraton para una maquina de 5 ruedas.
     */
    @Test
    public void shouldSimulateContestSolution() {
        SlotMachineContest.simulate(5);
        int answer = JOptionPane.showConfirmDialog(null,
            "¿Todas las ruedas muestran el mismo simbolo y el cuerpo es dorado?");
        Canvas.close();
        assertEquals(JOptionPane.YES_OPTION, answer);
    }
}
