import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas de unidad de la solucion de la maraton (ciclo 3).
 * Todas las pruebas se ejecutan en modo invisible.
 *
 * @author (sus nombres)
 * @version Ciclo 3 - 2026-2
 */
public class SlotMachineContestTest {

    // ---------------- SlotMachine(n) ----------------

    @Test
    public void shouldCreateMachineWithNWheelsAndNSymbols() {
        SlotMachine machine = new SlotMachine(6);
        assertTrue(machine.ok());
        assertEquals(6, machine.configuration().length);
        assertEquals(6, machine.symbols().length);
    }

    @Test
    public void shouldCreateMachineWithDifferentColors() {
        SlotMachine machine = new SlotMachine(SlotMachine.maxSymbols());
        String[] symbols = machine.symbols();
        java.util.HashSet<String> different = new java.util.HashSet<String>();
        for (String s : symbols) {
            assertTrue(different.add(s));
        }
        assertEquals(SlotMachine.maxSymbols(), different.size());
    }

    @Test
    public void shouldHaveAllSymbolsOnEveryWheel() {
        SlotMachine machine = new SlotMachine(5);
        for (int wheel = 1; wheel <= 5; wheel++) {
            java.util.HashSet<String> seen = new java.util.HashSet<String>();
            for (int step = 0; step < 5; step++) {
                seen.add(machine.configuration()[wheel - 1]);
                machine.spin(wheel, 1);
            }
            assertEquals(5, seen.size());
        }
    }

    @Test
    public void shouldNotCreateMachineWithInvalidSize() {
        SlotMachine machine = new SlotMachine(0);
        assertFalse(machine.ok());
        assertEquals(0, machine.configuration().length);
        machine = new SlotMachine(SlotMachine.maxSymbols() + 1);
        assertFalse(machine.ok());
    }

    // ---------------- solve ----------------

    @Test
    public void shouldSolveRandomMachines() {
        for (int n = 1; n <= 12; n++) {
            for (int t = 0; t < 20; t++) {
                SlotMachine machine = new SlotMachine(n);
                SlotMachineContest.solve(machine, n);
                assertTrue("n = " + n, machine.isJackpot());
            }
        }
    }

    @Test
    public void shouldSolveBigMachineWithinContestLimit() {
        SlotMachine machine = new SlotMachine(50);
        int[][] actions = SlotMachineContest.solve(machine, 50);
        assertTrue(machine.isJackpot());
        assertTrue(actions.length <= 10000);
    }

    @Test
    public void shouldReturnValidActions() {
        int n = 7;
        int[][] actions = SlotMachineContest.solve(n);
        assertNotNull(actions);
        for (int[] action : actions) {
            assertEquals(2, action.length);
            assertTrue(action[0] >= 1 && action[0] <= n);
            assertTrue(action[1] >= 1 && action[1] < n);
        }
    }

    @Test
    public void shouldNotNeedActionsWhenAlreadyWinning() {
        SlotMachine machine = new SlotMachine(4);
        String first = machine.symbols()[0];
        machine.spin(new String[]{first, first, first, first});
        assertEquals(0, SlotMachineContest.solve(machine, 4).length);
        assertTrue(machine.isJackpot());
    }

    @Test
    public void shouldSolveWhenAllSymbolsAreDifferent() {
        SlotMachine machine = new SlotMachine(4);
        String[] s = machine.symbols();
        machine.spin(new String[]{s[0], s[1], s[2], s[3]});
        SlotMachineContest.solve(machine, 4);
        assertTrue(machine.isJackpot());
    }

    @Test
    public void shouldNotSolveInvalidSizes() {
        assertEquals(0, SlotMachineContest.solve(0).length);
        assertEquals(0, SlotMachineContest.solve(-3).length);
    }

    @Test
    public void shouldNotSimulateInvalidSizes() {
        SlotMachineContest.simulate(0);
        SlotMachineContest.simulate(SlotMachineContest.MAX_SIMULATION + 1);
    }
}
