import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Simulador de una maquina tragamonedas (Problem I, ICPC 2025 - Slot Machine).
 *
 * Los simbolos se identifican mediante colores CSS. Las posiciones se
 * enumeran a partir de 1: si la posicion es menor a 1 se usa la posicion 1 y
 * si es mayor al numero maximo de elementos se usa el maximo.
 *
 * @author (sus nombres)
 * @version Ciclo 2 - 2026-2
 */
public class SlotMachine {
    private static final int X = 20;
    private static final int Y = 20;
    private static final int GAP = 12;
    private static final int STEP_DELAY = 300;
    private static final String BODY_COLOR = "darkslategray";
    private static final String JACKPOT_COLOR = "gold";

    private ArrayList<Wheel> wheels;
    private ArrayList<String> symbols;
    private Rectangle body;
    private boolean isVisible;
    private boolean ok;
    private Random random;

    /**
     * Crea una maquina tragamonedas vacia (sin ruedas ni simbolos) e invisible.
     */
    public SlotMachine() {
        wheels = new ArrayList<Wheel>();
        symbols = new ArrayList<String>();
        body = new Rectangle();
        random = new Random();
        isVisible = false;
        ok = true;
    }

    // ------------------------- manage wheels -------------------------

    /**
     * Adiciona una rueda en la posicion dada. La rueda tiene todos
     * los simbolos de la maquina.
     * @param pos posicion de la nueva rueda (desde 1)
     */
    public void addWheel(int pos) {
        erase();
        int index = clamp(pos, wheels.size() + 1) - 1;
        wheels.add(index, new Wheel(symbols));
        success();
    }

