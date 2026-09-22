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
 * @version: 1.6 (shapes)
 */
public class Canvas{
    // Note: The implementation of this class (specifically the handling of
    // shape identity and colors) is slightly more complex than necessary. This
    // is done on purpose to keep the interface and instance fields of the
    // shape objects in this project clean and simple for educational purposes.

	private static Canvas canvasSingleton;

	/**
	 * Factory method to get the canvas singleton object.
	 */
	public static Canvas getCanvas(){
		if(canvasSingleton == null) {
			canvasSingleton = new Canvas("Slot Machine", 900, 320, 
										 Color.white);
		}
		canvasSingleton.setVisible(true);
		return canvasSingleton;
	}

	/**
	 * Hide the canvas window, if it was already created.
	 * (Extension for the slotMachine project)
	 */
	public static void close(){
		if(canvasSingleton != null) {
			canvasSingleton.frame.setVisible(false);
		}
	}

	/**
	 * Tell if a color name is a valid CSS color name.
	 * (Extension for the slotMachine project)
	 * @param colorString the color name (case insensitive)
	 * @return true if it is a CSS named color
	 */
	public static boolean isValidColor(String colorString){
		return colorString != null
			&& CSS_COLORS.containsKey(colorString.trim().toLowerCase());
	}

	// CSS standard named colors (name -> RGB)
	private static final Map<String,Integer> CSS_COLORS = createCssColors();

	private static Map<String,Integer> createCssColors(){
		Object[] data = {
			"aliceblue",0xF0F8FF,"antiquewhite",0xFAEBD7,"aqua",0x00FFFF,"aquamarine",0x7FFFD4,
			"azure",0xF0FFFF,"beige",0xF5F5DC,"bisque",0xFFE4C4,"black",0x000000,
			"blanchedalmond",0xFFEBCD,"blue",0x0000FF,"blueviolet",0x8A2BE2,"brown",0xA52A2A,
			"burlywood",0xDEB887,"cadetblue",0x5F9EA0,"chartreuse",0x7FFF00,"chocolate",0xD2691E,
			"coral",0xFF7F50,"cornflowerblue",0x6495ED,"cornsilk",0xFFF8DC,"crimson",0xDC143C,
			"cyan",0x00FFFF,"darkblue",0x00008B,"darkcyan",0x008B8B,"darkgoldenrod",0xB8860B,
			"darkgray",0xA9A9A9,"darkgreen",0x006400,"darkgrey",0xA9A9A9,"darkkhaki",0xBDB76B,
			"darkmagenta",0x8B008B,"darkolivegreen",0x556B2F,"darkorange",0xFF8C00,"darkorchid",0x9932CC,
			"darkred",0x8B0000,"darksalmon",0xE9967A,"darkseagreen",0x8FBC8F,"darkslateblue",0x483D8B,
			"darkslategray",0x2F4F4F,"darkslategrey",0x2F4F4F,"darkturquoise",0x00CED1,"darkviolet",0x9400D3,
			"deeppink",0xFF1493,"deepskyblue",0x00BFFF,"dimgray",0x696969,"dimgrey",0x696969,
			"dodgerblue",0x1E90FF,"firebrick",0xB22222,"floralwhite",0xFFFAF0,"forestgreen",0x228B22,
			"fuchsia",0xFF00FF,"gainsboro",0xDCDCDC,"ghostwhite",0xF8F8FF,"gold",0xFFD700,
			"goldenrod",0xDAA520,"gray",0x808080,"green",0x008000,"greenyellow",0xADFF2F,
			"grey",0x808080,"honeydew",0xF0FFF0,"hotpink",0xFF69B4,"indianred",0xCD5C5C,
			"indigo",0x4B0082,"ivory",0xFFFFF0,"khaki",0xF0E68C,"lavender",0xE6E6FA,
			"lavenderblush",0xFFF0F5,"lawngreen",0x7CFC00,"lemonchiffon",0xFFFACD,"lightblue",0xADD8E6,
			"lightcoral",0xF08080,"lightcyan",0xE0FFFF,"lightgoldenrodyellow",0xFAFAD2,"lightgray",0xD3D3D3,
			"lightgreen",0x90EE90,"lightgrey",0xD3D3D3,"lightpink",0xFFB6C1,"lightsalmon",0xFFA07A,
			"lightseagreen",0x20B2AA,"lightskyblue",0x87CEFA,"lightslategray",0x778899,"lightslategrey",0x778899,
			"lightsteelblue",0xB0C4DE,"lightyellow",0xFFFFE0,"lime",0x00FF00,"limegreen",0x32CD32,
			"linen",0xFAF0E6,"magenta",0xFF00FF,"maroon",0x800000,"mediumaquamarine",0x66CDAA,
			"mediumblue",0x0000CD,"mediumorchid",0xBA55D3,"mediumpurple",0x9370DB,"mediumseagreen",0x3CB371,
			"mediumslateblue",0x7B68EE,"mediumspringgreen",0x00FA9A,"mediumturquoise",0x48D1CC,"mediumvioletred",0xC71585,
			"midnightblue",0x191970,"mintcream",0xF5FFFA,"mistyrose",0xFFE4E1,"moccasin",0xFFE4B5,
			"navajowhite",0xFFDEAD,"navy",0x000080,"oldlace",0xFDF5E6,"olive",0x808000,
			"olivedrab",0x6B8E23,"orange",0xFFA500,"orangered",0xFF4500,"orchid",0xDA70D6,
			"palegoldenrod",0xEEE8AA,"palegreen",0x98FB98,"paleturquoise",0xAFEEEE,"palevioletred",0xDB7093,
			"papayawhip",0xFFEFD5,"peachpuff",0xFFDAB9,"peru",0xCD853F,"pink",0xFFC0CB,
			"plum",0xDDA0DD,"powderblue",0xB0E0E6,"purple",0x800080,"rebeccapurple",0x663399,
			"red",0xFF0000,"rosybrown",0xBC8F8F,"royalblue",0x4169E1,"saddlebrown",0x8B4513,
			"salmon",0xFA8072,"sandybrown",0xF4A460,"seagreen",0x2E8B57,"seashell",0xFFF5EE,
			"sienna",0xA0522D,"silver",0xC0C0C0,"skyblue",0x87CEEB,"slateblue",0x6A5ACD,
			"slategray",0x708090,"slategrey",0x708090,"snow",0xFFFAFA,"springgreen",0x00FF7F,
			"steelblue",0x4682B4,"tan",0xD2B48C,"teal",0x008080,"thistle",0xD8BFD8,
			"tomato",0xFF6347,"turquoise",0x40E0D0,"violet",0xEE82EE,"wheat",0xF5DEB3,
			"white",0xFFFFFF,"whitesmoke",0xF5F5F5,"yellow",0xFFFF00,"yellowgreen",0x9ACD32
		};
		Map<String,Integer> colors = new HashMap<String,Integer>();
		for(int i = 0; i < data.length; i += 2) {
			colors.put((String)data[i], (Integer)data[i + 1]);
		}
		return colors;
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
     * @param  colorString   the new colour (CSS name) for the foreground of the Canvas 
     */
    public void setForegroundColor(String colorString){
        // Extended for the slotMachine project: supports all CSS named colors
        Integer rgb = (colorString == null) ? null
                      : CSS_COLORS.get(colorString.trim().toLowerCase());
        graphic.setColor(rgb == null ? Color.black : new Color(rgb));
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
