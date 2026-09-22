import static org.junit.Assert.*;

import javax.swing.JOptionPane;
import org.junit.Test;

/**
 * Acceptance tests of SlotMachine (for the presentation), in visible mode.
 * The user watches the simulator and confirms if it behaves as expected.
 *
 * @author slotMachine team
 * @version 2.0 (cycle 2)
 */
public class SlotMachineATest {

    /**
     * A player builds a machine, spins it and, at the end, leaves it in a
     * winning configuration: the machine must look golden.
     */
    @Test
    public void shouldPlayUntilWinning() {
        SlotMachine machine = new SlotMachine();
        machine.makeVisible();
        String[] colors = {"red", "royalblue", "gold", "limegreen", "darkorange"};
        for (int i = 0; i < colors.length; i++) {
            machine.addSymbol(i + 1, colors[i]);
        }
        for (int i = 1; i <= 4; i++) {
            machine.addWheel(i);
        }
        machine.spin();
        machine.spin();
        machine.spin(new String[]{"gold", "gold", "gold", "gold"});
        assertTrue(machine.isJackpot());
        assertTrue(ask("¿La máquina tiene 4 ruedas, giró dos veces y ahora luce dorada "
                + "con las luces verdes (ganadora)?"));
        machine.exit();
    }

    /**
     * A player locks a wheel, swaps wheels and rotates a wheel step by step;
     * the locked wheel does not move and an attempt to move it shows a message.
     */
    @Test
    public void shouldLockSwapAndRotateStepByStep() {
        SlotMachine machine = new SlotMachine();
        machine.makeVisible();
        String[] colors = {"crimson", "teal", "orchid", "khaki"};
        for (int i = 0; i < colors.length; i++) {
            machine.addSymbol(i + 1, colors[i]);
        }
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
        machine.placeSymbol(1, "orchid");
        machine.lock(1);
        machine.swap(1, 3);
        machine.spin(2, 7);
        machine.spin(3, 1);
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"crimson", "khaki", "orchid"}, machine.configuration());
        assertTrue(ask("¿La rueda fijada (triángulo rojo) pasó a la derecha, la rueda del "
                + "centro giró paso a paso y apareció un mensaje al intentar girar la fijada?"));
        machine.exit();
    }

    /*
     * Ask the user to confirm what is seen.
     */
    private boolean ask(String question) {
        return JOptionPane.showConfirmDialog(null, question, "Prueba de aceptación",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
