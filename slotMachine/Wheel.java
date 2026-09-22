import java.util.ArrayList;
import java.util.List;

/**
 * Una rueda de la maquina tragamonedas.
 * La rueda tiene una lista circular de simbolos (colores) y muestra
 * el simbolo que esta en su posicion actual. Tambien muestra, mas pequenos,
 * el simbolo anterior y el siguiente para que se aprecie el giro.
 *
 * @author (sus nombres)
 * @version Ciclo 2 - 2026-2
 */
public class Wheel {
    public static final int WIDTH = 70;
    public static final int HEIGHT = 170;

    private ArrayList<String> symbols;
    private int position;
    private boolean locked;
    private boolean isVisible;

    private Rectangle border;
    private Rectangle window;
    private Rectangle lockMark;
    private Circle previous;
    private Circle current;
    private Circle next;

    /**
     * Crea una rueda con los simbolos dados, mostrando el primero.
     * @param initialSymbols simbolos (colores) de la rueda en orden
     */
    public Wheel(List<String> initialSymbols) {
        symbols = new ArrayList<String>(initialSymbols);
        position = 0;
        locked = false;
        isVisible = false;
        createShapes();
    }

    /**
     * Adiciona un simbolo en la posicion dada (0 = primera).
     * El simbolo visible no cambia.
     * @param index posicion donde se inserta el simbolo
     * @param color color del simbolo
     */
    public void addSymbol(int index, String color) {
        if (!symbols.isEmpty() && index <= position) {
            position++;
        }
        symbols.add(index, color);
        update();
    }

    /**
     * Elimina un simbolo de la rueda. Si era el visible,
     * queda visible el siguiente.
     * @param color color del simbolo a eliminar
     */
    public void delSymbol(String color) {
        int index = symbols.indexOf(color);
        if (index < 0) {
            return;
        }
        symbols.remove(index);
        if (index < position) {
            position--;
        }
        if (position >= symbols.size()) {
            position = 0;
        }
        update();
    }

    /**
     * Rota la rueda el numero de pasos dado.
     * Cada paso muestra el siguiente simbolo de la rueda.
     * @param steps numero de pasos (puede ser negativo para girar al reves)
     */
    public void rotate(int steps) {
        if (symbols.isEmpty()) {
            return;
        }
        int n = symbols.size();
        position = ((position + steps) % n + n) % n;
        update();
    }

    /**
     * Deja visible el simbolo dado.
     * @param color color del simbolo a mostrar
     * @return true si el simbolo existe en la rueda
     */
    public boolean place(String color) {
        int index = symbols.indexOf(color);
        if (index < 0) {
            return false;
        }
        position = index;
        update();
        return true;
    }

    /**
     * Retorna el simbolo visible de la rueda.
     * @return color del simbolo visible, null si la rueda no tiene simbolos
     */
    public String visibleSymbol() {
        return symbols.isEmpty() ? null : symbols.get(position);
    }

    /**
     * Retorna el numero de simbolos de la rueda.
     * @return cantidad de simbolos
     */
    public int numberOfSymbols() {
        return symbols.size();
    }

    /**
     * Fija la rueda (no se puede mover).
     */
    public void lock() {
        locked = true;
    }

    /**
     * Suelta la rueda (se puede mover).
     */
    public void unlock() {
        locked = false;
    }

    /**
     * Indica si la rueda esta fijada.
     * @return true si esta fijada
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Dibuja la rueda con su esquina superior izquierda en (x, y).
     * @param x coordenada x
     * @param y coordenada y
     */
    public void makeVisible(int x, int y) {
        makeInvisible();
        border.moveTo(x - 3, y - 3);
        window.moveTo(x, y);
        previous.moveTo(x + 20, y + 15);
        current.moveTo(x + 10, y + 60);
        next.moveTo(x + 20, y + 125);
        lockMark.moveTo(x, y + HEIGHT + 8);
        isVisible = true;
        border.makeVisible();
        window.makeVisible();
        drawSymbols();
        if (locked) {
            lockMark.makeVisible();
        }
    }

    /**
     * Borra la rueda de la pantalla.
     */
    public void makeInvisible() {
        border.makeInvisible();
        window.makeInvisible();
        previous.makeInvisible();
        current.makeInvisible();
        next.makeInvisible();
        lockMark.makeInvisible();
        isVisible = false;
    }

    /*
     * Crea las figuras que representan la rueda.
     */
    private void createShapes() {
        border = new Rectangle();
        border.changeSize(HEIGHT + 6, WIDTH + 6);
        border.changeColor("black");
        window = new Rectangle();
        window.changeSize(HEIGHT, WIDTH);
        window.changeColor("white");
        lockMark = new Rectangle();
        lockMark.changeSize(10, WIDTH);
        lockMark.changeColor("red");
        previous = new Circle();
        previous.changeSize(30);
        current = new Circle();
        current.changeSize(50);
        next = new Circle();
        next.changeSize(30);
    }

    /*
     * Actualiza los colores de los simbolos mostrados.
     */
    private void update() {
        if (isVisible) {
            drawSymbols();
        }
    }

    /*
     * Dibuja el simbolo anterior, el actual y el siguiente.
     */
    private void drawSymbols() {
        if (symbols.isEmpty()) {
            previous.makeInvisible();
            current.makeInvisible();
            next.makeInvisible();
            return;
        }
        int n = symbols.size();
        previous.changeColor(symbols.get((position - 1 + n) % n));
        current.changeColor(symbols.get(position));
        next.changeColor(symbols.get((position + 1) % n));
        previous.makeVisible();
        current.makeVisible();
        next.makeVisible();
    }
}
