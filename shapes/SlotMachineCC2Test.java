import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Shared unit tests of SlotMachine (collective creation, cycle 2), in
 * invisible mode.
 *
 * IMPORTANT: replace "XxYy" in the names of the tests with the initials of
 * the authors (first surnames plus the first letter of the second surname,
 * in alphabetical order). For example: accordingDcAvShould...
 *
 * @author slotMachine team
 * @version 2.0 (cycle 2)
 */
public class SlotMachineCC2Test {

    @Test
    public void accordingXxYyShouldBeJackpotAfterRotatingToTheSameSymbol() {
        SlotMachine machine = new SlotMachine();
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.spin(1, 1);
        assertFalse(machine.isJackpot());
        machine.spin(2, -1);
        assertTrue(machine.ok());
        assertTrue(machine.isJackpot());
        assertEquals(1, machine.distinctSymbols());
    }

    @Test
    public void accordingXxYyShouldNotMoveALockedWheelWithAnyKindOfSpin() {
        SlotMachine machine = new SlotMachine();
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "gold");
        machine.addWheel(1);
        machine.lock(1);
        machine.spin(1);
        assertFalse(machine.ok());
        machine.spin(1, 2);
        assertFalse(machine.ok());
        machine.spin(new String[]{"gold"});
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red"}, machine.configuration());
    }
}
