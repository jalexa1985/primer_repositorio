import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests of SlotMachine (cycles 1 and 2), in invisible mode.
 * Each operation is tested with two questions: what should it do?
 * what should it not do?
 *
 * @author slotMachine team
 * @version 2.0 (cycle 2)
 */
public class SlotMachineC2Test {

    private SlotMachine machine;

    /**
     * Create a machine with 3 wheels and the symbols red, blue and gold.
     */
    @Before
    public void setUp() {
        machine = new SlotMachine();
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "gold");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
    }

    // ------------------------------------------------------------- cycle 1

    @Test
    public void shouldCreateAnEmptyMachine() {
        SlotMachine empty = new SlotMachine();
        assertTrue(empty.ok());
        assertArrayEquals(new String[0], empty.symbols());
        assertArrayEquals(new String[0], empty.configuration());
        assertEquals(0, empty.distinctSymbols());
        assertFalse(empty.isJackpot());
    }

    @Test
    public void shouldAddWheelsShowingTheFirstSymbol() {
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "red", "red"}, machine.configuration());
    }

    @Test
    public void shouldAddAWheelInAPosition() {
        machine.placeSymbol(1, "blue");
        machine.addWheel(1);
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "blue", "red", "red"}, machine.configuration());
    }

    @Test
    public void shouldAdjustTheWheelPositionToTheValidRange() {
        machine.placeSymbol(1, "blue");
        machine.addWheel(100);
        machine.addWheel(-5);
        assertTrue(machine.ok());
        assertEquals(5, machine.configuration().length);
        assertEquals("blue", machine.configuration()[1]);
    }

    @Test
    public void shouldDeleteAWheel() {
        machine.placeSymbol(2, "gold");
        machine.delWheel(2);
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "red"}, machine.configuration());
    }

    @Test
    public void shouldNotDeleteAWheelOfAMachineWithoutWheels() {
        SlotMachine empty = new SlotMachine();
        empty.delWheel(1);
        assertFalse(empty.ok());
    }

    @Test
    public void shouldAddSymbolsInOrder() {
        machine.addSymbol(2, "Green");
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "green", "blue", "gold"}, machine.symbols());
    }

    @Test
    public void shouldNotChangeTheVisibleSymbolsWhenAddingASymbol() {
        machine.placeSymbol(3, "blue");
        machine.addSymbol(1, "green");
        assertArrayEquals(new String[]{"red", "red", "blue"}, machine.configuration());
    }

    @Test
    public void shouldNotAddARepeatedSymbol() {
        machine.addSymbol(1, "RED");
        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    @Test
    public void shouldNotAddASymbolWithTheSameColorOfAnother() {
        machine.addSymbol(1, "aqua");
        machine.addSymbol(1, "cyan");
        assertFalse(machine.ok());
        assertEquals(4, machine.symbols().length);
    }

    @Test
    public void shouldNotAddASymbolThatIsNotACssColor() {
        machine.addSymbol(1, "rojito");
        assertFalse(machine.ok());
        machine.addSymbol(1, null);
        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    @Test
    public void shouldDeleteASymbolShowingTheNextOne() {
        machine.delSymbol("red");
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"blue", "gold"}, machine.symbols());
        assertArrayEquals(new String[]{"blue", "blue", "blue"}, machine.configuration());
    }

    @Test
    public void shouldNotDeleteASymbolThatIsNotInTheMachine() {
        machine.delSymbol("green");
        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    @Test
    public void shouldPlaceASymbolInAWheel() {
        machine.placeSymbol(2, "gold");
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "gold", "red"}, machine.configuration());
        assertEquals(2, machine.distinctSymbols());
    }

    @Test
    public void shouldNotPlaceASymbolThatIsNotInTheMachine() {
        machine.placeSymbol(2, "green");
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red", "red", "red"}, machine.configuration());
    }

    @Test
    public void shouldSpinAWheelShowingASymbolOfTheMachine() {
        machine.spin(1);
        assertTrue(machine.ok());
        String symbol = machine.configuration()[0];
        assertTrue(symbol.equals("red") || symbol.equals("blue") || symbol.equals("gold"));
    }

    @Test
    public void shouldNotSpinWithoutSymbols() {
        SlotMachine empty = new SlotMachine();
        empty.addWheel(1);
        empty.spin();
        assertFalse(empty.ok());
        empty.spin(1);
        assertFalse(empty.ok());
    }

    @Test
    public void shouldSpinAllTheWheels() {
        machine.spin();
        assertTrue(machine.ok());
        assertEquals(3, machine.configuration().length);
        assertTrue(machine.distinctSymbols() >= 1 && machine.distinctSymbols() <= 3);
    }

    @Test
    public void shouldKnowTheJackpot() {
        assertTrue(machine.isJackpot());
        machine.placeSymbol(3, "blue");
        assertFalse(machine.isJackpot());
    }

    @Test
    public void shouldNotBeJackpotWithoutWheels() {
        SlotMachine noWheels = new SlotMachine();
        noWheels.addSymbol(1, "red");
        assertFalse(noWheels.isJackpot());
    }

    @Test
    public void shouldWorkInvisibleAndVisibleStates() {
        machine.makeInvisible();
        assertTrue(machine.ok());
        machine.spin();
        assertTrue(machine.ok());
    }

    @Test
    public void shouldNotDoAnythingAfterExit() {
        machine.exit();
        assertTrue(machine.ok());
        machine.addWheel(1);
        assertFalse(machine.ok());
        machine.addSymbol(1, "green");
        assertFalse(machine.ok());
        machine.spin();
        assertFalse(machine.ok());
        machine.makeVisible();
        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    // ------------------------------------------------------------- cycle 2

    @Test
    public void shouldSwapTwoWheels() {
        machine.placeSymbol(1, "blue");
        machine.placeSymbol(3, "gold");
        machine.swap(1, 3);
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"gold", "red", "blue"}, machine.configuration());
    }

    @Test
    public void shouldNotSwapWithoutWheels() {
        SlotMachine empty = new SlotMachine();
        empty.swap(1, 2);
        assertFalse(empty.ok());
    }

    @Test
    public void shouldKeepTheLockWhenSwapping() {
        machine.lock(1);
        machine.swap(1, 2);
        machine.spin(2, 1);
        assertFalse(machine.ok());
        machine.spin(1, 1);
        assertTrue(machine.ok());
    }

    @Test
    public void shouldNotRotateALockedWheel() {
        machine.lock(2);
        assertTrue(machine.ok());
        machine.spin(2);
        assertFalse(machine.ok());
        machine.spin(2, 2);
        assertFalse(machine.ok());
        machine.placeSymbol(2, "gold");
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[1]);
    }

    @Test
    public void shouldNotMoveLockedWheelsWhenSpinningAll() {
        machine.placeSymbol(2, "gold");
        machine.lock(2);
        for (int i = 0; i < 10; i++) {
            machine.spin();
            assertTrue(machine.ok());
            assertEquals("gold", machine.configuration()[1]);
        }
    }

    @Test
    public void shouldNotSpinAllWhenAllTheWheelsAreLocked() {
        machine.lock(1);
        machine.lock(2);
        machine.lock(3);
        machine.spin();
        assertFalse(machine.ok());
    }

    @Test
    public void shouldNotDeleteALockedWheel() {
        machine.lock(1);
        machine.delWheel(1);
        assertFalse(machine.ok());
        assertEquals(3, machine.configuration().length);
    }

    @Test
    public void shouldNotDeleteASymbolVisibleOnALockedWheel() {
        machine.lock(1);
        machine.delSymbol("red");
        assertFalse(machine.ok());
        machine.delSymbol("gold");
        assertTrue(machine.ok());
    }

    @Test
    public void shouldUnlockAWheel() {
        machine.lock(1);
        machine.unlock(1);
        assertTrue(machine.ok());
        machine.spin(1, 1);
        assertTrue(machine.ok());
        machine.delWheel(1);
        assertTrue(machine.ok());
    }

    @Test
    public void shouldNotLockTwiceNorUnlockAnUnlockedWheel() {
        machine.lock(1);
        machine.lock(1);
        assertFalse(machine.ok());
        machine.unlock(2);
        assertFalse(machine.ok());
    }

    @Test
    public void shouldRotateAWheelForward() {
        machine.spin(1, 1);
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);
        machine.spin(1, 4);
        assertEquals("gold", machine.configuration()[0]);
    }

    @Test
    public void shouldRotateAWheelBackward() {
        machine.spin(1, -1);
        assertTrue(machine.ok());
        assertEquals("gold", machine.configuration()[0]);
        machine.spin(1, -5);
        assertEquals("red", machine.configuration()[0]);
    }

    @Test
    public void shouldNotChangeAnythingRotatingZeroSteps() {
        machine.spin(2, 0);
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "red", "red"}, machine.configuration());
    }

    @Test
    public void shouldSetAConfiguration() {
        machine.spin(new String[]{"gold", "BLUE", "red"});
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"gold", "blue", "red"}, machine.configuration());
        machine.spin(new String[]{"gold", "gold", "gold"});
        assertTrue(machine.isJackpot());
    }

    @Test
    public void shouldNotSetAConfigurationWithAWrongSize() {
        machine.spin(new String[]{"gold", "blue"});
        assertFalse(machine.ok());
        machine.spin((String[]) null);
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red", "red", "red"}, machine.configuration());
    }

    @Test
    public void shouldNotChangeAnythingWithAnInvalidConfiguration() {
        machine.spin(new String[]{"gold", "blue", "green"});
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red", "red", "red"}, machine.configuration());
    }

    @Test
    public void shouldNotChangeALockedWheelWithAConfiguration() {
        machine.lock(2);
        machine.spin(new String[]{"gold", "blue", "gold"});
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red", "red", "red"}, machine.configuration());
        machine.spin(new String[]{"gold", "red", "gold"});
        assertTrue(machine.ok());
    }
}
