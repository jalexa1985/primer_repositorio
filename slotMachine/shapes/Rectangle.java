package shapes;

/**
 * A rectangle that can be manipulated and that draws itself on a canvas.
 *
 * Extension (slotMachine): {@link #moveTo(int, int)}, {@link #getColor()},
 * {@link #isVisible()} and any CSS color name is accepted.
 *
 * @author  Michael Kolling and David J. Barnes (Modified)
 * @version 2.0 (slotMachine extension)
 */
public class Rectangle {

    public static int EDGES = 4;

    private int height;
    private int width;
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;

    /**
     * Create a new rectangle at default position with default color.
     */
    public Rectangle() {
        height = 30;
        width = 40;
        xPosition = 70;
        yPosition = 15;
        color = "magenta";
        isVisible = false;
    }

    /**
     * Make this rectangle visible. If it was already visible, do nothing.
     */
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    /**
     * Make this rectangle invisible. If it was already invisible, do nothing.
     */
    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    /**
     * Move the rectangle horizontally.
     * @param distance the desired distance in pixels
     */
    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Move the rectangle vertically.
     * @param distance the desired distance in pixels
     */
    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Move the rectangle to a given position (upper-left corner).
     * (slotMachine extension)
     * @param x the new x coordinate
     * @param y the new y coordinate
     */
    public void moveTo(int x, int y) {
        erase();
        xPosition = x;
        yPosition = y;
        draw();
    }

    /**
     * Change the size to the new size.
     * @param newHeight the new height in pixels. newHeight must be &gt;=0.
     * @param newWidth the new width in pixels. newWidth must be &gt;=0.
     */
    public void changeSize(int newHeight, int newWidth) {
        erase();
        height = newHeight;
        width = newWidth;
        draw();
    }

    /**
     * Change the color.
     * @param newColor the new color (any CSS color name).
     */
    public void changeColor(String newColor) {
        color = newColor;
        draw();
    }

    /**
     * Return the current color. (slotMachine extension)
     * @return the color name
     */
    public String getColor() {
        return color;
    }

    /**
     * Tell whether the rectangle is visible. (slotMachine extension)
     * @return true if it is visible
     */
    public boolean isVisible() {
        return isVisible;
    }

    /*
     * Draw the rectangle with current specifications on screen.
     */
    private void draw() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color,
                new java.awt.Rectangle(xPosition, yPosition, width, height));
            canvas.wait(10);
        }
    }

    /*
     * Erase the rectangle on screen.
     */
    private void erase() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }
}
