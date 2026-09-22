import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Canvas is a class to allow for simple graphical drawing on a canvas.
 * This is a modification of the general purpose Canvas, specially made for
 * the BlueJ "shapes" example. 
 *
 * @author: Bruce Quig
 * @author: Michael Kolling (mik)
 *
 * Extension (slotMachine): the canvas is bigger, it accepts every color name
 * of the CSS standard and it can tell if a color name is valid
 * ({@link #isColor(String)}) and its RGB value ({@link #rgb(String)}).
 *
 * @version: 1.7 (shapes - slotMachine extension)
 */
public class Canvas{
    // Note: The implementation of this class (specifically the handling of
    // shape identity and colors) is slightly more complex than necessary. This
    // is done on purpose to keep the interface and instance fields of the
    // shape objects in this project clean and simple for educational purposes.

	private static Canvas canvasSingleton;

    /** Width in pixels of the canvas. */
    public static final int WIDTH = 1000;
    /** Height in pixels of the canvas. */
    public static final int HEIGHT = 560;

    private static final String[] NAMES_AND_CODES = {
        "aliceblue", "f0f8ff", "antiquewhite", "faebd7", "aqua", "00ffff",
        "aquamarine", "7fffd4", "azure", "f0ffff", "beige", "f5f5dc",
        "bisque", "ffe4c4", "black", "000000", "blanchedalmond", "ffebcd",
        "blue", "0000ff", "blueviolet", "8a2be2", "brown", "a52a2a",
        "burlywood", "deb887", "cadetblue", "5f9ea0", "chartreuse", "7fff00",
        "chocolate", "d2691e", "coral", "ff7f50", "cornflowerblue", "6495ed",
        "cornsilk", "fff8dc", "crimson", "dc143c", "cyan", "00ffff",
        "darkblue", "00008b", "darkcyan", "008b8b", "darkgoldenrod", "b8860b",
        "darkgray", "a9a9a9", "darkgreen", "006400", "darkgrey", "a9a9a9",
        "darkkhaki", "bdb76b", "darkmagenta", "8b008b", "darkolivegreen", "556b2f",
        "darkorange", "ff8c00", "darkorchid", "9932cc", "darkred", "8b0000",
        "darksalmon", "e9967a", "darkseagreen", "8fbc8f", "darkslateblue", "483d8b",
        "darkslategray", "2f4f4f", "darkslategrey", "2f4f4f", "darkturquoise", "00ced1",
        "darkviolet", "9400d3", "deeppink", "ff1493", "deepskyblue", "00bfff",
        "dimgray", "696969", "dimgrey", "696969", "dodgerblue", "1e90ff",
        "firebrick", "b22222", "floralwhite", "fffaf0", "forestgreen", "228b22",
        "fuchsia", "ff00ff", "gainsboro", "dcdcdc", "ghostwhite", "f8f8ff",
        "gold", "ffd700", "goldenrod", "daa520", "gray", "808080",
        "green", "008000", "greenyellow", "adff2f", "grey", "808080",
        "honeydew", "f0fff0", "hotpink", "ff69b4", "indianred", "cd5c5c",
        "indigo", "4b0082", "ivory", "fffff0", "khaki", "f0e68c",
        "lavender", "e6e6fa", "lavenderblush", "fff0f5", "lawngreen", "7cfc00",
        "lemonchiffon", "fffacd", "lightblue", "add8e6", "lightcoral", "f08080",
        "lightcyan", "e0ffff", "lightgoldenrodyellow", "fafad2", "lightgray", "d3d3d3",
        "lightgreen", "90ee90", "lightgrey", "d3d3d3", "lightpink", "ffb6c1",
        "lightsalmon", "ffa07a", "lightseagreen", "20b2aa", "lightskyblue", "87cefa",
        "lightslategray", "778899", "lightslategrey", "778899", "lightsteelblue", "b0c4de",
        "lightyellow", "ffffe0", "lime", "00ff00", "limegreen", "32cd32",
        "linen", "faf0e6", "magenta", "ff00ff", "maroon", "800000",
        "mediumaquamarine", "66cdaa", "mediumblue", "0000cd", "mediumorchid", "ba55d3",
        "mediumpurple", "9370db", "mediumseagreen", "3cb371", "mediumslateblue", "7b68ee",
        "mediumspringgreen", "00fa9a", "mediumturquoise", "48d1cc", "mediumvioletred", "c71585",
        "midnightblue", "191970", "mintcream", "f5fffa", "mistyrose", "ffe4e1",
        "moccasin", "ffe4b5", "navajowhite", "ffdead", "navy", "000080",
        "oldlace", "fdf5e6", "olive", "808000", "olivedrab", "6b8e23",
        "orange", "ffa500", "orangered", "ff4500", "orchid", "da70d6",
        "palegoldenrod", "eee8aa", "palegreen", "98fb98", "paleturquoise", "afeeee",
        "palevioletred", "db7093", "papayawhip", "ffefd5", "peachpuff", "ffdab9",
        "peru", "cd853f", "pink", "ffc0cb", "plum", "dda0dd",
        "powderblue", "b0e0e6", "purple", "800080", "rebeccapurple", "663399",
        "red", "ff0000", "rosybrown", "bc8f8f", "royalblue", "4169e1",
        "saddlebrown", "8b4513", "salmon", "fa8072", "sandybrown", "f4a460",
        "seagreen", "2e8b57", "seashell", "fff5ee", "sienna", "a0522d",
        "silver", "c0c0c0", "skyblue", "87ceeb", "slateblue", "6a5acd",
        "slategray", "708090", "slategrey", "708090", "snow", "fffafa",
        "springgreen", "00ff7f", "steelblue", "4682b4", "tan", "d2b48c",
        "teal", "008080", "thistle", "d8bfd8", "tomato", "ff6347",
        "turquoise", "40e0d0", "violet", "ee82ee", "wheat", "f5deb3",
        "white", "ffffff", "whitesmoke", "f5f5f5", "yellow", "ffff00",
        "yellowgreen", "9acd32"
    };

    private static HashMap<String, Color> cssColors;

    /**
     * Tell if a name is a valid CSS color name (case insensitive).
     * @param colorName the name of the color
     * @return true if the name is a CSS color name
     */
    public static boolean isColor(String colorName){
        return colorName != null && colors().containsKey(colorName.trim().toLowerCase());
    }

    /**
     * Return the RGB value of a CSS color name.
     * @param colorName the name of the color
     * @return the RGB value, or -1 if it is not a CSS color name
     */
    public static int rgb(String colorName){
        return isColor(colorName) ? colors().get(colorName.trim().toLowerCase()).getRGB() : -1;
    }

    /*
     * Build (only once) the table of CSS colors.
     */
    private static HashMap<String, Color> colors(){
        if(cssColors == null) {
            cssColors = new HashMap<String, Color>();
            for(int i = 0; i < NAMES_AND_CODES.length; i += 2) {
                cssColors.put(NAMES_AND_CODES[i],
                              new Color(Integer.parseInt(NAMES_AND_CODES[i + 1], 16)));
            }
        }
        return cssColors;
    }

	/**
	 * Factory method to get the canvas singleton object.
	 */
	public static Canvas getCanvas(){
		if(canvasSingleton == null) {
			canvasSingleton = new Canvas("Slot Machine", WIDTH, HEIGHT, 
										 Color.white);
		}
		canvasSingleton.setVisible(true);
		return canvasSingleton;
	}

	//  ----- instance part -----

    private JFrame frame;
    private CanvasPane canvas;
    private Graphics2D graphic;
    private Color backgroundColour;
    private Image canvasImage;
    private List <Object> objects;
    private HashMap <Object,ShapeDescription> shapes;
    
    /**
     * Create a Canvas.
     * @param title  title to appear in Canvas Frame
     * @param width  the desired width for the canvas
     * @param height  the desired height for the canvas
     * @param bgClour  the desired background colour of the canvas
     */
    private Canvas(String title, int width, int height, Color bgColour){
        frame = new JFrame();
        canvas = new CanvasPane();
        frame.setContentPane(canvas);
        frame.setTitle(title);
        canvas.setPreferredSize(new Dimension(width, height));
        backgroundColour = bgColour;
        frame.pack();
        objects = new ArrayList <Object>();
        shapes = new HashMap <Object,ShapeDescription>();
    }

    /**
     * Set the canvas visibility and brings canvas to the front of screen
     * when made visible. This method can also be used to bring an already
     * visible canvas to the front of other windows.
     * @param visible  boolean value representing the desired visibility of
     * the canvas (true or false) 
     */
    public void setVisible(boolean visible){
        if(graphic == null) {
            // first time: instantiate the offscreen image and fill it with
            // the background colour
            Dimension size = canvas.getSize();
            canvasImage = canvas.createImage(size.width, size.height);
            graphic = (Graphics2D)canvasImage.getGraphics();
            graphic.setColor(backgroundColour);
            graphic.fillRect(0, 0, size.width, size.height);
            graphic.setColor(Color.black);
        }
        frame.setVisible(visible);
    }

    /**
     * Draw a given shape onto the canvas.
     * @param  referenceObject  an object to define identity for this shape
     * @param  color            the color of the shape
     * @param  shape            the shape object to be drawn on the canvas
     */
     // Note: this is a slightly backwards way of maintaining the shape
     // objects. It is carefully designed to keep the visible shape interfaces
     // in this project clean and simple for educational purposes.
    public void draw(Object referenceObject, String color, Shape shape){
    	objects.remove(referenceObject);   // just in case it was already there
    	objects.add(referenceObject);      // add at the end
    	shapes.put(referenceObject, new ShapeDescription(shape, color));
    	redraw();
    }
 
    /**
     * Erase a given shape's from the screen.
     * @param  referenceObject  the shape object to be erased 
     */
    public void erase(Object referenceObject){
    	objects.remove(referenceObject);   // just in case it was already there
    	shapes.remove(referenceObject);
    	redraw();
    }

    /**
     * Set the foreground colour of the Canvas.
     * @param  newColour   the new colour for the foreground of the Canvas 
     */
    public void setForegroundColor(String colorString){
		if(colorString.equals("red"))
			graphic.setColor(Color.red);
		else if(colorString.equals("black"))
			graphic.setColor(Color.black);
		else if(colorString.equals("blue"))
			graphic.setColor(Color.blue);
		else if(colorString.equals("yellow"))
			graphic.setColor(Color.yellow);
		else if(colorString.equals("green"))
			graphic.setColor(Color.green);
		else if(colorString.equals("magenta"))
			graphic.setColor(Color.magenta);
		else if(colorString.equals("white"))
			graphic.setColor(Color.white);
		else if(isColor(colorString))
			graphic.setColor(colors().get(colorString.trim().toLowerCase()));
		else
			graphic.setColor(Color.black);
    }

    /**
     * Wait for a specified number of milliseconds before finishing.
     * This provides an easy way to specify a small delay which can be
     * used when producing animations.
     * @param  milliseconds  the number 
     */
    public void wait(int milliseconds){
        try{
            Thread.sleep(milliseconds);
        } catch (Exception e){
            // ignoring exception at the moment
        }
    }

	/**
	 * Redraw ell shapes currently on the Canvas.
	 */
	private void redraw(){
		erase();
		for(Iterator i=objects.iterator(); i.hasNext(); ) {
                       shapes.get(i.next()).draw(graphic);
        }
        canvas.repaint();
    }
       
    /**
     * Erase the whole canvas. (Does not repaint.)
     */
    private void erase(){
        Color original = graphic.getColor();
        graphic.setColor(backgroundColour);
        Dimension size = canvas.getSize();
        graphic.fill(new java.awt.Rectangle(0, 0, size.width, size.height));
        graphic.setColor(original);
    }


    /************************************************************************
     * Inner class CanvasPane - the actual canvas component contained in the
     * Canvas frame. This is essentially a JPanel with added capability to
     * refresh the image drawn on it.
     */
    private class CanvasPane extends JPanel{
        public void paint(Graphics g){
            g.drawImage(canvasImage, 0, 0, null);
        }
    }
    
    /************************************************************************
     * Inner class CanvasPane - the actual canvas component contained in the
     * Canvas frame. This is essentially a JPanel with added capability to
     * refresh the image drawn on it.
     */
    private class ShapeDescription{
    	private Shape shape;
    	private String colorString;

		public ShapeDescription(Shape shape, String color){
    		this.shape = shape;
    		colorString = color;
    	}

		public void draw(Graphics2D graphic){
			setForegroundColor(colorString);
			graphic.draw(shape);
			graphic.fill(shape);
		}
    }

}
