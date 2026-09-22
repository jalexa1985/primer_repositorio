/**
 * Factory of the figures of the simulator, built reusing the classes of the
 * shapes project (Rectangle, Circle and Triangle).
 *
 * The shapes are created at their default position and then are placed with
 * their own methods (moveHorizontal, moveVertical, changeSize and
 * changeColor). The figures are created invisible.
 *
 * @author slotMachine team
 * @version 2.0 (cycle 2)
 */
public class Figures {

    /** Default x position of a new Rectangle (see Rectangle()). */
    private static final int RECTANGLE_X = 70;
    /** Default y position of a new Rectangle (see Rectangle()). */
    private static final int RECTANGLE_Y = 15;
    /** Default x position of a new Circle (see Circle()). */
    private static final int CIRCLE_X = 20;
    /** Default y position of a new Circle (see Circle()). */
    private static final int CIRCLE_Y = 15;
    /** Default x position (top vertex) of a new Triangle (see Triangle()). */
    private static final int TRIANGLE_X = 140;
    /** Default y position (top vertex) of a new Triangle (see Triangle()). */
    private static final int TRIANGLE_Y = 15;

    /**
     * Figures has only class methods.
     */
    private Figures() {
    }

    /**
     * Create an invisible rectangle.
     * @param x the x position of the upper left corner
     * @param y the y position of the upper left corner
     * @param width the width in pixels
     * @param height the height in pixels
     * @param color the color
     * @return the new rectangle
     */
    public static Rectangle rectangle(int x, int y, int width, int height, String color) {
        Rectangle rectangle = new Rectangle();
        rectangle.moveHorizontal(x - RECTANGLE_X);
        rectangle.moveVertical(y - RECTANGLE_Y);
        rectangle.changeSize(height, width);
        rectangle.changeColor(color);
        return rectangle;
    }

    /**
     * Create an invisible circle.
     * @param x the x position of the upper left corner of its bounding box
     * @param y the y position of the upper left corner of its bounding box
     * @param diameter the diameter in pixels
     * @param color the color
     * @return the new circle
     */
    public static Circle circle(int x, int y, int diameter, String color) {
        Circle circle = new Circle();
        circle.moveHorizontal(x - CIRCLE_X);
        circle.moveVertical(y - CIRCLE_Y);
        circle.changeSize(diameter);
        circle.changeColor(color);
        return circle;
    }

    /**
     * Create an invisible triangle (pointing up).
     * @param x the x position of the top vertex
     * @param y the y position of the top vertex
     * @param width the width of the base in pixels
     * @param height the height in pixels
     * @param color the color
     * @return the new triangle
     */
    public static Triangle triangle(int x, int y, int width, int height, String color) {
        Triangle triangle = new Triangle();
        triangle.moveHorizontal(x - TRIANGLE_X);
        triangle.moveVertical(y - TRIANGLE_Y);
        triangle.changeSize(height, width);
        triangle.changeColor(color);
        return triangle;
    }
}
