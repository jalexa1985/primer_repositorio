import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas de unidad compartidas del ciclo 3 (creacion colectiva - wiki).
 * IMPORTANTE: reemplacen "XxYy" por sus iniciales
 * (primeros apellidos + primera letra del segundo, en orden alfabetico).
 *
 * @author (sus nombres)
 * @version Ciclo 3 - 2026-2
 */
public class SlotMachineContestCTest {

    @Test
    public void accordingXxYyShouldSolveMachineWithOneWheelWithoutActions() {
        assertEquals(0, SlotMachineContest.solve(1).length);
    }

    @Test
    public void accordingXxYyShouldWinAfterSolving() {
        for (int n = 2; n <= 8; n++) {
            SlotMachine machine = new SlotMachine(n);
            SlotMachineContest.solve(machine, n);
            assertEquals(1, machine.distinctSymbols());
        }
    }
}
