# Sokoban

Trabajo Integrador — **Proceso de Desarrollo de Software** (Prof. Jonathan Pepe) — Fundación UADE.

Implementación en **Java 21 + Swing** del clásico juego de lógica *Sokoban*, construida como
ejercicio de aplicación de principios y patrones de diseño (**SOLID, GRASP, MVC, GoF**).

---

## Integrante

| Apellido y Nombre | Legajo | Mail |
|---|---|---|
| Abdo Ferez, Federico | 1194765 | fabdoferez@uade.edu.ar |

**Equipo de desarrollo:** Abdo Ferez, Federico — entrega individual (1 integrante).

---

## Requisitos previos

| Herramienta | Versión mínima | Cómo verificar |
|---|---|---|
| JDK | 21 | `java -version` |
| Maven | 3.9 (o el wrapper incluido) | `mvn -version` |

> No hace falta instalar Maven: el repositorio incluye el *Maven Wrapper*
> (`mvnw` / `mvnw.cmd`). La interfaz usa **Swing**, que forma parte del JDK, así que
> no hay que descargar SDKs gráficos adicionales.

---

## Cómo ejecutar el juego

### Opción 1 — Desde la línea de comandos (recomendada)

**Windows (PowerShell o CMD):**

```bat
.\mvnw.cmd clean package
java -jar target\sokoban-1.0.jar
```

**Linux / macOS:**

```bash
./mvnw clean package
java -jar target/sokoban-1.0.jar
```

### Opción 2 — Sin empaquetar

```bash
./mvnw clean compile exec:java
```

### Opción 3 — Desde el IDE

Importar el proyecto como **proyecto Maven** y ejecutar la clase
`com.sokoban.Main` (`src/main/java/com/sokoban/Main.java`).

### Ejecutar las pruebas

```bash
./mvnw test
```

---

## Controles

| Tecla / Botón | Acción |
|---|---|
| ← ↑ → ↓ | Mover al Sokoban (empuja la caja que tenga adelante) |
| Botón **Deshacer** / `U` | Deshacer movimientos (ver reglas abajo) |
| Botón **Reiniciar** / `R` | Reiniciar el nivel actual desde cero |
| `Esc` | Volver al menú principal |

El jugador **solo empuja** cajas, nunca tira de ellas, no puede empujar más de una caja
a la vez, ni atravesar cajas, paredes o muros cerrados.

---

## Reglas del juego

### Tipos de caja

| Caja | Regla |
|---|---|
| **Normal** | Se empuja libremente. Sobre hielo se desliza hasta chocar o salir del hielo. |
| **Frágil** | Mismas reglas, pero aguanta una cantidad limitada de empujes. Al agotarse, se rompe. |
| **Llave** | Mismas reglas; al llegar a un *casillero cerrojo* abre todos los muros de su grupo. |

### Elementos del tablero

| Elemento | Comportamiento |
|---|---|
| Pared / espacio vacío | Bloquean el paso de jugador y cajas. |
| Casilla de destino | Posición final de una caja. Cada destino acepta un tipo de caja. |
| Terreno resbaladizo (hielo) | Quien lo pisa se desliza en la misma dirección hasta chocar o salir del hielo. |
| Casillero cerrojo | Al recibir la caja llave de su grupo, abre los muros de ese grupo. |
| Muro abierto / cerrado | Cerrado se comporta como pared; abierto deja pasar a todos. |

### Deshacer

El botón **Deshacer** restaura el estado del nivel **5 movimientos hacia atrás**,
con un máximo de **3 usos por nivel** (15 movimientos en total).
Deshacer restaura posición del jugador, posición y estado de las cajas (incluida la
resistencia de las frágiles), muros, casilleros cerrojo y el mapa de visibilidad.
Cada uso penaliza el puntaje final.

### Victoria

Se gana el nivel cuando **todas las cajas están en su destino correspondiente**,
sin importar dónde quede el jugador. Al ganar se avanza automáticamente al siguiente nivel.

### Puntaje

Al terminar cada nivel se muestra un resumen con movimientos, empujes, usos de
*deshacer* y el puntaje final. El criterio definido por el equipo es:

| Concepto | Puntos |
|---|---|
| Segundo restante del tiempo límite | **+1** c/u |
| Caja guardada en su destino | **+100** c/u |
| Muro abierto con caja llave | **+100** c/u |
| Empuje realizado | **−1** c/u |
| Uso del botón deshacer | **−250** c/u |

El puntaje se traduce en estrellas comparándolo contra el objetivo del nivel:
≤ 50 % → ★, ≤ 85 % → ★★, > 85 % → ★★★.

---

## Funcionalidades adicionales

Además de los requisitos obligatorios, el juego incorpora:

1. **Portales de teletransporte** — pares de portales que trasladan al jugador o a una
   caja al otro extremo del mapa. Implementado con el patrón *Strategy*
   (`EntryEffect` → `TeleportEffect`), el mismo mecanismo que resuelve el hielo.
2. **Niebla de guerra (mapa de visibilidad)** — solo se dibuja la zona del almacén
   alcanzable desde el jugador; abrir un muro revela el área nueva. Implementado con
   `VisibilityMap`, recalculado por eventos de dominio (*Observer*).

