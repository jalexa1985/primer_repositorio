import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas de unidad compartidas (creacion colectiva - wiki).
 * IMPORTANTE: reemplacen "XxYy" por sus iniciales
 * (primeros apellidos + primera letra del segundo, en orden alfabetico).
 *
 * @author (sus nombres)
 * @version Ciclo 2 - 2026-2
 */
public class SlotMachineCC2Test {

    @Test
    public void accordingXxYyShouldKeepVisibleSymbolWhenAddingSymbols() {
        SlotMachine machine = new SlotMachine();
        machine.addSymbol(1, "red");
        machine.addWheel(1);
        machine.addSymbol(1, "blue");
        assertTrue(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }

    @Test
    public void accordingXxYyShouldNotBeJackpotAfterSwappingDifferentWheels() {
        SlotMachine machine = new SlotMachine();
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.spin(new String[]{"red", "blue"});
        machine.swap(1, 2);
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"blue", "red"}, machine.configuration());
        assertFalse(machine.isJackpot());
    }
}
