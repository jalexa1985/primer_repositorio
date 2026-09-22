import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Collective unit tests of the slot machine simulator (cycle 2).
 * The names of the tests include the initials of the authors.
 * All the tests work in invisible mode.
 *
 * @author slotMachine team
 * @version 2.0 (cycle 2)
 */
public class SlotMachineCC2Test {

    @Test
    public void accordingXXYYShouldSwapWheelsKeepingTheirSymbols() {
        SlotMachine machine = new SlotMachine();
        machine.addSymbol(1, "orange");
        machine.addSymbol(2, "purple");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.placeSymbol(2, "purple");
        machine.swap(1, 2);
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"purple", "orange"}, machine.configuration());
    }

    @Test
    public void accordingXXYYShouldNotRotateALockedWheel() {
        SlotMachine machine = new SlotMachine();
        machine.addSymbol(1, "orange");
        machine.addSymbol(2, "purple");
        machine.addWheel(1);
        machine.lock(1);
        machine.spin(1, 3);
        assertFalse(machine.ok());
        assertArrayEquals(new String[] {"orange"}, machine.configuration());
    }

    @Test
    public void accordingXXYYShouldReachJackpotSettingAConfiguration() {
        SlotMachine machine = new SlotMachine();
        machine.addSymbol(1, "orange");
        machine.addSymbol(2, "purple");
        machine.addSymbol(3, "teal");
        machine.addWheel(1);
        machine.addWheel(1);
        machine.spin(1, 1);
        assertFalse(machine.isJackpot());
        machine.spin(new String[] {"teal", "teal"});
        assertTrue(machine.isJackpot());
        assertEquals(1, machine.distinctSymbols());
    }
}
