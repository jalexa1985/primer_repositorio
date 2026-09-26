import java.util.ArrayList;

/**
 * Solucion del Problem I (Slot Machine) de la maraton internacional 2025.
 *
 * La maquina tiene n ruedas y n simbolos; cada rueda tiene los n simbolos en
 * algun orden. Solo se puede girar una rueda un numero de pasos y preguntar
 * cuantos simbolos distintos se ven. Hay que lograr que todas las ruedas
 * muestren el mismo simbolo.
 *
 * SlotMachine se usa solo como testing tool (SlotMachine(n), spin(wheel,steps)
 * y distinctSymbols()) y como simulador (makeVisible() y makeInvisible()).
 *
 * Estrategia (a lo sumo 2n^2 + n + 1 acciones):
 * 1. Dejar todas las ruedas con simbolos distintos: cada rueda se gira a la
 *    posicion que maximiza la cantidad de simbolos distintos (un simbolo que
 *    no se ve en las otras ruedas). Al final se ven los n simbolos.
 * 2. Girar la rueda 1 un paso: su simbolo original T deja de verse.
 *    Para cada otra rueda k se prueban sus n posiciones: las posiciones con el
 *    maximo de simbolos distintos son la actual y la de T (o solo la de T),
 *    asi que se sabe donde esta T en la rueda k. La rueda k vuelve a su lugar.
 * 3. Se devuelve la rueda 1 a T y se gira cada rueda k hasta T.
 *
 * @author (sus nombres)
 * @version Ciclo 3 - 2026-2
 */
public class SlotMachineContest {
    /** Maximo numero de ruedas que se pueden simular en pantalla. */
    public static final int MAX_SIMULATION = 10;

    /**
     * Resuelve el problema para una maquina de n ruedas y n simbolos
     * inicializada aleatoriamente. La maquina permanece invisible.
     * @param n numero de ruedas y simbolos
     * @return acciones (rueda, pasos) necesarias para ganar; vacio si n no es valido
     */
    public static int[][] solve(int n) {
        if (n < 1 || n > SlotMachine.maxSymbols()) {
            return new int[0][2];
        }
        SlotMachine machine = new SlotMachine(n);
        machine.makeInvisible();
        return solve(machine, n);
    }

    /**
     * Simula, en una maquina visible de n ruedas y n simbolos inicializada
     * aleatoriamente, las acciones necesarias para ganar.
     * Solo es posible si 1 &lt;= n &lt;= MAX_SIMULATION.
     * @param n numero de ruedas y simbolos
     */
    public static void simulate(int n) {
        if (n < 1 || n > MAX_SIMULATION) {
            return;
        }
        SlotMachine machine = new SlotMachine(n);
        machine.makeVisible();
        solve(machine, n);
    }

    /**
     * Resuelve el problema sobre una maquina dada de n ruedas y n simbolos
     * (cada rueda con todos los simbolos). Solo usa spin(wheel,steps) y
     * distinctSymbols(). Es de paquete para poder probarlo.
     * @param machine maquina a resolver
     * @param n numero de ruedas y simbolos
     * @return acciones (rueda, pasos) realizadas
     */
    static int[][] solve(SlotMachine machine, int n) {
        ArrayList<int[]> actions = new ArrayList<int[]>();
        if (n > 1 && machine.distinctSymbols() > 1) {
            makeAllDifferent(machine, n, actions);
            int[] targets = findTargets(machine, n, actions);
            spin(machine, 1, n - 1, actions);
            for (int k = 2; k <= n; k++) {
                spin(machine, k, targets[k], actions);
            }
        }
        return actions.toArray(new int[0][]);
    }

    /*
     * Fase 1: deja todas las ruedas mostrando simbolos diferentes.
     */
    private static void makeAllDifferent(SlotMachine machine, int n, ArrayList<int[]> actions) {
        for (int wheel = 1; wheel <= n && machine.distinctSymbols() < n; wheel++) {
            int[] distinct = scan(machine, wheel, n, actions);
            int best = 0;
            for (int p = 1; p < n; p++) {
                if (distinct[p] > distinct[best]) {
                    best = p;
                }
            }
            spin(machine, wheel, (best + 1) % n, actions);
        }
    }

    /*
     * Fase 2: con la rueda 1 girada un paso, encuentra en cada rueda k
     * cuantos pasos hay desde su posicion actual hasta el simbolo T.
     */
    private static int[] findTargets(SlotMachine machine, int n, ArrayList<int[]> actions) {
        int[] targets = new int[n + 1];
        spin(machine, 1, 1, actions);
        for (int k = 2; k <= n; k++) {
            int[] distinct = scan(machine, k, n, actions);
            spin(machine, k, 1, actions);
            int best = 1;
            for (int p = 2; p < n; p++) {
                if (distinct[p] > distinct[best]) {
                    best = p;
                }
            }
            targets[k] = best;
        }
        return targets;
    }

    /*
     * Prueba las n posiciones de una rueda. distinct[p] es el numero de
     * simbolos distintos cuando la rueda esta p pasos adelante. La rueda
     * queda en la posicion n-1.
     */
    private static int[] scan(SlotMachine machine, int wheel, int n, ArrayList<int[]> actions) {
        int[] distinct = new int[n];
        distinct[0] = machine.distinctSymbols();
        for (int p = 1; p < n; p++) {
            spin(machine, wheel, 1, actions);
            distinct[p] = machine.distinctSymbols();
        }
        return distinct;
    }

    /*
     * Gira una rueda y registra la accion (si los pasos no son cero).
     */
    private static void spin(SlotMachine machine, int wheel, int steps, ArrayList<int[]> actions) {
        if (steps > 0) {
            machine.spin(wheel, steps);
            actions.add(new int[]{wheel, steps});
        }
    }
}