Como extras de diseño también se incluyen **condiciones de derrota** configurables
(*deadlock*, tiempo agotado, caja rota, caja en destino equivocado, caja llave guardada
antes de usarse), implementadas como *Strategy* (`LossCondition`), y **persistencia del
progreso** entre sesiones a través de un puerto (`ProgressRepository`).

---

## Formato de los niveles

Los niveles son archivos de texto en `src/main/resources/levels/nivelNN.txt`.
Cada archivo tiene un encabezado, el separador `---` y el mapa en caracteres:

```
nombre: Almacén central
tiempo: 120
objetivo: 1000
---
#########
#@$ ~ . #
#  K  ; #
#   a   #
####A####
#       #
#########
```

### Leyenda de caracteres

| Char | Elemento | Char | Elemento |
|---|---|---|---|
| `#` | Pared | `.` | Destino de caja normal |
| `%` | Vacío inaccesible | `:` | Destino de caja frágil |
| *(espacio)* | Piso | `;` | Destino de caja llave |
| `@` | Jugador (Sokoban) | `~` | Hielo (resbaladizo) |
| `$` | Caja normal | `a`–`e` | Casillero cerrojo del grupo A–E |
| `1`–`9` | Caja frágil (empujes que aguanta) | `A`–`E` | Muro del grupo A–E |
| `K` | Caja llave | `P`–`S` | Portal (cada letra aparece 2 veces) |

El cargador valida el archivo e informa la línea exacta del error: debe haber
exactamente un jugador, tantas cajas de cada tipo como destinos de ese tipo, cada
casillero cerrojo con su muro (y viceversa), cada letra de portal exactamente dos
veces, el borde cerrado y ningún carácter desconocido.

---

## Estructura del proyecto

```
src/main/java/com/sokoban/
├── Main.java                 # punto de entrada
├── model/                    # MODELO — reglas del juego, sin dependencias de UI
│   ├── board/                #   tablero, celdas, posiciones, direcciones
│   ├── box/                  #   jugador y jerarquía de cajas (Template Method)
│   ├── tile/                 #   jerarquía de baldosas y sus efectos (Strategy)
│   ├── game/                 #   Level, Game, estados, derrotas, puntaje, eventos
│   └── history/              #   deshacer (Command + Memento)
├── view/                     # VISTA — Swing: ventana, tablero, HUD, menú, resumen
├── controller/               # CONTROLADOR — teclado, HUD, reloj
├── factory/                  # carga de niveles (Factory Method + Builder)
├── port/                     # interfaces de salida (DIP)
├── service/                  # adaptadores: sonido, persistencia
└── exception/

src/main/resources/
├── levels/                   # nivel01.txt … nivel05.txt
└── assets/
    ├── images/               # sprites (dominio público)
    └── audio/                # efectos de sonido (dominio público)

src/test/java/                # suite JUnit 5
docs/                         # consigna, informe técnico y diagrama de clases
```

---

## Patrones de diseño aplicados

| Patrón | Dónde | Para qué |
|---|---|---|
| **MVC** | `model/` · `view/` · `controller/` | El modelo no conoce Swing; la vista se actualiza por eventos. |
| **Factory Method** | `factory.CellCreator` y sus implementaciones | Cada carácter del `.txt` sabe qué baldosa y ocupante crear. |
| **Builder** | `factory.LevelBuilder` (director: `LevelLoader`) | Arma y valida el `Level` paso a paso. |
| **Template Method** | `model.box.Box` (`push`, `storeAt` + hooks) | El esqueleto del empuje es fijo; cada caja redefine su regla. |
| **Strategy** | `EntryEffect`, `LossCondition`, `ScoreCalculator` | Hielo/portal, derrotas y puntaje intercambiables sin tocar el tablero. |
| **State** | `model.game.state.GameState` y subclases | Menú / jugando / ganado / perdido / bloqueado / juego completo. |
| **Observer** | `GameListener`, `GameEvent` | La vista, el sonido y la visibilidad reaccionan sin acoplarse al modelo. |
| **Command + Memento** | `MoveCommand`, `LevelMemento`, `UndoHistory` | Historial acotado para deshacer. |
| **Flyweight** | `view.SpriteCatalog` | Cada sprite se carga una sola vez y se comparte. |
| **Facade** | `model.game.Game` | Única puerta de entrada del controlador al modelo. |
| **Repository / DIP** | `port.ProgressRepository` ← `service.FileProgressRepository` | El modelo depende de la abstracción, no del archivo. |

El detalle, con la justificación de cada decisión, está en el **informe técnico**
(`docs/informe-tecnico.pdf`) y en el **diagrama de clases** (`docs/diagrama-clases.mdj`,
StarUML).

---

## Créditos de assets

Todos los sprites y efectos de sonido utilizados son de **dominio público / CC0**.
El detalle de cada fuente está en `src/main/resources/assets/CREDITS.md`.

---

## Uso de herramientas de IA

Se declara, conforme a la consigna, el uso de herramientas de inteligencia artificial
como apoyo durante el desarrollo. El alcance concreto (qué se generó, qué se adaptó y
qué se escribió a mano) está documentado en el informe técnico. El código entregado fue
revisado, comprendido y validado en su totalidad por el autor.
