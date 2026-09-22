Proyecto "slotMachine" - DOPO 2026-2 (Ciclos 1 y 2)
Basado en el proyecto "shapes" (Michael Kolling y David J. Barnes).

Clases nuevas:
 - SlotMachine        : clase principal (diseno dado en los enunciados de los ciclos 1 y 2)
 - Wheel              : una rueda de la maquina (simbolos, posicion visible, fijada o no)
 - SlotMachineC2Test  : pruebas de unidad (modo invisible)
 - SlotMachineCC2Test : pruebas compartidas (reemplazar XxYy por sus iniciales)

Extensiones al paquete shapes:
 - Canvas    : soporta todos los colores CSS (setForegroundColor), isValidColor(String),
               close() y un lienzo mas grande (900x320).
 - Rectangle : moveTo(int x, int y)
 - Circle    : moveTo(int x, int y)

Decisiones:
 - Todas las ruedas comparten el mismo orden de simbolos; cada una tiene su simbolo visible.
 - Cada rueda muestra el simbolo visible (grande), el anterior (arriba) y el siguiente (abajo).
 - Una rueda fijada se marca con una barra roja debajo y no se puede mover
   (placeSymbol, spin(wheel), spin(wheel,steps), spin(String[])); spin() la ignora.
 - Estado ganador (isJackpot): el cuerpo de la maquina se vuelve dorado.
 - spin(wheel, steps) con steps negativo no se realiza (ok() = false).
 - Si una operacion no se puede realizar, ok() = false y, si esta visible, se muestra un JOptionPane.