    /**
     * Elimina la rueda de la posicion dada.
     * @param pos posicion de la rueda (desde 1)
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para eliminar.");
            return;
        }
        erase();
        wheels.remove(clamp(pos, wheels.size()) - 1);
        success();
    }

    /**
     * Intercambia dos ruedas.
     * @param wheel1 posicion de la primera rueda (desde 1)
     * @param wheel2 posicion de la segunda rueda (desde 1)
     */
    public void swap(int wheel1, int wheel2) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para intercambiar.");
            return;
        }
        erase();
        Collections.swap(wheels, clamp(wheel1, wheels.size()) - 1,
                         clamp(wheel2, wheels.size()) - 1);
        success();
    }

    /**
     * Fija una rueda: no puede girar ni cambiar de simbolo visible.
     * @param wheel posicion de la rueda (desde 1)
     */
    public void lock(int wheel) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para fijar.");
            return;
        }
        Wheel w = wheelAt(wheel);
        if (w.isLocked()) {
            fail("La rueda ya esta fijada.");
            return;
        }
        w.lock();
        success();
    }

    /**
     * Suelta una rueda fijada.
     * @param wheel posicion de la rueda (desde 1)
     */
    public void unlock(int wheel) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para soltar.");
            return;
        }
        Wheel w = wheelAt(wheel);
        if (!w.isLocked()) {
            fail("La rueda no esta fijada.");
            return;
        }
        w.unlock();
        success();
    }

    // ------------------------- manage symbols -------------------------

    /**
     * Adiciona un simbolo a todas las ruedas en la posicion dada.
     * @param pos posicion del simbolo en la rueda (desde 1)
     * @param color color CSS del simbolo; no puede repetirse
     */
    public void addSymbol(int pos, String color) {
        if (!Canvas.isValidColor(color)) {
            fail("'" + color + "' no es un color CSS valido.");
            return;
        }
        String symbol = normalize(color);
        if (symbols.contains(symbol)) {
            fail("El simbolo " + symbol + " ya existe.");
            return;
        }
        int index = clamp(pos, symbols.size() + 1) - 1;
        symbols.add(index, symbol);
        for (Wheel w : wheels) {
            w.addSymbol(index, symbol);
        }
        success();
    }

    /**
     * Elimina un simbolo de todas las ruedas.
     * @param symbol color del simbolo a eliminar
     */
    public void delSymbol(String symbol) {
        String s = normalize(symbol);
        if (!symbols.contains(s)) {
            fail("El simbolo " + symbol + " no existe.");
            return;
        }
        symbols.remove(s);
        for (Wheel w : wheels) {
            w.delSymbol(s);
        }
        success();
    }

    // ------------------------- spin wheels -------------------------

    /**
     * Deja visible el simbolo dado en una rueda.
     * @param wheel posicion de la rueda (desde 1)
     * @param symbol color del simbolo
     */
    public void placeSymbol(int wheel, String symbol) {
        if (!canMove(wheel)) {
            return;
        }
        String s = normalize(symbol);
        if (!symbols.contains(s)) {
            fail("El simbolo " + symbol + " no existe.");
            return;
        }
        wheelAt(wheel).place(s);
        success();
    }

    /**
     * Gira una rueda de forma aleatoria.
     * @param wheel posicion de la rueda (desde 1)
     */
    public void spin(int wheel) {
        if (!canMove(wheel)) {
            return;
        }
        wheelAt(wheel).rotate(random.nextInt(symbols.size()));
        success();
    }

    /**
     * Rota una rueda un numero de pasos. Si el simulador esta visible
     * el movimiento se muestra paso a paso.
     * @param wheel posicion de la rueda (desde 1)
     * @param steps numero de pasos (mayor o igual a 0)
     */
    public void spin(int wheel, int steps) {
        if (steps < 0) {
            fail("El numero de pasos no puede ser negativo.");
            return;
        }
        if (!canMove(wheel)) {
            return;
        }
        Wheel w = wheelAt(wheel);
        for (int i = 0; i < steps; i++) {
            w.rotate(1);
            if (isVisible) {
                Canvas.getCanvas().wait(STEP_DELAY);
            }
        }
        success();
    }

    /**
     * Deja la maquina en la configuracion dada (un simbolo por rueda,
     * de izquierda a derecha). Las ruedas fijadas deben mantener su simbolo.
     * @param setSymbols simbolos visibles deseados
     */
    public void spin(String[] setSymbols) {
        if (setSymbols == null || setSymbols.length != wheels.size() || wheels.isEmpty()) {
            fail("La configuracion debe tener un simbolo por cada rueda.");
            return;
        }
        for (int i = 0; i < setSymbols.length; i++) {
            String s = normalize(setSymbols[i]);
            Wheel w = wheels.get(i);
            if (!symbols.contains(s)) {
                fail("El simbolo " + setSymbols[i] + " no existe.");
                return;
            }
            if (w.isLocked() && !s.equals(w.visibleSymbol())) {
                fail("La rueda " + (i + 1) + " esta fijada.");
                return;
            }
        }
        for (int i = 0; i < setSymbols.length; i++) {
            wheels.get(i).place(normalize(setSymbols[i]));
        }
        success();
    }

    /**
     * Gira de forma aleatoria todas las ruedas que no estan fijadas.
     */
    public void spin() {
        if (wheels.isEmpty() || symbols.isEmpty()) {
            fail("La maquina necesita ruedas y simbolos para girar.");
            return;
        }
        for (Wheel w : wheels) {
            if (!w.isLocked()) {
                w.rotate(random.nextInt(symbols.size()));
            }
        }
        success();
    }

    // ------------------------- consult symbols -------------------------

    /**
     * Retorna los colores de los simbolos en el orden en que estan en la rueda.
     * @return simbolos de la maquina
     */
    public String[] symbols() {
        ok = true;
        return symbols.toArray(new String[0]);
    }

    /**
     * Retorna el numero de simbolos distintos visibles en la maquina.
     * @return numero de simbolos visibles distintos
     */
    public int distinctSymbols() {
        HashSet<String> distinct = new HashSet<String>();
        for (Wheel w : wheels) {
            if (w.visibleSymbol() != null) {
                distinct.add(w.visibleSymbol());
            }
        }
        ok = true;
        return distinct.size();
    }

    /**
     * Retorna los colores de los simbolos visibles de izquierda a derecha.
     * @return configuracion de la maquina (null en ruedas sin simbolos)
     */
    public String[] configuration() {
        String[] config = new String[wheels.size()];
        for (int i = 0; i < config.length; i++) {
            config[i] = wheels.get(i).visibleSymbol();
        }
        ok = true;
        return config;
    }

    /**
     * Indica si la configuracion es ganadora: hay ruedas y todas
     * muestran el mismo simbolo.
     * @return true si es una configuracion ganadora
     */
    public boolean isJackpot() {
        boolean jackpot = !wheels.isEmpty() && !symbols.isEmpty()
                          && distinctSymbols() == 1;
        ok = true;
        return jackpot;
    }

    // ------------------------- visibility / exit -------------------------

    /**
     * Hace visible el simulador.
     */
    public void makeVisible() {
        isVisible = true;
        success();
    }

    /**
     * Hace invisible el simulador.
     */
    public void makeInvisible() {
        erase();
        isVisible = false;
        ok = true;
    }

    /**
     * Termina el simulador.
     */
    public void exit() {
        makeInvisible();
        Canvas.close();
        ok = true;
    }

    /**
     * Indica si se logro realizar la ultima operacion.
     * @return true si la ultima operacion fue exitosa
     */
    public boolean ok() {
        return ok;
    }

    // ------------------------- private -------------------------

    /*
     * Verifica que la rueda dada pueda moverse. Si no, informa el error.
     */
    private boolean canMove(int wheel) {
        if (wheels.isEmpty() || symbols.isEmpty()) {
            fail("La maquina necesita ruedas y simbolos.");
            return false;
        }
        if (wheelAt(wheel).isLocked()) {
            fail("La rueda " + clamp(wheel, wheels.size()) + " esta fijada.");
            return false;
        }
        return true;
    }

    /*
     * Retorna la rueda de la posicion dada (desde 1, ajustada a los limites).
     */
    private Wheel wheelAt(int pos) {
        return wheels.get(clamp(pos, wheels.size()) - 1);
    }

    /*
     * Ajusta una posicion al rango [1, max].
     */
    private static int clamp(int pos, int max) {
        return Math.max(1, Math.min(pos, max));
    }

    /*
     * Normaliza el nombre de un color.
     */
    private static String normalize(String color) {
        return color == null ? "" : color.trim().toLowerCase();
    }

    /*
     * Registra que la operacion fue exitosa y actualiza la vista.
     */
    private void success() {
        ok = true;
        redraw();
    }

    /*
     * Registra que la operacion fallo y avisa al usuario si esta visible.
     */
    private void fail(String message) {
        ok = false;
        if (isVisible) {
            JOptionPane.showMessageDialog(null, message, "Slot Machine",
                                          JOptionPane.WARNING_MESSAGE);
        }
    }

    /*
     * Dibuja toda la maquina. Si es ganadora, el cuerpo se ve dorado.
     */
    private void redraw() {
        if (!isVisible) {
            return;
        }
        erase();
        int n = Math.max(wheels.size(), 1);
        body.changeSize(Wheel.HEIGHT + 60, n * (Wheel.WIDTH + GAP) + GAP);
        body.changeColor(isJackpot() ? JACKPOT_COLOR : BODY_COLOR);
        body.moveTo(X, Y);
        body.makeVisible();
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).makeVisible(X + GAP + i * (Wheel.WIDTH + GAP), Y + 20);
        }
    }

    /*
     * Borra toda la maquina de la pantalla.
     */
    private void erase() {
        body.makeInvisible();
        for (Wheel w : wheels) {
            w.makeInvisible();
        }
    }
}
