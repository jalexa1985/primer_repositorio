import java.util.List;

/**
 * A wheel of a slot machine.
 *
 * All the wheels of a machine share the same list of symbols (the list of
 * the machine), so they have the same symbols in the same order. A wheel
 * knows which of them is visible. The wheel is drawn as a window that shows
 * the visible symbol (big circle) between the previous and the next one
 * (small circles). A locked wheel shows a red triangle under it.
 *
 * @author slotMachine team
 * @version 2.0 (cycle 2)
 */
public class Wheel {

    /** Width in pixels of a wheel. */
    public static final int WIDTH = 90;
    /** Height in pixels of a wheel (including the lock mark). */
    public static final int HEIGHT = 310;

    private static final String EMPTY = "gainsboro";

    private List<String> symbols;
    private int visible;
    private boolean locked;
    private boolean isVisible;
    private int xPosition;

    private Rectangle frame;
    private Rectangle window;
    private Rectangle payLine;
    private Circle previous;
    private Circle current;
    private Circle next;
    private Triangle lockMark;

    /**
     * Create a wheel that shows the first symbol, unlocked and invisible.
     * @param symbols the list of symbols of the machine (shared, not copied)
     * @param x the x position of the wheel
     * @param y the y position of the wheel
     */
    public Wheel(List<String> symbols, int x, int y) {
        this.symbols = symbols;
        visible = 0;
        locked = false;
        isVisible = false;
        xPosition = x;
        frame = Figures.rectangle(x, y, WIDTH, 270, "dimgray");
        window = Figures.rectangle(x + 5, y + 5, WIDTH - 10, 260, "white");
        payLine = Figures.rectangle(x + 5, y + 133, WIDTH - 10, 4, "red");
        previous = Figures.circle(x + 25, y + 15, 40, EMPTY);
        current = Figures.circle(x + 10, y + 100, 70, EMPTY);
        next = Figures.circle(x + 25, y + 215, 40, EMPTY);
        lockMark = Figures.triangle(x + WIDTH / 2, y + 280, 30, 25, "red");
        updateColors();
    }

    /**
     * Return the visible symbol.
     * @return the color of the visible symbol, or null if there are no symbols
     */
    public String visibleSymbol() {
        return symbols.isEmpty() ? null : symbols.get(visible);
    }

    /**
     * Rotate the wheel one step. Forward (direction 1) shows the next symbol;
     * backward (direction -1) shows the previous one.
     * @param direction 1 to go forward, -1 to go backward
     */
    public void step(int direction) {
        if (symbols.isEmpty()) {
            return;
        }
        int size = symbols.size();
        visible = ((visible + direction) % size + size) % size;
        updateColors();
    }

    /**
     * Rotate the wheel until it shows a symbol.
     * @param symbol the color of the symbol; it must be a symbol of the machine
     */
    public void show(String symbol) {
        int index = symbols.indexOf(symbol);
        if (index >= 0) {
            visible = index;
            updateColors();
        }
    }

    /**
     * Update the wheel after a symbol was added to the list of the machine.
     * The visible symbol does not change.
     * @param index the index (from 0) where the symbol was added
     */
    public void symbolAdded(int index) {
        if (symbols.size() > 1 && index <= visible) {
            visible++;
        }
        updateColors();
    }

    /**
     * Update the wheel after a symbol was removed from the list of the
     * machine. If the removed symbol was visible, the next one is shown.
     * @param index the index (from 0) the symbol had
     */
    public void symbolRemoved(int index) {
        if (index < visible) {
            visible--;
        }
        if (visible >= symbols.size()) {
            visible = 0;
        }
        updateColors();
    }

    /**
     * Lock or unlock the wheel.
     * @param locked true to lock the wheel, false to unlock it
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
        if (isVisible) {
            if (locked) {
                lockMark.makeVisible();
            } else {
                lockMark.makeInvisible();
            }
        }
    }

    /**
     * Tell if the wheel is locked.
     * @return true if the wheel is locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Move the wheel horizontally to a position.
     * @param x the new x position of the wheel
     */
    public void moveTo(int x) {
        int distance = x - xPosition;
        xPosition = x;
        frame.moveHorizontal(distance);
        window.moveHorizontal(distance);
        payLine.moveHorizontal(distance);
        previous.moveHorizontal(distance);
        current.moveHorizontal(distance);
        next.moveHorizontal(distance);
        lockMark.moveHorizontal(distance);
    }

    /**
     * Make the wheel visible (it is drawn over the other figures).
     */
    public void makeVisible() {
        isVisible = true;
        frame.makeVisible();
        window.makeVisible();
        payLine.makeVisible();
        previous.makeVisible();
        current.makeVisible();
        next.makeVisible();
        if (locked) {
            lockMark.makeVisible();
        }
    }

    /**
     * Make the wheel invisible.
     */
    public void makeInvisible() {
        isVisible = false;
        frame.makeInvisible();
        window.makeInvisible();
        payLine.makeInvisible();
        previous.makeInvisible();
        current.makeInvisible();
        next.makeInvisible();
        lockMark.makeInvisible();
    }

    /*
     * Paint the circles with the visible symbol and its neighbours.
     */
    private void updateColors() {
        if (symbols.isEmpty()) {
            previous.changeColor(EMPTY);
            current.changeColor(EMPTY);
            next.changeColor(EMPTY);
            return;
        }
        int size = symbols.size();
        previous.changeColor(symbols.get((visible - 1 + size) % size));
        current.changeColor(symbols.get(visible));
        next.changeColor(symbols.get((visible + 1) % size));
    }
}
