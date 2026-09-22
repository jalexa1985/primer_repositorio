------------------------------------------------------------------------
PROYECTO: slotMachine  (DOPO - Proyecto inicial 2026-2, ciclos 1 y 2)
------------------------------------------------------------------------
PROPÓSITO: Simulador de una máquina tragamonedas inspirado en el
           Problem I "Slot Machine" de la maratón internacional ICPC 2025.
           (No resuelve el problema de la maratón; sólo es el simulador.)
VERSIÓN:   2.0 (ciclo 2 - Refactoring y Extensión)
AUTORES:   <Apellido1 Nombre1> - <Apellido2 Nombre2>

CÓMO INICIAR
  1. Abrir la carpeta slotMachine con BlueJ (Project > Open Project).
  2. Compilar (botón Compile).
  3. Clic derecho sobre SlotMachine > new SlotMachine().
  4. Sobre el objeto: makeVisible(), addSymbol(1,"red"), addSymbol(2,"gold"),
     addWheel(1), addWheel(2), spin(), spin(1,3), isJackpot(), ...
  5. Pruebas: clic derecho sobre SlotMachineC2Test / SlotMachineCC2Test
     > Test All (modo invisible). SlotMachineATest contiene las dos pruebas
     de aceptación (modo visible, piden confirmación al usuario).

DECISIONES DE DISEÑO
  - Los símbolos se identifican por su nombre de color CSS (148 colores,
    clase shapes.CssColors). No se permiten dos símbolos del mismo color
    (tampoco alias con el mismo RGB, p.ej. "aqua" y "cyan").
    Los nombres se guardan en minúscula.
  - Todas las ruedas tienen todos los símbolos de la máquina, en el mismo
    orden (el que retorna symbols()). Cada rueda muestra uno de ellos.
  - Las posiciones empiezan en 1 y se ajustan al rango [1, máximo].
    En addWheel/addSymbol el máximo es cantidad+1 (agregar al final).
  - Una rueda nueva muestra el primer símbolo.
  - addSymbol no cambia el símbolo visible de las ruedas. Si se borra un
    símbolo visible, la rueda muestra el siguiente.
  - spin(wheel, steps): pasos positivos hacia adelante (se ve el siguiente
    símbolo), negativos hacia atrás. Si está visible, se anima paso a paso.
  - spin(wheel) / spin(): giran un número aleatorio de pasos (animado).
    spin() no mueve las ruedas fijadas.
  - Una rueda fijada (lock) no puede girar (spin, placeSymbol,
    spin(setSymbols)), no se puede eliminar y no se puede borrar el símbolo
    que muestra. Se ve con una marca roja debajo de la rueda.
  - spin(setSymbols) es atómico: si algo no es válido, no cambia nada.
  - distinctSymbols(): cantidad de símbolos distintos VISIBLES.
  - configuration(): símbolos visibles de izquierda a derecha (arreglo
    vacío si la máquina no tiene símbolos).
  - isJackpot(): hay ruedas, hay símbolos y todas muestran el mismo.
    La máquina ganadora se ve dorada con las luces verdes encendidas.
  - Si una operación no se puede hacer, ok() es false y, sólo si el
    simulador está visible, se muestra un JOptionPane.
  - exit(): hace invisible el simulador; después ninguna operación se
    puede realizar (ok() = false).

EXTENSIONES AL PAQUETE shapes (incluir en astah sólo esto)
  - Canvas: tamaño 900x520, título "Slot Machine", colores CSS.
  - Rectangle, Circle: moveTo(int,int), getColor(), isVisible().
  - Triangle: moveTo(int,int).
  - CssColors (nueva): exists(String), get(String), normalize(String).

CLASES
  SlotMachine  clase principal (diseño dado).
  Wheel        rueda: símbolos, símbolo visible, fijada; se dibuja sola.
  SlotMachineC2Test   pruebas de unidad (invisible), ciclos 1 y 2.
  SlotMachineCC2Test  pruebas compartidas (renombrar XXYY con las
                      iniciales de los autores, p.ej. accordingDcAvShould..).
  SlotMachineATest    dos pruebas de aceptación (visible).
