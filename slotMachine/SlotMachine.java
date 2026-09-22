import shapes.Canvas;
import shapes.Circle;
import shapes.CssColors;
import shapes.Rectangle;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Simulator of a slot machine, inspired by the Problem I "Slot Machine" of
 * the ICPC World Finals 2025.
 *
 * The machine has wheels and symbols. The symbols are identified by their
 * CSS color names and all of them must have different colors. Every wheel
 * has all the symbols of the machine, in the same order; the visible symbol
 * of each wheel depends on how much it has rotated. The machine is a winner
 * (jackpot) when all the wheels show the same symbol.
 *
 * Positions of wheels and symbols start at 1. If a position is less than 1,
 * position 1 is used; if it is greater than the maximum, the maximum is used.
 * {@link #ok()} tells if the last operation could be done. If an operation
 * can not be done and the simulator is visible, a message is shown.
 *
 * @author slotMachine team
 * @version 2.0 (cycle 2)
 */
public class SlotMachine {

    private static final int LEFT = 40;
    private static final int TOP = 40;
    private static final int WHEELS_TOP = 130;
    private static final int WHEEL_HEIGHT = 260;
    private static final int MAX_SLOT = 90;
    private static final int WHEELS_AREA = 740;
    private static final int STEP_DELAY = 120;

    private List<Wheel> wheels;
    private List<String> symbols;
    private boolean isVisible;
    private boolean ok;
    private boolean finished;
    private boolean jackpotShown;
    private Random random;

    private Rectangle body;
    private Rectangle header;
    private Circle leftLight;
    private Circle rightLight;
    private Rectangle leverBar;
    private Circle leverKnob;

    /**
     * Create an empty slot machine (without wheels nor symbols), invisible.
     */
    public SlotMachine() {
        wheels = new ArrayList<Wheel>();
        symbols = new ArrayList<String>();
        random = new Random();
        isVisible = false;
        finished = false;
        body = new Rectangle();
        header = new Rectangle();
        leftLight = new Circle();
        rightLight = new Circle();
        leverBar = new Rectangle();
        leverKnob = new Circle();
        ok = true;
    }

    // ------------------------------------------------------------------ wheels

    /**
     * Add a new wheel in a position. The new wheel has all the symbols of
     * the machine and shows the first one.
     * @param pos the position of the new wheel (1..wheels+1)
     */
    public void addWheel(int pos) {
        if (!available()) {
            return;
        }
        wheels.add(clamp(pos, wheels.size() + 1) - 1, new Wheel(symbols));
        redraw();
        ok = true;
    }

    /**
     * Delete the wheel of a position. A locked wheel can not be deleted.
     * @param pos the position of the wheel (1..wheels)
     */
    public void delWheel(int pos) {
        if (!available() || !hasWheels()) {
            return;
        }
        int index = clamp(pos, wheels.size()) - 1;
        if (wheels.get(index).isLocked()) {
            fail("La rueda " + (index + 1) + " está fijada, no se puede eliminar.");
            return;
        }
        Wheel wheel = wheels.remove(index);
        wheel.makeInvisible();
        redraw();
        ok = true;
    }

    /**
     * Swap the positions of two wheels.
     * @param wheel1 the position of the first wheel
     * @param wheel2 the position of the second wheel
     */
    public void swap(int wheel1, int wheel2) {
        if (!available() || !hasWheels()) {
            return;
        }
        int first = clamp(wheel1, wheels.size()) - 1;
        int second = clamp(wheel2, wheels.size()) - 1;
        Wheel wheel = wheels.get(first);
        wheels.set(first, wheels.get(second));
        wheels.set(second, wheel);
        redraw();
        ok = true;
    }

    /**
     * Lock a wheel, so it can not rotate.
     * @param wheel the position of the wheel
     */
    public void lock(int wheel) {
        setLocked(wheel, true);
    }

    /**
     * Unlock a wheel, so it can rotate again.
     * @param wheel the position of the wheel
     */
    public void unlock(int wheel) {
        setLocked(wheel, false);
    }

    // ----------------------------------------------------------------- symbols

    /**
     * Add a symbol to the machine (to every wheel) in a position.
     * @param pos the position of the new symbol (1..symbols+1)
     * @param color the CSS color name of the symbol; it must be different
     * from the colors of the other symbols
     */
    public void addSymbol(int pos, String color) {
        if (!available()) {
            return;
        }
        if (color == null || !CssColors.exists(color)) {
            fail("'" + color + "' no es un nombre de color CSS.");
            return;
        }
        String symbol = CssColors.normalize(color);
        if (sameColorSymbol(symbol) != null) {
            fail("Ya existe un símbolo del color '" + sameColorSymbol(symbol) + "'.");
            return;
        }
        int index = clamp(pos, symbols.size() + 1) - 1;
        symbols.add(index, symbol);
        for (Wheel wheel : wheels) {
            wheel.addSymbol(index, symbol);
        }
        refresh();
        ok = true;
    }

    /**
     * Delete a symbol from the machine (from every wheel). A symbol visible
     * on a locked wheel can not be deleted.
     * @param symbol the color of the symbol
     */
    public void delSymbol(String symbol) {
        if (!available() || !hasSymbol(symbol)) {
            return;
        }
        String color = CssColors.normalize(symbol);
        for (Wheel wheel : wheels) {
            if (wheel.isLocked() && color.equals(wheel.visibleSymbol())) {
                fail("El símbolo '" + color + "' está visible en una rueda fijada.");
                return;
            }
        }
        symbols.remove(color);
        for (Wheel wheel : wheels) {
            wheel.removeSymbol(color);
        }
        refresh();
        ok = true;
    }

    // ----------------------------------------------------------------- spinning

    /**
     * Rotate a wheel so it shows a symbol.
     * @param wheel the position of the wheel
     * @param symbol the color of the symbol
     */
    public void placeSymbol(int wheel, String symbol) {
        Wheel target = movableWheel(wheel);
        if (target == null || !hasSymbol(symbol)) {
            return;
        }
        target.place(CssColors.normalize(symbol));
        refresh();
        ok = true;
    }

    /**
     * Spin a wheel a random number of steps.
     * @param wheel the position of the wheel
     */
    public void spin(int wheel) {
        Wheel target = movableWheel(wheel);
        if (target == null) {
            return;
        }
        int[] steps = new int[wheels.size()];
        steps[wheels.indexOf(target)] = random.nextInt(symbols.size() * 2) + 1;
        rotate(steps);
        ok = true;
    }

    /**
     * Rotate a wheel a number of steps. Positive steps move forward (the next
     * symbol becomes visible) and negative steps move backward. If the
     * simulator is visible, the movement is shown step by step.
     * @param wheel the position of the wheel
     * @param steps the number of steps
     */
    public void spin(int wheel, int steps) {
        Wheel target = movableWheel(wheel);
        if (target == null) {
            return;
        }
        int[] allSteps = new int[wheels.size()];
        allSteps[wheels.indexOf(target)] = steps;
        rotate(allSteps);
        ok = true;
    }

    /**
     * Leave the machine in a given configuration: each wheel shows the
     * corresponding symbol. Locked wheels must already show their symbol.
     * If the configuration is not valid, nothing changes.
     * @param setSymbols the colors of the symbols to show, from left to right
     */
    public void spin(String[] setSymbols) {
        if (!available() || !hasWheels() || !hasSymbols()) {
            return;
        }
        if (setSymbols == null || setSymbols.length != wheels.size()) {
            fail("La configuración debe tener un símbolo por cada una de las "
                 + wheels.size() + " ruedas.");
            return;
        }
        String[] colors = validConfiguration(setSymbols);
        if (colors == null) {
            return;
        }
        for (int i = 0; i < colors.length; i++) {
            wheels.get(i).place(colors[i]);
        }
        refresh();
        ok = true;
    }

    /**
     * Spin all the wheels that are not locked a random number of steps.
     */
    public void spin() {
        if (!available() || !hasWheels() || !hasSymbols()) {
            return;
        }
        int[] steps = new int[wheels.size()];
        for (int i = 0; i < steps.length; i++) {
            if (!wheels.get(i).isLocked()) {
                steps[i] = random.nextInt(symbols.size() * 2) + 1;
            }
        }
        rotate(steps);
        ok = true;
    }

    // ------------------------------------------------------------------ queries

    /**
     * Return the symbols in the order they are on the wheels, starting at 1.
     * @return the colors of the symbols
     */
    public String[] symbols() {
        ok = true;
        return symbols.toArray(new String[0]);
    }

    /**
     * Return the number of different symbols visible in the machine.
     * @return the number of different visible symbols
     */
    public int distinctSymbols() {
        ok = true;
        String[] visible = configuration();
        HashSet<String> different = new HashSet<String>();
        for (String symbol : visible) {
            different.add(symbol);
        }
        return different.size();
    }

    /**
     * Return the visible symbols of all the wheels, from left to right.
     * @return the colors of the visible symbols (empty if the machine has no
     * symbols)
     */
    public String[] configuration() {
        ok = true;
        if (symbols.isEmpty()) {
            return new String[0];
        }
        String[] visible = new String[wheels.size()];
        for (int i = 0; i < visible.length; i++) {
            visible[i] = wheels.get(i).visibleSymbol();
        }
        return visible;
    }

    /**
     * Tell if the machine is in a winner configuration: it has wheels and
     * all of them show the same symbol.
     * @return true if it is a jackpot
     */
    public boolean isJackpot() {
        boolean jackpot = !wheels.isEmpty() && !symbols.isEmpty() && distinctSymbols() == 1;
        ok = true;
        return jackpot;
    }

    // --------------------------------------------------------------- simulator

    /**
     * Make the simulator visible.
     */
    public void makeVisible() {
        if (!available()) {
            return;
        }
        isVisible = true;
        redraw();
        ok = true;
    }

    /**
     * Make the simulator invisible. It keeps working.
     */
    public void makeInvisible() {
        hideAll();
        isVisible = false;
        ok = true;
    }

    /**
     * Finish the simulator: it becomes invisible and no more operations
     * can be done.
     */
    public void exit() {
        makeInvisible();
        finished = true;
        ok = true;
    }

    /**
     * Tell if the last operation could be done.
     * @return true if the last operation was done
     */
    public boolean ok() {
        return ok;
    }

    // ---------------------------------------------------------- private logic

    /*
     * Lock or unlock a wheel.
     */
    private void setLocked(int wheel, boolean lock) {
        if (!available() || !hasWheels()) {
            return;
        }
        int index = clamp(wheel, wheels.size()) - 1;
        Wheel target = wheels.get(index);
        if (target.isLocked() == lock) {
            fail("La rueda " + (index + 1) + (lock ? " ya está fijada." : " no está fijada."));
            return;
        }
        if (lock) {
            target.lock();
        } else {
            target.unlock();
        }
        ok = true;
    }

    /*
     * Return the wheel of a position if it can rotate; otherwise, fail and
     * return null.
     */
    private Wheel movableWheel(int wheel) {
        if (!available() || !hasWheels() || !hasSymbols()) {
            return null;
        }
        int index = clamp(wheel, wheels.size()) - 1;
        if (wheels.get(index).isLocked()) {
            fail("La rueda " + (index + 1) + " está fijada, no puede girar.");
            return null;
        }
        return wheels.get(index);
    }

    /*
     * Validate a configuration. Return the normalized colors, or null (after
     * failing) if it is not valid.
     */
    private String[] validConfiguration(String[] setSymbols) {
        String[] colors = new String[setSymbols.length];
        for (int i = 0; i < setSymbols.length; i++) {
            if (!hasSymbol(setSymbols[i])) {
                return null;
            }
            colors[i] = CssColors.normalize(setSymbols[i]);
            Wheel wheel = wheels.get(i);
            if (wheel.isLocked() && !colors[i].equals(wheel.visibleSymbol())) {
                fail("La rueda " + (i + 1) + " está fijada y muestra '"
                     + wheel.visibleSymbol() + "'.");
                return null;
            }
        }
        return colors;
    }

    /*
     * Rotate the wheels the given steps (one value for each wheel). If the
     * simulator is visible, the movement is shown step by step.
     */
    private void rotate(int[] steps) {
        int max = 0;
        for (int s : steps) {
            max = Math.max(max, Math.abs(s));
        }
        int total = isVisible ? max : 1;
        for (int step = 0; step < total; step++) {
            for (int i = 0; i < steps.length; i++) {
                if (!isVisible) {
                    wheels.get(i).rotate(steps[i]);
                } else if (step < Math.abs(steps[i])) {
                    wheels.get(i).rotate(Integer.signum(steps[i]));
                }
            }
            if (isVisible) {
                Canvas.getCanvas().wait(STEP_DELAY);
            }
        }
        refresh();
    }

    /*
     * Tell if the simulator can work; fail if it was finished.
     */
    private boolean available() {
        if (finished) {
            fail("El simulador ya terminó.");
        }
        return !finished;
    }

    /*
     * Tell if the machine has wheels; fail if not.
     */
    private boolean hasWheels() {
        if (wheels.isEmpty()) {
            fail("La máquina no tiene ruedas.");
        }
        return !wheels.isEmpty();
    }

    /*
     * Tell if the machine has symbols; fail if not.
     */
    private boolean hasSymbols() {
        if (symbols.isEmpty()) {
            fail("La máquina no tiene símbolos.");
        }
        return !symbols.isEmpty();
    }

    /*
     * Tell if the machine has a symbol; fail if not.
     */
    private boolean hasSymbol(String symbol) {
        boolean exists = symbol != null && symbols.contains(CssColors.normalize(symbol));
        if (!exists) {
            fail("La máquina no tiene el símbolo '" + symbol + "'.");
        }
        return exists;
    }

    /*
     * Return the symbol with the same color (RGB) of a color, or null.
     */
    private String sameColorSymbol(String color) {
        for (String symbol : symbols) {
            if (CssColors.get(symbol).equals(CssColors.get(color))) {
                return symbol;
            }
        }
        return null;
    }

    /*
     * Adjust a position (1 based) to the range 1..max.
     */
    private int clamp(int pos, int max) {
        return Math.max(1, Math.min(pos, max));
    }

    /*
     * Register that the last operation failed, telling the user if visible.
     */
    private void fail(String message) {
        ok = false;
        if (isVisible) {
            JOptionPane.showMessageDialog(null, message, "Slot Machine",
                                          JOptionPane.WARNING_MESSAGE);
        }
    }

    // ---------------------------------------------------------- private drawing

    /*
     * Redraw the machine if the winner state changed.
     */
    private void refresh() {
        if (isVisible && isJackpot() != jackpotShown) {
            redraw();
        }
    }

    /*
     * Draw again all the elements of the machine (if it is visible).
     */
    private void redraw() {
        if (!isVisible) {
            return;
        }
        hideAll();
        jackpotShown = isJackpot();
        layout();
        body.makeVisible();
        header.makeVisible();
        leftLight.makeVisible();
        rightLight.makeVisible();
        leverBar.makeVisible();
        leverKnob.makeVisible();
        for (Wheel wheel : wheels) {
            wheel.makeVisible();
        }
    }

    /*
     * Compute the position, size and color of all the elements.
     */
    private void layout() {
        int n = Math.max(1, wheels.size());
        int slot = Math.min(MAX_SLOT, WHEELS_AREA / n);
        int width = n * slot + 40;
        body.changeSize(WHEEL_HEIGHT + 130, width);
        body.moveTo(LEFT, TOP + 40);
        body.changeColor(jackpotShown ? "gold" : "firebrick");
        header.changeSize(40, width - 40);
        header.moveTo(LEFT + 20, TOP);
        header.changeColor(jackpotShown ? "orange" : "darkred");
        placeLights(width);
        leverBar.changeSize(120, 10);
        leverBar.moveTo(LEFT + width + 10, WHEELS_TOP);
        leverBar.changeColor("silver");
        leverKnob.changeSize(28);
        leverKnob.moveTo(LEFT + width + 1, WHEELS_TOP - 24);
        leverKnob.changeColor("red");
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).setBounds(LEFT + 20 + i * slot + 5, WHEELS_TOP, slot - 10, WHEEL_HEIGHT);
        }
    }

    /*
     * Place the lights of the header.
     */
    private void placeLights(int width) {
        String color = jackpotShown ? "lime" : "gray";
        leftLight.changeSize(24);
        leftLight.moveTo(LEFT + 30, TOP + 8);
        leftLight.changeColor(color);
        rightLight.changeSize(24);
        rightLight.moveTo(LEFT + width - 54, TOP + 8);
        rightLight.changeColor(color);
    }

    /*
     * Erase all the elements of the machine.
     */
    private void hideAll() {
        for (Wheel wheel : wheels) {
            wheel.makeInvisible();
        }
        body.makeInvisible();
        header.makeInvisible();
        leftLight.makeInvisible();
        rightLight.makeInvisible();
        leverBar.makeInvisible();
        leverKnob.makeInvisible();
    }
}
