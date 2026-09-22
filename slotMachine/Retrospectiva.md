# Retrospectiva – Proyecto inicial slotMachine (ciclos 1 y 2)

> Completar los campos marcados con `<...>` con los datos reales del equipo.

## Ciclo 1

### 1. Mini-ciclos definidos y justificación
| Mini-ciclo | Contenido | Justificación |
|---|---|---|
| MC1 | Crear máquina, visible/invisible, exit, `ok()` | Base para ver y probar todo lo demás |
| MC2 | Agregar / eliminar ruedas | Sin ruedas no hay nada que mostrar |
| MC3 | Agregar / eliminar símbolos (colores CSS únicos) | Las ruedas necesitan símbolos |
| MC4 | `placeSymbol`, `spin(wheel)`, `spin()` | Movimiento de las ruedas |
| MC5 | `symbols`, `distinctSymbols`, `configuration`, `isJackpot` + aspecto ganador | Consultas sobre el estado |
| MC6 | Mensajes con JOptionPane, documentación, diseño en astah | Usabilidad y entrega |

### 2. Estado actual
Todos los mini-ciclos terminados. <ajustar si algo quedó pendiente>

### 3. Tiempo invertido (Horas/Hombre)
- <Integrante 1>: <h>
- <Integrante 2>: <h>

### 4–8
- Mayor logro: <...>
- Mayor problema técnico: <...> (p.ej. el orden de dibujo del Canvas: al cambiar un color la figura queda encima; se resolvió redibujando toda la máquina sólo cuando cambia el estado ganador).
- Trabajo en equipo / compromisos: <...>
- Práctica XP más útil: <...> (p.ej. pruebas de unidad primero / programación en parejas)
- Referencias: <...> (p.ej. W3C, *CSS Color Module Level 4*, https://www.w3.org/TR/css-color-4/#named-colors; Oracle, *Java SE API – JOptionPane*)

## Ciclo 2

### 1. Mini-ciclos definidos y justificación
| Mini-ciclo | Contenido | Justificación |
|---|---|---|
| MC1 | Pruebas de unidad de ciclo 1 (`SlotMachineC2Test`) en modo invisible | Red de seguridad antes del refactoring |
| MC2 | Refactoring: clase `Wheel` dueña de sus símbolos y su dibujo | Extensibilidad, métodos cortos |
| MC3 | `swap`, `lock`, `unlock` (+ marca visual de rueda fijada) | Requisitos 9 y 10 |
| MC4 | `spin(wheel, steps)` animado paso a paso | Requisito 11 y usabilidad |
| MC5 | `spin(setSymbols)` atómico | Requisito 12 |
| MC6 | `SlotMachineCC2Test`, pruebas de aceptación, astah, retrospectiva | Entrega |

### 2. Estado actual
Todos los mini-ciclos terminados; 37 pruebas de unidad pasan. <ajustar>

### 3. Tiempo invertido (Horas/Hombre)
- <Integrante 1>: <h>
- <Integrante 2>: <h>

### 4–8
- Mayor logro: <...>
- Mayor problema técnico: <...>
- Trabajo en equipo / compromisos: <...>
- Práctica XP más útil: <...>
- Referencias: <...>
