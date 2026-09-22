import static org.junit.Assert.*;

import javax.swing.JOptionPane;
import org.junit.Test;

/**
 * Acceptance tests of the slot machine simulator, prepared for the
 * presentation. They work in visible mode: the user watches the simulator
 * and confirms if the behavior is right.
 *
 * @author slotMachine team
 * @version 2.0 (cycle 2)
 */
public class SlotMachineATest {

    /**
     * A player builds a machine, locks the wheels that match and rotates the
     * others step by step until getting the jackpot.
     */
    @Test
    public void shouldGetJackpotLockingAndRotatingWheels() {
        SlotMachine machine = new SlotMachine();
        machine.makeVisible();
        String[] colors = {"red", "gold", "royalblue", "limegreen", "darkorange"};
        for (int i = 0; i < colors.length; i++) {
            machine.addSymbol(i + 1, colors[i]);
        }
        for (int i = 1; i <= 4; i++) {
            machine.addWheel(i);
        }
        machine.spin(new String[] {"royalblue", "red", "royalblue", "limegreen"});
        pause();
        machine.lock(1);
        machine.lock(3);
        machine.spin(2, 2);
        machine.spin(4, -1);
        pause();
        assertTrue(machine.isJackpot());
        assertTrue(ask("¿Las cuatro ruedas muestran azul, las ruedas 1 y 3 están "
                       + "fijadas (marca roja) y la máquina luce ganadora (dorada)?"));
        machine.exit();
    }

    /**
     * A user manages wheels and symbols (swap, delete) and the simulator
     * refuses an invalid action with a message.
     */
    @Test
    public void shouldManageWheelsAndRefuseInvalidActions() {
        SlotMachine machine = new SlotMachine();
        machine.makeVisible();
        machine.addSymbol(1, "crimson");
        machine.addSymbol(2, "mediumseagreen");
        machine.addSymbol(3, "dodgerblue");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
        machine.placeSymbol(1, "dodgerblue");
        pause();
        machine.swap(1, 3);
        pause();
        machine.delSymbol("crimson");
        machine.addSymbol(1, "DodgerBlue");
        assertFalse(machine.ok());
        pause();
        assertArrayEquals(new String[] {"mediumseagreen", "mediumseagreen", "dodgerblue"},
                          machine.configuration());
        assertTrue(ask("¿Se intercambió la rueda azul al final, desapareció el símbolo "
                       + "carmesí y se mostró un mensaje al agregar otra vez 'DodgerBlue'?"));
        machine.exit();
    }

    /*
     * Wait so the user can see the simulator.
     */
    private void pause() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /*
     * Ask the user to confirm what is shown.
     */
    private boolean ask(String question) {
        return JOptionPane.showConfirmDialog(null, question, "Prueba de aceptación",
                   JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
