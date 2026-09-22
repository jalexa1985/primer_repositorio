import shapes.Circle;
import shapes.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * A wheel of the slot machine. A wheel is a circular sequence of symbols
 * (identified by their CSS color names); exactly one of them is visible in
 * the window of the machine. A wheel can be locked, so it can not rotate.
 *
 * Visually the wheel shows the visible symbol in the middle of its window,
 * the next symbol above it and the previous one below it (the symbols go
 * down when the wheel rotates forward).
 *
 * @author slotMachine team
 * @version 2.0 (cycle 2)
 */
public class Wheel {

    private List<String> symbols;
    private int current;
    private boolean locked;

    private boolean isVisible;
    private Rectangle frame;
    private Rectangle window;
    private Circle upper;
    private Circle middle;
    private Circle lower;
    private Rectangle lockMark;

    /**
     * Create a wheel with the given symbols (in that order); the first one
     * is the visible symbol.
     * @param symbols the colors of the symbols of the wheel
     */
    public Wheel(List<String> symbols) {
        this.symbols = new ArrayList<String>(symbols);
        current = 0;
        locked = false;
        isVisible = false;
        frame = new Rectangle();
        window = new Rectangle();
        upper = new Circle();
        middle = new Circle();
        lower = new Circle();
        lockMark = new Rectangle();
        frame.changeColor("dimgray");
        window.changeColor("white");
        lockMark.changeColor("lightgray");
    }

    /**
     * Insert a symbol in the wheel. The visible symbol does not change.
     * @param index the index (0 based) of the new symbol, 0 &lt;= index &lt;= size()
     * @param color the color of the new symbol
     */
    public void addSymbol(int index, String color) {
        if (!symbols.isEmpty() && index <= current) {
            current++;
        }
        symbols.add(index, color);
        refresh();
    }

    /**
     * Remove a symbol from the wheel. If it was the visible one, the next
     * symbol becomes visible.
     * @param color the color of the symbol to remove
     */
    public void removeSymbol(String color) {
        int index = symbols.indexOf(color);
        if (index < 0) {
            return;
        }
        symbols.remove(index);
        if (index < current) {
            current--;
        }
        if (current >= symbols.size()) {
            current = 0;
        }
        refresh();
    }

    /**
     * Rotate the wheel a number of steps. Positive steps move forward (the
     * next symbol becomes visible), negative steps move backward.
     * @param steps the number of steps
     */
    public void rotate(int steps) {
        if (!symbols.isEmpty()) {
            current = Math.floorMod(current + steps, symbols.size());
            refresh();
        }
    }

    /**
     * Rotate the wheel so the given symbol becomes visible.
     * @param color the color of the symbol; it must be in the wheel
     */
    public void place(String color) {
        int index = symbols.indexOf(color);
        if (index >= 0) {
            current = index;
            refresh();
        }
    }

    /**
     * Return the number of steps needed to show a symbol moving forward.
     * @param color the color of the symbol
     * @return the number of steps, or -1 if the symbol is not in the wheel
     */
    public int stepsTo(String color) {
        int index = symbols.indexOf(color);
        return index < 0 ? -1 : Math.floorMod(index - current, symbols.size());
    }

    /**
     * Return the visible symbol.
     * @return the color of the visible symbol, or null if the wheel is empty
     */
    public String visibleSymbol() {
        return symbols.isEmpty() ? null : symbols.get(current);
    }

    /**
     * Return the symbols of the wheel in order, starting with the first one.
     * @return the colors of the symbols
     */
    public String[] symbols() {
        return symbols.toArray(new String[0]);
    }

    /**
     * Return the number of symbols of the wheel.
     * @return the number of symbols
     */
    public int size() {
        return symbols.size();
    }

    /**
     * Tell if the wheel has a symbol.
     * @param color the color of the symbol
     * @return true if the symbol is in the wheel
     */
    public boolean contains(String color) {
        return symbols.contains(color);
    }

    /**
     * Lock the wheel: it can not rotate.
     */
    public void lock() {
        locked = true;
        refresh();
    }

    /**
     * Unlock the wheel: it can rotate again.
     */
    public void unlock() {
        locked = false;
        refresh();
    }

    /**
     * Tell if the wheel is locked.
     * @return true if it is locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Place the wheel in the canvas.
     * @param x the x coordinate of the upper-left corner
     * @param y the y coordinate of the upper-left corner
     * @param width the width of the wheel
     * @param height the height of the wheel (without the lock mark)
     */
    public void setBounds(int x, int y, int width, int height) {
        int big = Math.max(6, Math.min(width - 16, height / 3 - 10));
        int small = big * 3 / 5;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        frame.changeSize(height, width);
        frame.moveTo(x, y);
        window.changeSize(height - 10, width - 10);
        window.moveTo(x + 5, y + 5);
        middle.changeSize(big);
        middle.moveTo(centerX - big / 2, centerY - big / 2);
        upper.changeSize(small);
        upper.moveTo(centerX - small / 2, y + 12);
        lower.changeSize(small);
        lower.moveTo(centerX - small / 2, y + height - 12 - small);
        lockMark.changeSize(12, width);
        lockMark.moveTo(x, y + height + 6);
    }

    /**
     * Make the wheel visible.
     */
    public void makeVisible() {
        isVisible = true;
        frame.makeVisible();
        window.makeVisible();
        lockMark.makeVisible();
        refresh();
    }

    /**
     * Make the wheel invisible.
     */
    public void makeInvisible() {
        isVisible = false;
        frame.makeInvisible();
        window.makeInvisible();
        upper.makeInvisible();
        middle.makeInvisible();
        lower.makeInvisible();
        lockMark.makeInvisible();
    }

    /*
     * Update the colors of the visible symbols and the lock mark.
     */
    private void refresh() {
        if (!isVisible) {
            return;
        }
        lockMark.changeColor(locked ? "crimson" : "lightgray");
        if (symbols.isEmpty()) {
            upper.makeInvisible();
            middle.makeInvisible();
            lower.makeInvisible();
            return;
        }
        int n = symbols.size();
        upper.changeColor(symbols.get((current + 1) % n));
        middle.changeColor(symbols.get(current));
        lower.changeColor(symbols.get((current - 1 + n) % n));
        upper.makeVisible();
        middle.makeVisible();
        lower.makeVisible();
    }
}
