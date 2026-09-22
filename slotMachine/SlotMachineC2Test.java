import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad del simulador SlotMachine (ciclos 1 y 2).
 * Todas las pruebas se ejecutan en modo invisible.
 *
 * @author (sus nombres)
 * @version Ciclo 2 - 2026-2
 */
public class SlotMachineC2Test {
    private SlotMachine machine;

    /**
     * Crea una maquina con 3 ruedas y los simbolos red, blue, green.
     */
    @Before
    public void setUp() {
        machine = new SlotMachine();
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
    }

    // ---------------- ciclo 1 ----------------

    @Test
    public void shouldCreateEmptyMachine() {
        SlotMachine empty = new SlotMachine();
        assertEquals(0, empty.configuration().length);
        assertEquals(0, empty.symbols().length);
        assertFalse(empty.isJackpot());
    }

    @Test
    public void shouldAddWheelsWithAllSymbols() {
        machine.addWheel(10);
        assertTrue(machine.ok());
        assertEquals(4, machine.configuration().length);
        assertEquals("red", machine.configuration()[3]);
    }

    @Test
    public void shouldDeleteWheel() {
        machine.delWheel(-5);
        assertTrue(machine.ok());
        assertEquals(2, machine.configuration().length);
    }

    @Test
    public void shouldNotDeleteWheelFromEmptyMachine() {
        SlotMachine empty = new SlotMachine();
        empty.delWheel(1);
        assertFalse(empty.ok());
    }

    @Test
    public void shouldAddSymbolsInOrder() {
        machine.addSymbol(2, "yellow");
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "yellow", "blue", "green"}, machine.symbols());
        machine.addSymbol(0, "Purple");
        assertEquals("purple", machine.symbols()[0]);
    }

    @Test
    public void shouldNotAddRepeatedOrInvalidSymbols() {
        machine.addSymbol(1, "red");
        assertFalse(machine.ok());
        machine.addSymbol(1, "notAColor");
        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    @Test
    public void shouldDeleteSymbol() {
        machine.delSymbol("red");
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"blue", "green"}, machine.symbols());
        assertArrayEquals(new String[]{"blue", "blue", "blue"}, machine.configuration());
    }

    @Test
    public void shouldNotDeleteUnknownSymbol() {
        machine.delSymbol("pink");
        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    @Test
    public void shouldPlaceSymbol() {
        machine.placeSymbol(2, "green");
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "green", "red"}, machine.configuration());
        assertEquals(2, machine.distinctSymbols());
    }

    @Test
    public void shouldNotPlaceUnknownSymbol() {
        machine.placeSymbol(1, "pink");
        assertFalse(machine.ok());
    }

    @Test
    public void shouldSpinKeepingValidSymbols() {
        machine.spin();
        assertTrue(machine.ok());
        for (String s : machine.configuration()) {
            assertTrue(s.equals("red") || s.equals("blue") || s.equals("green"));
        }
        machine.spin(1);
        assertTrue(machine.ok());
    }

    @Test
    public void shouldNotSpinWithoutSymbols() {
        SlotMachine m = new SlotMachine();
        m.addWheel(1);
        m.spin();
        assertFalse(m.ok());
    }

    @Test
    public void shouldDetectJackpot() {
        assertTrue(machine.isJackpot());
        machine.placeSymbol(3, "blue");
        assertFalse(machine.isJackpot());
    }

    @Test
    public void shouldWorkInvisibleAndExit() {
        machine.makeInvisible();
        assertTrue(machine.ok());
        machine.exit();
        assertTrue(machine.ok());
    }

    // ---------------- ciclo 2 ----------------

    @Test
    public void shouldSwapWheels() {
        machine.placeSymbol(1, "green");
        machine.swap(1, 3);
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "red", "green"}, machine.configuration());
    }

    @Test
    public void shouldNotSwapWithoutWheels() {
        SlotMachine empty = new SlotMachine();
        empty.swap(1, 2);
        assertFalse(empty.ok());
    }

    @Test
    public void shouldLockAndUnlockWheel() {
        machine.lock(2);
        assertTrue(machine.ok());
        machine.placeSymbol(2, "blue");
        assertFalse(machine.ok());
        machine.spin(2, 1);
        assertFalse(machine.ok());
        machine.unlock(2);
        assertTrue(machine.ok());
        machine.placeSymbol(2, "blue");
        assertTrue(machine.ok());
    }

    @Test
    public void shouldNotLockTwiceNorUnlockUnlocked() {
        machine.lock(1);
        machine.lock(1);
        assertFalse(machine.ok());
        machine.unlock(3);
        assertFalse(machine.ok());
    }

    @Test
    public void shouldNotMoveLockedWheelsWhenSpinningAll() {
        machine.placeSymbol(1, "green");
        machine.lock(1);
        for (int i = 0; i < 20; i++) {
            machine.spin();
            assertEquals("green", machine.configuration()[0]);
        }
    }

    @Test
    public void shouldRotateWheelSteps() {
        machine.spin(1, 1);
        assertEquals("blue", machine.configuration()[0]);
        machine.spin(1, 2);
        assertEquals("red", machine.configuration()[0]);
        machine.spin(1, 5);
        assertEquals("green", machine.configuration()[0]);
    }

    @Test
    public void shouldNotRotateNegativeSteps() {
        machine.spin(1, -1);
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }

    @Test
    public void shouldSetConfiguration() {
        machine.spin(new String[]{"green", "blue", "red"});
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"green", "blue", "red"}, machine.configuration());
        assertEquals(3, machine.distinctSymbols());
    }

    @Test
    public void shouldNotSetInvalidConfiguration() {
        machine.spin(new String[]{"green", "blue"});
        assertFalse(machine.ok());
        machine.spin(new String[]{"green", "blue", "pink"});
        assertFalse(machine.ok());
        machine.lock(1);
        machine.spin(new String[]{"green", "blue", "red"});
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red", "red", "red"}, machine.configuration());
    }
}
