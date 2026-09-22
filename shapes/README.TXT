------------------------------------------------------------------------
PROYECTO: slotMachine (construido sobre el proyecto shapes)
          DOPO - Proyecto inicial 2026-2, ciclos 1 y 2
------------------------------------------------------------------------
PROPÓSITO: Simulador de una máquina tragamonedas inspirado en el
           Problem I "Slot Machine" de la maratón internacional 2025.
           (No resuelve el problema de la maratón; sólo es el simulador.)
VERSIÓN:   2.0 (ciclo 2 - Refactoring y Extensión)
AUTORES:   <Apellido1 Nombre1> - <Apellido2 Nombre2>

CÓMO INICIAR
  1. BlueJ > Project > Open Project > esta carpeta.
  2. Compile.
  3. Clic derecho sobre SlotMachine > new SlotMachine().
  4. Sobre el objeto: makeVisible(), addSymbol(1,"red"),
     addSymbol(2,"royalblue"), addWheel(1), addWheel(2), spin(),
     spin(1,3), lock(1), swap(1,2), spin({"red","red"}), isJackpot() ...
  5. Pruebas: clic derecho sobre SlotMachineC2Test / SlotMachineCC2Test
     > Test All (modo invisible). SlotMachineATest tiene las dos pruebas
     de aceptación (modo visible, piden confirmación al usuario).

CLASES
  SlotMachine  Clase principal (respeta el diagrama del enunciado).
  Wheel        Una rueda: sabe qué símbolo muestra y si está fijada.
  Figures      Crea las figuras de shapes y las ubica usando sus propios
               métodos (moveHorizontal, moveVertical, changeSize,
               changeColor).
  Canvas, Rectangle, Circle, Triangle: proyecto shapes original.

EXTENSIÓN DEL PROYECTO shapes (en astah incluir sólo esto)
  Canvas: + WIDTH, HEIGHT (lienzo de 1000x560, título "Slot Machine")
          + isColor(String) : boolean   (¿es un nombre de color CSS?)
          + rgb(String) : int           (valor RGB de un color CSS)
          - NAMES_AND_CODES, cssColors, colors()
          setForegroundColor ahora acepta los 148 colores CSS.

DECISIONES DE DISEÑO
  - Los símbolos se identifican por su nombre de color CSS (en minúscula).
    No puede haber dos símbolos del mismo color (ni alias con el mismo
    RGB, p.ej. "aqua" y "cyan").
  - Todas las ruedas tienen todos los símbolos, en el orden de symbols().
    Cada rueda muestra uno (círculo grande) entre el anterior y el
    siguiente (círculos pequeños). Una rueda nueva muestra el primero.
  - Posiciones desde 1; menores a 1 -> 1; mayores al máximo -> máximo.
    En addWheel/addSymbol el máximo es cantidad+1 (agregar al final).
  - addSymbol no cambia lo que muestran las ruedas. Si se borra un
    símbolo visible, la rueda muestra el siguiente.
  - spin(wheel, steps): pasos positivos hacia adelante, negativos hacia
    atrás. Si está visible se ve paso a paso.
  - spin(wheel) / spin(): número aleatorio de pasos (animado, con la
    palanca). spin() no mueve las ruedas fijadas; falla si todas lo están.
  - Una rueda fijada (triángulo rojo debajo) no gira (spin, placeSymbol,
    spin(setSymbols)), no se puede eliminar ni borrar el símbolo que
    muestra. Fijar una rueda fijada o soltar una suelta no se puede.
  - spin(setSymbols) es atómico: si algo no es válido no cambia nada.
  - isJackpot(): hay ruedas, hay símbolos y todas muestran el mismo.
    La máquina ganadora se ve dorada con las luces verdes.
  - Si una operación no se puede hacer, ok() es false y, sólo si el
    simulador está visible, se muestra un JOptionPane.
  - exit(): cierra el simulador; después ninguna operación se puede
    realizar (ok() = false).
