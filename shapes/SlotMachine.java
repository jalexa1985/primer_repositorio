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
 * CSS color names and all of them have different colors. Every wheel has
 * all the symbols of the machine, in the same order; each wheel shows one
 * of them. The machine is a winner (jackpot) when all the wheels show the
 * same symbol; then it looks golden with its lights on.
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
    private static final int TOP = 30;
    private static final int SLOT = 100;
    private static final int BODY_HEIGHT = 440;
    private static final int STEP_DELAY = 120;
    private static final int LEVER_PULL = 60;

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
    private int rightX;

    /**
     * Create an empty slot machine (without wheels nor symbols), invisible.
     */
    public SlotMachine() {
        wheels = new ArrayList<Wheel>();
        symbols = new ArrayList<String>();
        random = new Random();
        isVisible = false;
        finished = false;
        jackpotShown = false;
        rightX = LEFT + bodyWidth();
        body = Figures.rectangle(LEFT, TOP, bodyWidth(), BODY_HEIGHT, "silver");
        header = Figures.rectangle(LEFT, TOP, bodyWidth(), 70, "darkred");
        leftLight = Figures.circle(LEFT + 10, TOP + 15, 40, "gray");
        rightLight = Figures.circle(rightX - 50, TOP + 15, 40, "gray");
        leverBar = Figures.rectangle(rightX + 10, TOP + 130, 12, 160, "black");
        leverKnob = Figures.circle(rightX - 2, TOP + 100, 36, "red");
        ok = true;
    }

    // --------------------------------------------------------------- wheels

    /**
     * Add a new wheel in a position. The new wheel has all the symbols of
     * the machine and shows the first one.
     * @param pos the position of the new wheel (1..wheels+1)
     */
    public void addWheel(int pos) {
        if (!available()) {
            return;
        }
        int index = clamp(pos, wheels.size() + 1) - 1;
        wheels.add(index, new Wheel(symbols, wheelX(index), TOP + 90));
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
        wheels.remove(index).makeInvisible();
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
     * Lock a wheel, so it can not rotate nor be deleted.
     * @param wheel the position of the wheel
     */
    public void lock(int wheel) {
        setLocked(wheel, true);
    }

    /**
     * Unlock a locked wheel, so it can rotate again.
     * @param wheel the position of the wheel
     */
    public void unlock(int wheel) {
        setLocked(wheel, false);
    }

    // -------------------------------------------------------------- symbols

    /**
     * Add a symbol to the machine (to every wheel) in a position. The
     * visible symbols of the wheels do not change.
     * @param pos the position of the new symbol (1..symbols+1)
     * @param color the CSS color name of the symbol; it must have a color
     * different from the colors of the other symbols
     */
    public void addSymbol(int pos, String color) {
        if (!available()) {
            return;
        }
        if (!Canvas.isColor(color)) {
            fail("'" + color + "' no es un nombre de color CSS.");
            return;
        }
        String symbol = normalize(color);
        String same = sameColorSymbol(symbol);
        if (same != null) {
            fail("Ya existe un símbolo del color '" + same + "'.");
            return;
        }
        int index = clamp(pos, symbols.size() + 1) - 1;
        symbols.add(index, symbol);
        for (Wheel wheel : wheels) {
            wheel.symbolAdded(index);
        }
        checkJackpot();
        ok = true;
    }

    /**
     * Delete a symbol from the machine (from every wheel). A symbol visible
     * on a locked wheel can not be deleted. The wheels that showed it show
     * the next symbol.
     * @param symbol the color of the symbol
     */
    public void delSymbol(String symbol) {
        if (!available() || !hasSymbol(symbol)) {
            return;
        }
        String color = normalize(symbol);
        for (Wheel wheel : wheels) {
            if (wheel.isLocked() && color.equals(wheel.visibleSymbol())) {
                fail("El símbolo '" + color + "' está visible en una rueda fijada.");
                return;
            }
        }
        int index = symbols.indexOf(color);
        symbols.remove(index);
        for (Wheel wheel : wheels) {
            wheel.symbolRemoved(index);
        }
        checkJackpot();
        ok = true;
    }

    // ------------------------------------------------------------- spinning

    /**
     * Rotate a wheel so it shows a symbol.
     * @param wheel the position of the wheel
     * @param symbol the color of the symbol
     */
    public void placeSymbol(int wheel, String symbol) {
        if (!available() || !hasWheels() || !hasSymbol(symbol)) {
            return;
        }
        Wheel theWheel = unlockedWheel(wheel);
        if (theWheel == null) {
            return;
        }
        theWheel.show(normalize(symbol));
        checkJackpot();
        ok = true;
    }

    /**
     * Spin a wheel a random number of steps.
     * @param wheel the position of the wheel
     */
    public void spin(int wheel) {
        if (!available() || !hasWheels() || !hasSymbols()) {
            return;
        }
        Wheel theWheel = unlockedWheel(wheel);
        if (theWheel == null) {
            return;
        }
        List<Wheel> toSpin = new ArrayList<Wheel>();
        toSpin.add(theWheel);
        pullLever();
        animate(toSpin, randomSteps(1));
        checkJackpot();
        ok = true;
    }

    /**
     * Rotate a wheel a number of steps. With positive steps the wheel goes
     * forward (it shows the next symbols); with negative steps it goes
     * backward. If the simulator is visible, the movement is shown step by
     * step.
     * @param wheel the position of the wheel
     * @param steps the number of steps
     */
    public void spin(int wheel, int steps) {
        if (!available() || !hasWheels() || !hasSymbols()) {
            return;
        }
        Wheel theWheel = unlockedWheel(wheel);
        if (theWheel == null) {
            return;
        }
        for (int i = 0; i < Math.abs(steps); i++) {
            theWheel.step(steps > 0 ? 1 : -1);
            pause();
        }
        checkJackpot();
        ok = true;
    }

    /**
     * Leave the machine in a given configuration: the wheel i shows the
     * symbol setSymbols[i-1]. Locked wheels must keep their symbol. If the
     * configuration is not valid, nothing changes.
     * @param setSymbols the colors of the symbols, one for each wheel,
     * from left to right
     */
    public void spin(String[] setSymbols) {
        if (!available() || !validConfiguration(setSymbols)) {
            return;
        }
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).show(normalize(setSymbols[i]));
        }
        checkJackpot();
        ok = true;
    }

    /**
     * Spin all the unlocked wheels, each one a random number of steps.
     */
    public void spin() {
        if (!available() || !hasWheels() || !hasSymbols()) {
            return;
        }
        List<Wheel> toSpin = new ArrayList<Wheel>();
        for (Wheel wheel : wheels) {
            if (!wheel.isLocked()) {
                toSpin.add(wheel);
            }
        }
        if (toSpin.isEmpty()) {
            fail("Todas las ruedas están fijadas.");
            return;
        }
        pullLever();
        animate(toSpin, randomSteps(toSpin.size()));
        checkJackpot();
        ok = true;
    }

    // --------------------------------------------------------------- consult

    /**
     * Return the symbols of the machine, in the order they have in the
     * wheels, starting from 1.
     * @return the colors of the symbols
     */
    public String[] symbols() {
        ok = !finished;
        return symbols.toArray(new String[0]);
    }

    /**
     * Return the number of different symbols visible on the wheels.
     * @return the number of different visible symbols
     */
    public int distinctSymbols() {
        ok = !finished;
        return new HashSet<String>(visibleSymbols()).size();
    }

    /**
     * Return the visible symbols of all the wheels, from left to right.
     * If the machine has no symbols, the array is empty.
     * @return the colors of the visible symbols
     */
    public String[] configuration() {
        ok = !finished;
        return visibleSymbols().toArray(new String[0]);
    }

    /**
     * Tell if the machine is a winner: it has wheels and symbols and all
     * the wheels show the same symbol.
     * @return true if the configuration is a jackpot
     */
    public boolean isJackpot() {
        ok = !finished;
        return !wheels.isEmpty() && !symbols.isEmpty() && distinctSymbols() == 1;
    }

    // ------------------------------------------------------------ visibility

    /**
     * Make the simulator visible.
     */
    public void makeVisible() {
        if (!available()) {
            return;
        }
        isVisible = true;
        showAll();
        ok = true;
    }

    /**
     * Make the simulator invisible. It keeps working in invisible mode.
     */
    public void makeInvisible() {
        if (!available()) {
            return;
        }
        hideAll();
        isVisible = false;
        ok = true;
    }

    /**
     * Finish the simulator. After this, no operation can be done.
     */
    public void exit() {
        if (!available()) {
            return;
        }
        if (isVisible) {
            hideAll();
            Canvas.getCanvas().setVisible(false);
        }
        isVisible = false;
        finished = true;
        ok = true;
    }

    /**
     * Tell if the last operation could be done.
     * @return true if the last operation could be done
     */
    public boolean ok() {
        return ok;
    }

    // ------------------------------------------------------ private: checks

    /*
     * Tell if the simulator can still work. If not, the operation fails.
     */
    private boolean available() {
        if (finished) {
            ok = false;
        }
        return !finished;
    }

    /*
     * Tell if the machine has wheels. If not, the operation fails.
     */
    private boolean hasWheels() {
        if (wheels.isEmpty()) {
            fail("La máquina no tiene ruedas.");
        }
        return !wheels.isEmpty();
    }

    /*
     * Tell if the machine has symbols. If not, the operation fails.
     */
    private boolean hasSymbols() {
        if (symbols.isEmpty()) {
            fail("La máquina no tiene símbolos.");
        }
        return !symbols.isEmpty();
    }

    /*
     * Tell if a symbol is in the machine. If not, the operation fails.
     */
    private boolean hasSymbol(String symbol) {
        boolean has = symbol != null && symbols.contains(normalize(symbol));
        if (!has) {
            fail("La máquina no tiene el símbolo '" + symbol + "'.");
        }
        return has;
    }

    /*
     * Return the wheel of a position, or null (the operation fails) if it
     * is locked.
     */
    private Wheel unlockedWheel(int pos) {
        int index = clamp(pos, wheels.size()) - 1;
        if (wheels.get(index).isLocked()) {
            fail("La rueda " + (index + 1) + " está fijada.");
            return null;
        }
        return wheels.get(index);
    }

    /*
     * Tell if a configuration can be set. If not, the operation fails.
     */
    private boolean validConfiguration(String[] setSymbols) {
        if (setSymbols == null || setSymbols.length != wheels.size()) {
            fail("Se necesita un símbolo para cada una de las " + wheels.size() + " ruedas.");
            return false;
        }
        for (int i = 0; i < setSymbols.length; i++) {
            if (!hasSymbol(setSymbols[i])) {
                return false;
            }
            Wheel wheel = wheels.get(i);
            if (wheel.isLocked() && !normalize(setSymbols[i]).equals(wheel.visibleSymbol())) {
                fail("La rueda " + (i + 1) + " está fijada.");
                return false;
            }
        }
        return true;
    }

    /*
     * Return the symbol with the same color (name or RGB value) of a color.
     */
    private String sameColorSymbol(String color) {
        for (String symbol : symbols) {
            if (symbol.equals(color) || Canvas.rgb(symbol) == Canvas.rgb(color)) {
                return symbol;
            }
        }
        return null;
    }

    // ---------------------------------------------------- private: helpers

    /*
     * Lock or unlock a wheel.
     */
    private void setLocked(int pos, boolean locked) {
        if (!available() || !hasWheels()) {
            return;
        }
        int index = clamp(pos, wheels.size()) - 1;
        Wheel wheel = wheels.get(index);
        if (wheel.isLocked() == locked) {
            fail("La rueda " + (index + 1) + (locked ? " ya está fijada." : " no está fijada."));
            return;
        }
        wheel.setLocked(locked);
        ok = true;
    }

    /*
     * Rotate some wheels at the same time, each one its number of steps.
     */
    private void animate(List<Wheel> toSpin, int[] steps) {
        boolean moving = true;
        for (int step = 0; moving; step++) {
            moving = false;
            for (int i = 0; i < toSpin.size(); i++) {
                if (step < steps[i]) {
                    toSpin.get(i).step(1);
                    moving = true;
                }
            }
            pause();
        }
    }

    /*
     * Return random numbers of steps (at least one complete turn).
     */
    private int[] randomSteps(int howMany) {
        int[] steps = new int[howMany];
        for (int i = 0; i < howMany; i++) {
            steps[i] = symbols.size() + random.nextInt(2 * symbols.size());
        }
        return steps;
    }

    /*
     * Return the visible symbols from left to right.
     */
    private List<String> visibleSymbols() {
        List<String> visible = new ArrayList<String>();
        if (!symbols.isEmpty()) {
            for (Wheel wheel : wheels) {
                visible.add(wheel.visibleSymbol());
            }
        }
        return visible;
    }

    /*
     * Adjust a position to the range [1, max].
     */
    private int clamp(int pos, int max) {
        return Math.max(1, Math.min(pos, max));
    }

    /*
     * Return the normalized name of a color.
     */
    private String normalize(String color) {
        return color.trim().toLowerCase();
    }

    /*
     * The operation fails: show a message if the simulator is visible.
     */
    private void fail(String message) {
        ok = false;
        if (isVisible) {
            JOptionPane.showMessageDialog(null, message, "Slot Machine",
                                          JOptionPane.WARNING_MESSAGE);
        }
    }

    // ---------------------------------------------------- private: drawing

    /*
     * Width of the body of the machine.
     */
    private int bodyWidth() {
        return Math.max(1, wheels.size()) * SLOT + 20;
    }

    /*
     * X position of the wheel of an index (from 0).
     */
    private int wheelX(int index) {
        return LEFT + 15 + index * SLOT;
    }

    /*
     * If the jackpot state changed, draw the machine again.
     */
    private void checkJackpot() {
        if (isJackpot() != jackpotShown) {
            redraw();
        }
    }

    /*
     * Place, resize and paint all the figures and draw them again.
     */
    private void redraw() {
        boolean show = isVisible;
        if (show) {
            hideAll();
        }
        layout();
        if (show) {
            showAll();
        }
    }

    /*
     * Place, resize and paint all the figures (the machine is invisible).
     */
    private void layout() {
        int distance = LEFT + bodyWidth() - rightX;
        rightX += distance;
        body.changeSize(BODY_HEIGHT, bodyWidth());
        header.changeSize(70, bodyWidth());
        rightLight.moveHorizontal(distance);
        leverBar.moveHorizontal(distance);
        leverKnob.moveHorizontal(distance);
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).moveTo(wheelX(i));
        }
        jackpotShown = isJackpot();
        body.changeColor(jackpotShown ? "gold" : "silver");
        leftLight.changeColor(jackpotShown ? "lime" : "gray");
        rightLight.changeColor(jackpotShown ? "lime" : "gray");
    }

    /*
     * Draw all the figures, from back to front.
     */
    private void showAll() {
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
     * Erase all the figures.
     */
    private void hideAll() {
        for (Wheel wheel : wheels) {
            wheel.makeInvisible();
        }
        leverKnob.makeInvisible();
        leverBar.makeInvisible();
        rightLight.makeInvisible();
        leftLight.makeInvisible();
        header.makeInvisible();
        body.makeInvisible();
    }

    /*
     * Pull the lever of the machine (only seen if it is visible).
     */
    private void pullLever() {
        if (isVisible) {
            leverKnob.slowMoveVertical(LEVER_PULL);
            leverKnob.slowMoveVertical(-LEVER_PULL);
        }
    }

    /*
     * Wait a moment between two steps (only if it is visible).
     */
    private void pause() {
        if (isVisible) {
            Canvas.getCanvas().wait(STEP_DELAY);
        }
    }
}
