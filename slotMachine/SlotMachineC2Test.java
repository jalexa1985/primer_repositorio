import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests of the slot machine simulator (cycles 1 and 2).
 * All the tests work in invisible mode. Each requirement is tested with
 * two questions: what should it do? and what should it not do?
 *
 * @author slotMachine team
 * @version 2.0 (cycle 2)
 */
public class SlotMachineC2Test {

    private SlotMachine machine;

    /**
     * Create a machine with 3 wheels and symbols red, green, blue.
     */
    @Before
    public void setUp() {
        machine = new SlotMachine();
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "green");
        machine.addSymbol(3, "blue");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
    }

    // ---------------------------------------------------------------- create

    @Test
    public void shouldCreateAnEmptyMachine() {
        SlotMachine empty = new SlotMachine();
        assertTrue(empty.ok());
        assertEquals(0, empty.symbols().length);
        assertEquals(0, empty.configuration().length);
        assertFalse(empty.isJackpot());
    }

    // ---------------------------------------------------------------- wheels

    @Test
    public void shouldAddWheelsShowingTheFirstSymbol() {
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"red", "red", "red"}, machine.configuration());
    }

    @Test
    public void shouldAddWheelInTheGivenPosition() {
        machine.placeSymbol(1, "blue");
        machine.addWheel(1);
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"red", "blue", "red", "red"}, machine.configuration());
    }

    @Test
    public void shouldAdjustWheelPositionsOutOfRange() {
        machine.placeSymbol(3, "green");
        machine.addWheel(100);
        machine.placeSymbol(100, "blue");
        machine.placeSymbol(-5, "green");
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"green", "red", "green", "blue"}, machine.configuration());
    }

    @Test
    public void shouldDeleteAWheel() {
        machine.placeSymbol(2, "blue");
        machine.delWheel(2);
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"red", "red"}, machine.configuration());
    }

    @Test
    public void shouldNotDeleteAWheelOfAnEmptyMachine() {
        SlotMachine empty = new SlotMachine();
        empty.delWheel(1);
        assertFalse(empty.ok());
    }

    @Test
    public void shouldNotDeleteALockedWheel() {
        machine.lock(2);
        machine.delWheel(2);
        assertFalse(machine.ok());
        assertEquals(3, machine.configuration().length);
    }

    @Test
    public void shouldSwapTwoWheels() {
        machine.placeSymbol(1, "green");
        machine.placeSymbol(3, "blue");
        machine.swap(1, 3);
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"blue", "red", "green"}, machine.configuration());
    }

    @Test
    public void shouldNotSwapWheelsOfAnEmptyMachine() {
        SlotMachine empty = new SlotMachine();
        empty.swap(1, 2);
        assertFalse(empty.ok());
    }

    @Test
    public void shouldLockAndUnlockAWheel() {
        machine.lock(1);
        assertTrue(machine.ok());
        machine.spin(1, 1);
        assertFalse(machine.ok());
        machine.unlock(1);
        assertTrue(machine.ok());
        machine.spin(1, 1);
        assertTrue(machine.ok());
        assertEquals("green", machine.configuration()[0]);
    }

    @Test
    public void shouldNotLockALockedWheelNorUnlockAnUnlockedOne() {
        machine.lock(2);
        machine.lock(2);
        assertFalse(machine.ok());
        machine.unlock(3);
        assertFalse(machine.ok());
    }

    // ---------------------------------------------------------------- symbols

    @Test
    public void shouldAddSymbolsInTheGivenPosition() {
        machine.addSymbol(1, "Yellow");
        machine.addSymbol(99, "purple");
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"yellow", "red", "green", "blue", "purple"},
                          machine.symbols());
    }

    @Test
    public void shouldNotChangeTheVisibleSymbolWhenAddingASymbol() {
        machine.placeSymbol(2, "green");
        machine.addSymbol(1, "yellow");
        assertArrayEquals(new String[] {"red", "green", "red"}, machine.configuration());
    }

    @Test
    public void shouldNotAddASymbolThatIsNotACssColor() {
        machine.addSymbol(1, "rojo");
        assertFalse(machine.ok());
        machine.addSymbol(1, null);
        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    @Test
    public void shouldNotAddSymbolsWithTheSameColor() {
        machine.addSymbol(1, "RED");
        assertFalse(machine.ok());
        machine.addSymbol(1, "cyan");
        machine.addSymbol(1, "aqua");
        assertFalse(machine.ok());
        assertEquals(4, machine.symbols().length);
    }

    @Test
    public void shouldDeleteASymbolFromAllWheels() {
        machine.placeSymbol(2, "green");
        machine.delSymbol("green");
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"red", "blue"}, machine.symbols());
        assertArrayEquals(new String[] {"red", "blue", "red"}, machine.configuration());
    }

    @Test
    public void shouldNotDeleteASymbolThatDoesNotExist() {
        machine.delSymbol("black");
        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    @Test
    public void shouldNotDeleteASymbolVisibleOnALockedWheel() {
        machine.lock(1);
        machine.delSymbol("red");
        assertFalse(machine.ok());
        machine.delSymbol("blue");
        assertTrue(machine.ok());
    }

    // --------------------------------------------------------------- spinning

    @Test
    public void shouldPlaceASymbol() {
        machine.placeSymbol(2, "blue");
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[1]);
    }

    @Test
    public void shouldNotPlaceASymbolThatDoesNotExist() {
        machine.placeSymbol(2, "white");
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[1]);
    }

    @Test
    public void shouldSpinAWheelToAnExistingSymbol() {
        machine.spin(2);
        assertTrue(machine.ok());
        String visible = machine.configuration()[1];
        assertTrue(visible.equals("red") || visible.equals("green") || visible.equals("blue"));
    }

    @Test
    public void shouldNotSpinWithoutSymbols() {
        SlotMachine noSymbols = new SlotMachine();
        noSymbols.addWheel(1);
        noSymbols.spin(1);
        assertFalse(noSymbols.ok());
        noSymbols.spin();
        assertFalse(noSymbols.ok());
    }

    @Test
    public void shouldSpinAWheelForwardAndBackward() {
        machine.spin(1, 1);
        machine.spin(2, 5);
        machine.spin(3, -1);
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"green", "blue", "blue"}, machine.configuration());
    }

    @Test
    public void shouldNotSpinALockedWheel() {
        machine.lock(3);
        machine.spin(3, 2);
        assertFalse(machine.ok());
        machine.spin(3);
        assertFalse(machine.ok());
        machine.placeSymbol(3, "blue");
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[2]);
    }

    @Test
    public void shouldNotMoveLockedWheelsWhenSpinningAll() {
        machine.placeSymbol(2, "blue");
        machine.lock(2);
        for (int i = 0; i < 10; i++) {
            machine.spin();
            assertTrue(machine.ok());
            assertEquals("blue", machine.configuration()[1]);
        }
    }

    @Test
    public void shouldSetAConfiguration() {
        machine.spin(new String[] {"blue", "green", "red"});
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"blue", "green", "red"}, machine.configuration());
    }

    @Test
    public void shouldNotSetAnInvalidConfiguration() {
        machine.spin(new String[] {"blue", "green"});
        assertFalse(machine.ok());
        machine.spin(new String[] {"blue", "green", "pink"});
        assertFalse(machine.ok());
        machine.spin((String[]) null);
        assertFalse(machine.ok());
        assertArrayEquals(new String[] {"red", "red", "red"}, machine.configuration());
    }

    @Test
    public void shouldNotChangeALockedWheelWhenSettingAConfiguration() {
        machine.lock(1);
        machine.spin(new String[] {"blue", "blue", "blue"});
        assertFalse(machine.ok());
        assertArrayEquals(new String[] {"red", "red", "red"}, machine.configuration());
        machine.spin(new String[] {"red", "blue", "green"});
        assertTrue(machine.ok());
    }

    // ---------------------------------------------------------------- queries

    @Test
    public void shouldCountDistinctVisibleSymbols() {
        assertEquals(1, machine.distinctSymbols());
        machine.placeSymbol(1, "green");
        assertEquals(2, machine.distinctSymbols());
        machine.placeSymbol(2, "blue");
        assertEquals(3, machine.distinctSymbols());
    }

    @Test
    public void shouldBeJackpotOnlyWhenAllWheelsShowTheSameSymbol() {
        assertTrue(machine.isJackpot());
        machine.spin(2, 1);
        assertFalse(machine.isJackpot());
        machine.spin(new String[] {"blue", "blue", "blue"});
        assertTrue(machine.isJackpot());
    }

    @Test
    public void shouldNotBeJackpotWithoutWheelsOrSymbols() {
        SlotMachine empty = new SlotMachine();
        assertFalse(empty.isJackpot());
        empty.addWheel(1);
        assertFalse(empty.isJackpot());
    }

    @Test
    public void shouldNotChangeTheMachineWhenQuerying() {
        String[] symbols = machine.symbols();
        symbols[0] = "black";
        assertEquals("red", machine.symbols()[0]);
    }

    // -------------------------------------------------------------- simulator

    @Test
    public void shouldWorkInvisible() {
        machine.makeInvisible();
        assertTrue(machine.ok());
        machine.spin();
        assertTrue(machine.ok());
    }

    @Test
    public void shouldNotWorkAfterExit() {
        machine.exit();
        assertTrue(machine.ok());
        machine.addWheel(1);
        assertFalse(machine.ok());
        machine.spin();
        assertFalse(machine.ok());
    }
}
