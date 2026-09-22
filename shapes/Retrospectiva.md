# Retrospectiva – Proyecto inicial slotMachine (ciclos 1 y 2)

> Plantilla con la información técnica ya completada. Los datos personales
> (horas, logros, trabajo en equipo) deben escribirlos los autores.

## Mini-ciclos definidos

| Ciclo | Mini-ciclo | Justificación |
|---|---|---|
| 1 | 1. Extender shapes (colores CSS, lienzo) y crear `Figures` | Todo lo demás depende de poder dibujar y ubicar figuras. |
| 1 | 2. `Wheel` + `addWheel/delWheel` | La rueda es la unidad básica del simulador. |
| 1 | 3. `addSymbol/delSymbol` | Los símbolos se comparten entre todas las ruedas. |
| 1 | 4. `placeSymbol`, `spin(wheel)`, `spin()` | Giros sobre ruedas y símbolos existentes. |
| 1 | 5. Consultas (`symbols`, `distinctSymbols`, `configuration`, `isJackpot`) y aspecto ganador | Leen el estado ya construido. |
| 1 | 6. Visibilidad, `exit`, `ok` y mensajes | Transversales a todas las operaciones. |
| 2 | 7. `swap`, `lock`, `unlock` | Extensión de *manage wheels*. |
| 2 | 8. `spin(wheel, steps)` paso a paso y `spin(setSymbols)` | Extensión de *spin wheels*. |
| 2 | 9. Pruebas `SlotMachineC2Test`, `SlotMachineCC2Test`, `SlotMachineATest` | Validar qué debe y qué no debe hacer. |

## Estado actual
Todos los mini-ciclos (1–9) están terminados. 41 pruebas de unidad pasan
en modo invisible. Pendiente: diagramas en astah (clases y secuencia).

## Preguntas
3. Tiempo total invertido por cada uno (horas/hombre): _completar_.
4. Mayor logro: _completar_.
5. Mayor problema técnico: las figuras de shapes no tienen `moveTo`; se
   resolvió creando `Figures`, que ubica cada figura con `moveHorizontal` /
   `moveVertical` desde su posición por omisión. También el orden de
   dibujo del `Canvas` (la última figura dibujada queda encima), resuelto
   redibujando en orden (cuerpo → encabezado → luces → palanca → ruedas).
6. Qué hicimos bien como equipo / compromisos: _completar_.
7. Práctica XP más útil: _completar_ (p. ej. pruebas de unidad primero).
8. Referencias:
   - Barnes, D. J. & Kölling, M. (2017). *Objects First with Java: A Practical
     Introduction Using BlueJ* (6th ed.). Pearson.
   - Oracle. (s. f.). *Java Platform SE Documentation* – `javax.swing.JOptionPane`.
   - W3C. (2022). *CSS Color Module Level 4* – Named colors.
