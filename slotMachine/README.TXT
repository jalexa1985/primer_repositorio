Proyecto "slotMachine" - DOPO 2026-2 (Ciclos 1, 2 y 3)
Basado en el proyecto "shapes" (Michael Kolling y David J. Barnes).

Clases:
 - SlotMachine        : simulador y testing tool (diseno dado en los enunciados)
 - SlotMachineContest : solucion de la maraton: solve(n) y simulate(n)   [ciclo 3]
 - Wheel              : una rueda (sus simbolos en su propio orden, simbolo visible, fijada o no)
 - SlotMachineC2Test, SlotMachineCC2Test               : pruebas ciclos 1 y 2 (invisibles)
 - SlotMachineContestTest, SlotMachineContestCTest     : pruebas ciclo 3 (invisibles)
 - SlotMachineAcceptanceTest                           : 2 pruebas de aceptacion (visibles)
   En las pruebas compartidas reemplazar XxYy por sus iniciales.

Extensiones al paquete shapes:
 - Canvas    : colores CSS, isValidColor(String), palette(n), paletteSize(), close(), lienzo 900x320
 - Rectangle : moveTo(int x, int y)
 - Circle    : moveTo(int x, int y)

Ciclo 3:
 - SlotMachine(n): n ruedas y n simbolos de colores diferentes; cada rueda tiene los n
   simbolos en orden aleatorio y muestra uno aleatorio. Queda invisible.
 - SlotMachineContest.solve(n) usa solo SlotMachine(n), spin(wheel,steps) y distinctSymbols()
   sobre una maquina invisible y retorna las acciones {rueda, pasos}.
 - SlotMachineContest.simulate(n) hace lo mismo sobre una maquina visible (1 <= n <= 10).
 - Algoritmo (maximo 2n^2 + n + 1 acciones; n = 50 -> menos de 10000):
   1. Por cada rueda se prueban sus n posiciones y se deja en la que da mas simbolos
      distintos. Al final las n ruedas muestran simbolos diferentes.
   2. Se gira la rueda 1 un paso: su simbolo T deja de verse. En cada rueda k se prueban
      las n posiciones: el maximo de simbolos distintos se da en la posicion de T (y quizas
      en la actual), asi se sabe cuantos pasos hay hasta T. La rueda vuelve a su lugar.
   3. Se devuelve la rueda 1 a T y cada rueda k se gira hasta T.

Decisiones (ciclos 1 y 2):
 - Rueda fijada: barra roja debajo; no se mueve con placeSymbol ni spin(...); spin() la ignora.
 - Estado ganador: el cuerpo de la maquina se vuelve dorado.
 - spin(wheel, steps) con steps negativo no se realiza (ok() = false).
 - Si una operacion no se puede realizar, ok() = false y, si esta visible, se muestra un JOptionPane.
