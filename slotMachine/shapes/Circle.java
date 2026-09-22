package shapes;

import java.awt.geom.Ellipse2D;

/**
 * A circle that can be manipulated and that draws itself on a canvas.
 *
 * Extension (slotMachine): {@link #moveTo(int, int)}, {@link #getColor()},
 * {@link #isVisible()} and any CSS color name is accepted.
 *
 * @author  Michael Kolling and David J. Barnes
 * @version 2.0 (slotMachine extension)
 */
public class Circle {

    public static final double PI = 3.1416;

    private int diameter;
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;

    /**
     * Create a new circle at default position with default color.
     */
    public Circle() {
        diameter = 30;
        xPosition = 20;
        yPosition = 15;
        color = "blue";
        isVisible = false;
    }

    /**
     * Make this circle visible. If it was already visible, do nothing.
     */
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    /**
     * Make this circle invisible. If it was already invisible, do nothing.
     */
    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    /**
     * Move the circle horizontally.
     * @param distance the desired distance in pixels
     */
    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Move the circle vertically.
     * @param distance the desired distance in pixels
     */
    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Move the circle to a given position (upper-left corner of its box).
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
     * Change the size.
     * @param newDiameter the new size (in pixels). Size must be &gt;=0.
     */
    public void changeSize(int newDiameter) {
        erase();
        diameter = newDiameter;
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
     * Tell whether the circle is visible. (slotMachine extension)
     * @return true if it is visible
     */
    public boolean isVisible() {
        return isVisible;
    }

    /*
     * Draw the circle with current specifications on screen.
     */
    private void draw() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color,
                new Ellipse2D.Double(xPosition, yPosition, diameter, diameter));
            canvas.wait(10);
        }
    }

    /*
     * Erase the circle on screen.
     */
    private void erase() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }
}
