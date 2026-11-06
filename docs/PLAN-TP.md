# Sokoban — Análisis de estado, patrones y plan de trabajo

> Documento de trabajo interno (no es entregable). Fecha: 2026-11-06.
> Consigna de referencia: `docs/PDS - TI - Prof Pepe.pdf`.

---

# 1. Análisis del estado de la carpeta

## 1.1 Resumen ejecutivo

El proyecto es un **esqueleto arquitectónico muy bien pensado pero mayormente sin
implementar**. La estructura de paquetes, la jerarquía de clases y —sobre todo— la
suite de tests son excelentes: los tests funcionan como especificación ejecutable de
todo el TP. Lo que falta es el relleno.

**Baseline inicial** (`./mvnw test`, 2026-11-06, antes de tocar nada):

```
Tests run: 101,  Passing: 38,  Failures: 19,  Errors: 44
```

**Baseline tras cerrar las bloqueantes M1–M3** (misma fecha):

```
Tests run: 105,  Passing: 39,  Failures: 18,  Errors: 48
```

**Baseline tras el paso 0.2 y la Fase 1 completa** (misma fecha):

```
Tests run: 105,  Passing: 65,  Failures: 3,  Errors: 37
```

**Baseline tras la Fase 2 completa** (misma fecha):

```
Tests run: 105,  Failures: 0,  Errors: 0   ->  BUILD SUCCESS
```

**Baseline tras la Fase 3 (interfaz Swing)** (misma fecha):

```
Tests run: 107,  Failures: 0,  Errors: 0   ->  BUILD SUCCESS
```

Los 2 tests nuevos son `LevelSolutionTest`, que juega los 5 niveles de punta a punta
con las soluciones de `docs/soluciones-niveles.md` y verifica que el juego llegue a
`GameCompletedState`. **El juego está completo y jugable**: `java -jar
target/sokoban-1.0.jar` abre el menú, se juegan los 5 niveles con flechas, hay HUD,
botones, sonido, niebla de guerra, resumen con puntaje y progreso persistente.

**El modelo esta terminado.** No queda ni un `UnsupportedOperationException` en
`src/main`, y `./mvnw clean package` pasa con la suite entera en verde.
Lo unico vacio son las clases de `view/`, `controller/` y `service/` del sonido,
que son la Fase 3.

## 1.2 Métricas

| Métrica | Valor |
|---|---|
| Clases de producción | 90 archivos `.java` |
| Líneas de producción | 1.358 |
| Líneas de test | 1.793 |
| Clases completamente vacías (stub de 4 líneas) | **28** |
| Métodos con `throw new UnsupportedOperationException("TODO")` | 26 |
| Niveles `.txt` en `src/main/resources/levels/` | ~~**0**~~ → **5** ✅ (M2) |
| Sprites / sonidos | **0** (solo `.gitkeep`) |

## 1.3 Bloqueante #1 — El proyecto está en JavaFX, la consigna pide Swing

Es el hallazgo más grave y el más fácil de pasar por alto.

> «El proyecto debe ser desarrollado en Java utilizando **Swing** para la
> implementación de la interfaz gráfica de usuario (2D).»

Estado actual:

| Archivo | Problema |
|---|---|
| `pom.xml` | Depende de `javafx-controls`, `javafx-fxml`, `javafx-media`; plugin `javafx-maven-plugin`. |
| `src/main/java/com/sokoban/sokoban/HelloApplication.java` | `extends javafx.application.Application` |
| `src/main/java/com/sokoban/sokoban/HelloController.java` | `@FXML`, `javafx.scene.control.Label` |
| `src/main/java/com/sokoban/sokoban/Launcher.java` | `Application.launch(...)` |
| `src/main/java/module-info.java` | `requires javafx.controls; requires javafx.fxml;` |
| `src/main/resources/com/sokoban/sokoban/hello-view.fxml` | Vista FXML de ejemplo |

Todo eso es el *scaffolding* del arquetipo de IntelliJ "JavaFX Application" y **hay que
borrarlo entero**. No hay una sola línea de UI propia escrita todavía, así que el costo
de la migración es cero en términos de código perdido: es solo cambiar el `pom.xml`,
borrar 4 archivos y escribir la UI en Swing desde el principio.

## 1.4 Qué está completo y funcionando (38 tests en verde)

| Área | Archivo | Estado |
|---|---|---|
| Posiciones y direcciones | `model/board/Position.java`, `Direction.java` | ✅ completo |
| Celda | `model/board/Cell.java` | ✅ completo |
| Movimiento básico y empuje | `model/board/Board.java` | ✅ completo (sin efectos de baldosa) |
| Jerarquía de cajas | `model/box/Box.java`, `NormalBox`, `WeakBox`, `KeyBox` | ✅ completo |
| Jerarquía de baldosas | `model/tile/Tile.java`, `Wall`, `Void`, `Floor`, `Ice`, `Portal`, `StoragePoint` | ✅ estructura completa, falta comportamiento de `Ice`/`Portal`/`StoragePoint` |
| Puerta y casillero cerrojo | `model/tile/mechanism/Door.java`, `KeyTile.java` | ✅ clases completas, ❌ sin cableado entre sí |
| Parseo de niveles | `factory/LevelLoader.java`, `LevelBuilder.java`, `*Creator.java` | ✅ parsea, ❌ valida solo 1 de 5 reglas |
| Contadores | `model/game/LevelStats.java` | ✅ completo |
| Estados (clases) | `model/game/state/*` | ✅ las 6 clases existen |
| Excepción de nivel | `exception/InvalidLevelException.java` | ✅ completo |

## 1.5 Qué falta — inventario completo

### A. Clases totalmente vacías (28)

| Paquete | Clases vacías |
|---|---|
| `controller/` | `GameClock`, `HudController`, `InputController` |
| `view/` | `BoardView`, `GameWindow`, `HudView`, `MenuView`, `Screen`, `SpriteCatalog`, `SpriteKey`, `SummaryView` |
| `model/history/` | `BoxSnapshot`, `Command`, `LevelMemento`, `MoveCommand`, `UndoHistory` |
| `model/tile/effect/` | `EntryEffect`, `NoEffect`, `SlideEffect`, `TeleportEffect` |
| `model/tile/mechanism/` | `DoorGroup`, `KeyTileListener` |
| `service/` | `ProgressSaver`, `SoundEffect`, `SoundService` |
| `model/tile/` | `Floor`, `Ice` (vacías por diseño, correcto) |

**Toda la capa de Vista y Controlador está vacía. Todo el mecanismo de deshacer está
vacío. Todo el sistema de efectos de baldosa está vacío.**

### B. Métodos `TODO` (26)

| Clase | Métodos sin implementar |
|---|---|
| `model/game/Game.java` | `getState`, `getCurrentLevel`, `getCurrentLevelNumber`, `start`, `move`, `undo`, `restart`, `nextLevel`, `isInputBlocked` — **9 de 9: la clase entera** |
| `model/game/Level.java` | `undo`, `getRemainingUndos`, `isWon`, `getLossReason`, `getVisibilityMap`, `addListener`, `elapseSeconds`, `getRemainingSeconds` (8) |
| `model/board/VisibilityMap.java` | `isVisible`, `recalculate` (2) |
| `model/game/loss/*` | `isMet` en las 5 condiciones (5) |
| `model/game/score/StandardScoreCalculator.java` | `calculate` (1) |
| `service/FileProgressRepository.java` | `save`, `load` (2) |

### C. Recursos faltantes

- `src/main/resources/levels/nivel01.txt` … `nivel05.txt` → **no existen**.
  `LevelLoaderTest.realLevelsLoad` falla por esto.
- Sprites en `assets/images/` → **ninguno**. La consigna exige que *todos* los
  elementos del tablero se representen con imágenes de dominio público.
- Sonidos en `assets/audio/` → **ninguno**. La consigna exige efectos de sonido.

### D. Entregables documentales faltantes

| Entregable | Estado |
|---|---|
| Repositorio público de GitHub | ⚠️ la carpeta **no es un repo git** todavía (`git init` pendiente) |
| README con instrucciones | ✅ generado en esta sesión |
| Video gameplay | ❌ pendiente (requiere el juego andando) |
| Diagrama de clases en StarUML | ❌ pendiente (ver sección 7) |
| Informe técnico | ❌ pendiente |

## 1.6 Hallazgos de diseño que hay que decidir antes de implementar

Estos no son bugs: son **ambigüedades reales** donde el código, los tests y la consigna
no coinciden. Hay que resolverlas y poder defenderlas oralmente.

### ✅ H1 — RESUELTO (M3): la regla de *deshacer* de los tests contradecía la consigna

> **Consigna:** «Se permite deshacer los **últimos 15 movimientos**… cada vez que se
> presione, restaure el estado del nivel **5 movimientos** hacia atrás, hasta un
> máximo de **tres usos consecutivos**.»

> **`UndoTest.onlyThreeUndos` (viejo):** 4 movimientos a la derecha, 3 undos, y esperaba
> que el jugador quedara en `(1,2)` → eso era **1 movimiento por undo**, no 5.

Se cambió el test, no la consigna. La regla implementada es:

| Constante | Valor | De dónde sale |
|---|---|---|
| `STEPS_PER_UNDO` | 5 | «restaure … 5 movimientos hacia atrás» |
| `MAX_CONSECUTIVE_UNDOS` | 3 | «máximo de tres usos consecutivos» |
| `HISTORY_SIZE` | 15 | «los últimos 15 movimientos» = 5 × 3 |

**Decisión a defender — qué significa «consecutivos».** Hay dos lecturas:

- **(a)** 3 undos *por nivel*, en total.
- **(b)** 3 undos *seguidos*; al volver a mover, el contador se reinicia.

Se eligió **(b)**, que es la lectura literal y además la única internamente coherente:
la ventana de 15 movimientos es exactamente 3 undos de 5, así que tras el tercero ya no
queda historial; al jugar más movimientos la ventana se desliza y el botón vuelve a
habilitarse. `getRemainingUndos()` pasa a significar «usos **consecutivos** restantes».
Está cubierto por `UndoTest.undosRefillAfterMoving`.

> Si en la defensa se prefiere la lectura (a), es un cambio de una línea en
> `UndoHistory` (no reiniciar el contador al mover) más borrar ese único test.

### ✅ H2 — RESUELTO (Fase 1, paso 3): la casilla de destino vacía **es** un agujero

`MovementTest.cannotStepOnEmptyStoragePoint` espera `BLOCKED` al pisar un `.` vacío, y
`canWalkOverStoredBox` espera que con una caja encima sea transitable («puente»).
Se eligió **mantener la variante** (opción (a) del análisis original): suma creatividad
y ya estaba en los tests.

**La solución salió más limpia de lo previsto: no hizo falta cambiar la firma de
`Tile.allowsPlayer()`.** La clave es que **el destino se queda con la caja**: cuando
`StoragePoint` acepta una caja, la guarda en su propio campo `storedBox` y el tablero
la saca del lugar de ocupante de la celda. Entonces:

- `StoragePoint.allowsPlayer()` → `isFilled()`: vacío bloquea, lleno es puente.
- `StoragePoint.allowsBox(box)` → `!isFilled()`: no se apila una caja sobre otra guardada.
- La celda queda libre, así que el jugador puede pararse encima de la caja guardada
  (`Cell` tiene un solo lugar de ocupante, y por eso los dos no podían coexistir ahí).
- `Board.getBoxAt(p)` devuelve el ocupante si es caja, y si no, `tile.getStoredBox()`.
- Una caja guardada **no se puede volver a empujar**, que es justo la semántica de
  «el destino es un agujero».

Como efecto colateral, la victoria se vuelve trivial y polimórfica:
`Tile.isPendingStorage()` (false por defecto, `storedBox == null` en `StoragePoint`) y
`Board.isComplete()` recorre las celdas sin un solo `instanceof`.

### ✅ H3 — RESUELTO (Fase 1, paso 3): `StoragePoint.receive()` ya está cableado

Se agregó `Tile.receiveBox(Box): LandingOutcome`, que el tablero invoca sobre la baldosa
donde la caja termina su movimiento. La baldosa actualiza su propio estado y devuelve la
consecuencia; el tablero aplica lo que la baldosa no puede hacer sola (sacar la caja de
la celda si quedó guardada, o reemplazar la baldosa si era un casillero cerrojo).

`LandingOutcome` = `NONE | STORED | WRONG_STORAGE | KEY_NOT_USED | DOOR_OPENED`.
Con eso funcionan la victoria, `WRONG_STORAGE` y `EARLY_KEY_STORAGE`.

### ✅ H4 — RESUELTO (Fase 1, paso 2): puerta y casillero cerrojo conectados

`KeyTile` pasó a ser el *Subject* del Observer (`addListener`, y notifica en
`receiveBox` cuando la caja que llega devuelve `opensDoors() == true`).
`DoorGroup implements KeyTileListener` junta todos los muros de una letra y los abre de
una sola vez. `LevelBuilder.wireDoorGroups()` arma los grupos y suscribe cada casillero.

🔑 **Para defender:** `Door` y `KeyTile` **no se conocen entre sí**; se hablan a través
del grupo. Y la caja nunca se castea: `Box.opensDoors()` y `Box.markDoorOpened()` son
hooks con implementación vacía en la clase base que solo `KeyBox` redefine.

### 🟡 H5 — `GameEvent` no transporta datos

`GameEvent` solo tiene un `EventType`. La vista no puede animar el movimiento ni el
`SoundService` elegir el sonido correcto sin saber *qué* caja, *dónde* y *en qué
dirección*. Hay que enriquecerlo.

### 🟡 H6 — `Game` recibe `IntFunction<Level>`, que no puede lanzar excepción

`LevelLoader.load()` lanza `InvalidLevelException` (checked), pero `IntFunction` no
declara excepciones. El adaptador va a tener que envolverla en una no-checked, o hay
que definir una interfaz propia `LevelSource { Level load(int n) throws ... }`.

### 🟢 H7 — Varios detalles menores

| # | Detalle | Acción |
|---|---|---|
| a | ✅ `model.tile.Void` colisionaba con `java.lang.Void` | renombrado a `Empty` (paso 0.2) |
| b | ✅ `NoEffect`, `SlideEffect`, `TeleportEffect` no implementaban `EntryEffect` | interfaz definida e implementada (paso 1) |
| c | ✅ Paquetes raíz sueltos sin `com.sokoban` | todo bajo `com.sokoban.*` (paso 0.2) |
| d | ✅ `Player.facing` era `final` | ahora mutable, con `face(Direction)` para el sprite |
| e | ✅ `module-info.java` solo exportaba `com.sokoban.sokoban` | borrado al migrar a Swing (M1) |
| f | `Board.getCols()` asume matriz rectangular | está garantizado por `LevelBuilder`; documentarlo |
| g | `Level` acumula 7 responsabilidades | ver sección 5, paso 9 |
| h | `.gitignore` ignora `.idea/` pero `.idea/` está en el disco | correcto, no tocar |

### ✅ H9 — RESUELTO: `service/ProgressSaver` ya tiene uso

Se implementó como `GameListener`: al recibir `LEVEL_WON` persiste el progreso a
través del puerto `ProgressRepository`. Así `Game` no tiene que acordarse de guardar,
igual que no se acuerda de hacer sonar nada.

### 🟡 H8 — Limitación conocida: empuje + teletransporte del jugador

En `Board.movePlayer` la caja se empuja **antes** de resolver el efecto de la casilla a
la que entra el jugador, porque el deslizamiento necesita que la caja ya se haya corrido.
Si el jugador empuja una caja que está parada **sobre un portal** y la salida de ese
portal está ocupada, el movimiento devuelve `BLOCKED` con la caja ya movida.

Requiere que una caja haya quedado sobre un portal (solo pasa si antes se teletransportó)
y que la salida esté bloqueada. Ningún nivel ni ningún test lo provoca. Cuando esté el
Memento (paso 5) el estado es recuperable con undo. Queda anotado para la defensa.

---

# 2. Patrones de diseño: inventario y ubicación

Leyenda de estado: ✅ implementado · 🟡 estructura presente, lógica faltante · ❌ solo el nombre del archivo

## 2.1 Patrones GoF

| # | Patrón | Ubicación exacta | Rol de cada clase | Estado |
|---|---|---|---|---|
| 1 | **Factory Method** | `factory/CellCreator.java` | *Creator* abstracto | ✅ |
| | | `factory/WallCreator.java`, `DoorCreator.java`, `KeyTileCreator.java`, `PortalCreator.java`, `WeakBoxCreator.java` | *ConcreteCreator* | ✅ |
| | | `factory/CellContent.java` | *Product* (record `tile` + `occupant`) | ✅ |
| | | `factory/LevelLoader.java:221-244` — `Map<Character, CellCreator>` | registro char → fábrica (también *Indirection*) | ✅ |
| 2 | **Builder** | `factory/LevelBuilder.java` | *Builder* (`withHeader` → `addCell` → `build`), valida las 5 reglas y aparea portales y grupos de muros | ✅ |
| | | `factory/LevelLoader.java` | *Director* | ✅ |
| 3 | **Template Method** | `model/box/Box.java:561-580` | `push()` y `storeAt()` son `final`; hooks `onPushed()` / `onStored()` | ✅ |
| | | `model/box/WeakBox.java` (`onPushed`), `model/box/KeyBox.java` (`onStored`) | *ConcreteClass* | ✅ |
| 4 | **State** | `model/game/state/GameState.java` | *State*, con `onMove` / `onUndo` / `onNextLevel` y default methods | ✅ |
| | | `MenuState`, `PlayingState`, `WonState`, `LostState`, `BlockedState`, `GameCompletedState` | *ConcreteState*: cada uno define sus transiciones | ✅ |
| | | `model/game/Game.java` | *Context*: delega y reemplaza su estado, sin un solo `if` | ✅ |
| 5 | **Observer** | `model/game/event/GameListener.java` | *Observer* | ✅ interfaz |
| | | `model/game/event/GameEvent.java`, `EventType.java` | mensaje con `Position` y `Direction` | ✅ |
| | | `model/game/Level.java` — `addListener()` / `publish()` | *Subject* | ✅ |
| | | `model/tile/mechanism/KeyTileListener.java` ← `DoorGroup` | *Observer* del cerrojo; `KeyTile` es el *Subject* | ✅ |
| 6 | **Memento** | `model/history/LevelMemento.java` | *Memento*: celdas, muros, destinos y cajas | ✅ |
| | | `model/history/BoxSnapshot.java` | estado interno de una caja (resistencia, llave usada) | ✅ |
| | | `model/history/UndoHistory.java` | *Caretaker*: ventana de 15, 5 por uso, 3 usos | ✅ |
| | | `model/game/Level.java` | *Originator* | ✅ |
| 7 | **Command** | `model/history/Command.java` | *Command* | ✅ |
| | | `model/history/MoveCommand.java` | *ConcreteCommand*. Ojo: el undo **no** invierte comandos, restaura un Memento | ✅ |
| 8 | **Strategy** (a) puntaje | `model/game/score/ScoreCalculator.java` | *Strategy* | ✅ interfaz |
| | | `model/game/score/StandardScoreCalculator.java` | *ConcreteStrategy* | ✅ |
| | | `model/game/score/ScoreResult.java` | resultado (`score` + `stars`) | ✅ |
| 9 | **Strategy** (b) derrota | `model/game/loss/LossCondition.java` | *Strategy* | ✅ interfaz |
| | | `DeadlockCondition`, `TimeOutCondition`, `BrokenBoxCondition`, `WrongStorageCondition`, `EarlyKeyStorageCondition` | *ConcreteStrategy* ×5, evaluadas en orden por `Level` | ✅ |
| 10 | **Strategy** (c) efecto de baldosa | `model/tile/effect/EntryEffect.java` | *Strategy*, aplicado por `Board` vía `Tile.getEntryEffect()` | ✅ |
| | | `NoEffect`, `SlideEffect`, `TeleportEffect` | *ConcreteStrategy* ×3. `SlideEffect` no itera: delega en el efecto de la celda siguiente y `NoEffect` corta la recursión | ✅ |
| 11 | **Flyweight** | `view/SpriteCatalog.java`, `view/SpriteKey.java` | *FlyweightFactory* + clave | ❌ vacías |
| 12 | **Facade** | `model/game/Game.java` | única fachada del modelo hacia el controlador | ✅ |
| 13 | **Repository + Adapter** | `port/ProgressRepository.java` (interfaz), `port/Progress.java` | *puerto* | ✅ |
| | | `service/FileProgressRepository.java` | *adaptador* a archivo | ✅ |
| 14 | **Polimorfismo de baldosas** | `model/tile/Tile.java` + 8 subclases | `allowsPlayer()`, `allowsBox()`, `getEntryEffect()`, `receiveBox()`, `isPendingStorage()` polimórficos | ✅ |
| 15 | **Double Dispatch** | `model/box/Occupant.canEnter(Tile)` ← `Player` / `Box` | Evita que `Board` pregunte «¿sos caja o jugador?»: el ocupante le pregunta a la baldosa lo que le corresponde | ✅ |

## 2.2 Patrones GRASP

| Patrón | Dónde se ve | Evidencia |
|---|---|---|
| **Information Expert** | `model/board/Board.java:356-358` (javadoc explícito) | El tablero conoce todas las celdas → valida y ejecuta movimientos. |
| | `model/box/Box.java` | La caja sabe si aguanta el empuje. |
| | `model/tile/Tile.java` | La baldosa sabe a quién deja pasar. |
| | `model/game/LevelStats.java` | Los contadores viven con quien los conoce. |
| **Creator** | `factory/LevelBuilder.java:140` (javadoc explícito) | Crea `Cell`, `Board` y `Level` porque agrega sus partes. |
| **Polymorphism** | `model/tile/Tile.java:1201-1204` (javadoc explícito) | No hay un solo `switch` sobre tipo de baldosa ni de caja. |
| **Pure Fabrication** | `factory/LevelLoader`, `model/history/UndoHistory`, `service/SoundService`, `service/FileProgressRepository`, `model/game/score/ScoreCalculator` | Clases que no son del dominio, creadas para bajar acoplamiento. |
| **Indirection** | `factory/LevelLoader.creators` (Map char→creator) | Desacopla el formato del `.txt` del modelo. |
| | `port/ProgressRepository` | Desacopla el modelo del sistema de archivos. |
| **Protected Variations** | `CellCreator`, `LossCondition`, `EntryEffect`, `ScoreCalculator` | Puntos de variación aislados tras interfaces. |
| **Low Coupling** | Paquetes `model` / `view` / `controller` / `port` | El modelo no importa nada de UI. |
| **High Cohesion** | Un paquete por concepto (`board`, `box`, `tile`, `history`, `loss`, `score`, `state`) | — |
| **Controller** | `controller/InputController`, `controller/HudController` | ❌ vacíos todavía. |

## 2.3 Principios SOLID

| Principio | Cómo se cumple hoy | Dónde |
|---|---|---|
| **SRP** | Separación por paquete muy limpia. ⚠️ `Level` tiende a God class (mover + deshacer + victoria + derrota + tiempo + eventos + visibilidad). | `model/game/Level.java` |
| **OCP** | Agregar un carácter al `.txt` = registrar un `CellCreator`, sin tocar el loader. Agregar una derrota = agregar un `LossCondition`. | `LevelLoader:221`, `loss/` |
| **LSP** | Las subclases de `Tile` solo restringen vía booleanos; las de `Box` respetan el contrato de `push()`. Ninguna lanza excepciones nuevas. | `tile/`, `box/` |
| **ISP** | `Occupant` tiene **un** método. `GameListener`, `CellCreator`, `LossCondition`, `ScoreCalculator`, `ProgressRepository` son todas interfaces mínimas. | varios |
| **DIP** | `Game` depende de `IntFunction<Level>`, no de `LevelLoader`. `port.ProgressRepository` ← `service.FileProgressRepository`. | `Game.java:701`, `port/` |

## 2.4 MVC

| Capa | Paquete | Estado |
|---|---|---|
| Modelo | `model/`, `factory/`, `port/` | 🟡 parcial, pero **sin ninguna dependencia de UI** ✅ |
| Vista | `view/` | ❌ 8 clases vacías |
| Controlador | `controller/` | ❌ 3 clases vacías |

El modelo está correctamente aislado: no hay un solo `import javafx.*` ni `javax.swing.*`
dentro de `model/`. Eso es lo que hace que la migración a Swing sea barata.

---

# 3. Modificaciones propuestas

Priorizadas. 🔴 bloqueante · 🟠 necesaria para aprobar · 🟡 mejora la nota

| # | Prio | Modificación | Archivos |
|---|---|---|---|
| ~~M1~~ | ✅ | **Migrado de JavaFX a Swing.** `pom.xml` sin ninguna dependencia gráfica externa (`artifactId` → `sokoban`, versión `1.0`, `maven-jar-plugin` con `Main-Class` y `exec-maven-plugin`). Borrados el paquete `com/sokoban/sokoban/`, `module-info.java` y `hello-view.fxml`. Nuevo punto de entrada `com.sokoban.Main` (bootstrap Swing en el EDT). | `pom.xml`, `com/sokoban/Main.java` |
| ~~M2~~ | ✅ | **Creados los 5 niveles** `nivel01.txt`…`nivel05.txt`, con dificultad creciente y validados contra las reglas de M7. Soluciones en `docs/soluciones-niveles.md`. | `src/main/resources/levels/` |
| ~~M3~~ | ✅ | **`UndoTest` reescrito a la regla de la consigna** (5 movimientos por undo, 3 usos consecutivos, historial de 15). Pasó de 6 a 10 casos. Ver H1. | `src/test/java/model/history/UndoTest.java` |
| ~~M4~~ | ✅ | **HECHO. Conectar `StoragePoint` con el tablero**: que `Board`/`Level` pregunte a la baldosa destino si acepta la caja (`receive`). Sin esto no hay victoria ni derrotas por guardado. Ver H3. | `Board.java`, `Level.java` |
| ~~M5~~ | ✅ | **HECHO. Implementar `DoorGroup` + `KeyTileListener`**: registro `grupo → List<Door>`, construido por `LevelBuilder`. Al entrar una `KeyBox` en un `KeyTile`, abre el grupo, marca `markDoorOpened()`, consume la baldosa y emite `DOOR_OPENED`. Ver H4. | `DoorGroup.java`, `KeyTileListener.java`, `LevelBuilder.java`, `Level.java` |
| ~~M6~~ | ✅ | **HECHO. Definir `EntryEffect` de verdad**: `EntryEffect { Position applyTo(Board b, Occupant o, Position landing, Direction d); }` e implementarlo en `NoEffect`, `SlideEffect` (hielo), `TeleportEffect` (portal). `Tile` expone `getEntryEffect()`. `Board` lo aplica tras cada entrada. Ver H7b. | `tile/effect/*`, `Tile.java`, `Ice.java`, `Portal.java`, `Board.java` |
| ~~M7~~ | ✅ | **HECHO. Completar las 5 validaciones de `LevelBuilder`**: cajas vs destinos por tipo, cerrojo ↔ muro, portales pareados, borde cerrado, un solo jugador (ya está). | `LevelBuilder.java` |
| ~~M8~~ | ✅ | **HECHO. Implementar `Game` completo** (9 métodos) con el patrón State real. | `Game.java` |
| ~~M9~~ | ✅ | **HECHO. Implementar `Level`**: `undo`, `isWon`, `getLossReason`, `addListener`, tiempo, visibilidad. | `Level.java` |
| ~~M10~~ | ✅ | **HECHO. Implementar Command + Memento** para deshacer. | `history/*` |
| ~~M11~~ | ✅ | **HECHO. Implementar toda la vista Swing y los controladores.** | `view/*`, `controller/*` |
| ~~M12~~ | ✅ | **HECHO. 21 sprites PNG y 9 efectos WAV**, generados programáticamente: obra propia del equipo, CC0. Documentados en `assets/CREDITS.md`, con instrucciones para reemplazarlos por arte de terceros si se quiere. | `resources/assets/` |
| ~~M13~~ | ✅ | **HECHO. Enriquecer `GameEvent`** con `Position` y `Direction`. Ver H5. | `event/GameEvent.java` |
| ~~M14~~ | ✅ | **HECHO. Dar comportamiento real a los `GameState`**: `onMove`, `onUndo`, `onNextLevel` con default methods. `Game` no tiene un solo `if` sobre el estado. | `state/*`, `Game.java` |
| M15 | 🟡 | **Reemplazar `IntFunction<Level>` por una interfaz propia `LevelSource`** que declare `InvalidLevelException`. Ver H6. | `Game.java`, `port/LevelSource.java` |
| ~~M16~~ | ✅ | **HECHO. Mover todos los paquetes bajo `com.sokoban.*`** y renombrar `Void` → `Empty`. | todo el árbol |
| M17 | 🟡 | **Parcial. Extraer de `Level`** las responsabilidades de tiempo y de visibilidad a colaboradores (`LevelTimer`, el `VisibilityMap` como listener). Evita la God class. Ver H7g. | `Level.java` |
| M18 | 🟡 | **`git init` + repositorio público en GitHub.** La carpeta no está versionada. | — |

---

# 4. README

Generado en la raíz del repositorio: `README.md`.

> ⚠️ Está escrito contra el **estado final** del TP (Swing, 5 niveles, artefacto
> `sokoban-1.0.jar`). Antes de entregar hay que verificar que los comandos de la
> sección «Cómo ejecutar» coincidan con el `pom.xml` final, y completar los datos
> del integrante y el nombre del equipo.

---

# 5. Plan de trabajo sobre los patrones de diseño

Orden pensado para que **cada paso deje tests en verde** y nunca haya que volver atrás.
Los tests ya existentes son la definición de "hecho" de cada paso.

### Fase 0 — Saneamiento (sin tocar lógica) — ✅ hecha salvo el paso 0.2

**Paso 0.1 — ✅ HECHO. Migrar a Swing.**
- `pom.xml`: eliminados `javafx-controls`, `javafx-fxml`, `javafx-media` y el
  `javafx-maven-plugin`. Agregados `exec-maven-plugin` y `maven-jar-plugin` con
  `Main-Class = com.sokoban.Main` (no hace falta `maven-shade-plugin`: sin dependencias
  de runtime, un jar común alcanza). `artifactId` → `sokoban`, versión `1.0`.
- Borrados: `src/main/java/com/sokoban/sokoban/` (3 clases), `src/main/java/module-info.java`,
  `src/main/resources/com/sokoban/sokoban/hello-view.fxml`.
- Agregado `com.sokoban.Main`: bootstrap Swing dentro del EDT, con una ventana
  provisoria que hay que reemplazar por `view.GameWindow` en el paso 11 de la UI.
- ✅ *Verificado:* no queda ninguna referencia a `javafx` en `src/`; `mvn package` genera
  `target/sokoban-1.0.jar` con el manifiesto correcto y los 5 niveles adentro;
  `java -jar target/sokoban-1.0.jar` abre la ventana.

**Paso 0.2 — Reubicar paquetes bajo `com.sokoban.*` y renombrar `Void` → `Empty`** (M16).
Hacerlo ahora, con 1.358 líneas, es trivial; después será doloroso.
- ✅ *Verificación:* compila y los 38 verdes se mantienen.

**Paso 0.3 — ✅ HECHO. Corregir `UndoTest`** a la regla de la consigna (M3).
Los 10 casos quedan rojos a propósito: son la especificación del paso 5 (Memento+Command).

---

### Fase 1 — Cerrar el modelo de tablero — ✅ COMPLETA

**Paso 1 — ✅ HECHO. Strategy de efectos de baldosa (M6).** *Patrón: Strategy.*

```java
public interface EntryEffect {
    /** Devuelve la posición final del ocupante que acaba de entrar en 'landing'. */
    Position resolve(Board board, Position landing, Direction direction);
}
```

- `NoEffect` → `return landing;` (valor por defecto en `Tile.getEntryEffect()`).
- `SlideEffect` → avanza mientras la celda siguiente sea entrable y esté libre.
- `TeleportEffect` → consulta al `Board` el portal pareado; si la salida está ocupada,
  devuelve `null` → `Board` traduce eso en `MoveResult.BLOCKED`.
- `Board.movePlayer` y `Board.pushBox` llaman al efecto después de posicionar.
- 🔑 *Decisión a defender:* el efecto es **del terreno**, no de la caja → va en `Tile`,
  no en `Box`. Por eso es Strategy y no otro Template Method.
- ✅ *Verde:* `IceTest` (5), `PortalTest` (5), `WinConditionTest.winIsCheckedAfterSlide`.

**Paso 2 — ✅ HECHO. Cableado puerta ↔ cerrojo (M5).** *Patrón: Observer (+ Mediator ligero).*

- `DoorGroup`: `char group` + `List<Door> doors` + `open()` que abre todas.
- `LevelBuilder` arma un `Map<Character, DoorGroup>` mientras recorre las celdas.
- `KeyTile` pasa a ser `Subject`: `addListener(KeyTileListener)`;
  `KeyTileListener { void onKeyTileActivated(char group); }`.
- Al aterrizar una `KeyBox` sobre un `KeyTile`: se notifica, el `DoorGroup` abre,
  `keyBox.markDoorOpened()`, la celda cambia su baldosa a `Floor` (baldosa consumida).
- 🔑 *Decisión:* `Door` **no** conoce a `KeyTile`; se hablan por el grupo. Bajo acoplamiento.
- ✅ *Verde:* `DoorKeyTileTest` (3 que fallan).

**Paso 3 — ✅ HECHO. Conectar el guardado (M4).**

- Tras mover/empujar, `Board` pregunta a la baldosa de llegada. Si es `StoragePoint`,
  invoca `receive(box)` y devuelve el `StorageResult` hacia arriba.
- `StoragePoint.allowsPlayer(Occupant occupant)` → resuelve H2: vacío bloquea, ocupado
  es puente. Cambia la firma en `Tile` (afecta también a `Door`, `Wall`, `Empty`).
- ✅ *Verde:* `MovementTest` (2), `StoragePointTest` (2 de nivel).

**Paso 4 — ✅ HECHO. Validaciones del Builder (M7).** *Patrón: Builder.*
Agregar a `LevelBuilder.validate()` los 4 chequeos faltantes, cada uno con su línea.
- ✅ *Verde:* `LevelLoaderTest` (4).

---

### Fase 2 — Historial y ciclo de juego — ✅ COMPLETA

**Paso 5 — ✅ HECHO. Memento + Command (M10).** *Patrones: Memento, Command.*

```java
public interface Command { void execute(); }                      // Command
public record BoxSnapshot(Position position, int remainingPushes, boolean doorOpened) {}
public final class LevelMemento { /* posición jugador, List<BoxSnapshot>, puertas abiertas,
                                     baldosas cerrojo consumidas, visibilidad, stats */ }
public class UndoHistory {                                        // Caretaker
    private final Deque<LevelMemento> mementos;   // capacidad 15
    private int remainingUndos = 3;               // 3 usos por nivel
    void push(LevelMemento m); LevelMemento undo(int steps /* = 5 */);
}
```

- `Level` es el *Originator*: `saveToMemento()` / `restoreFrom(memento)`.
- `MoveCommand implements Command`: encapsula dirección + el memento previo.
- 🔑 *Decisión a defender:* **Memento y no undo inverso**. Con hielo, portales, puertas
  que se abren y baldosas que se consumen, invertir un movimiento es irresoluble;
  restaurar un snapshot es correcto por construcción. Es el argumento fuerte del TP.
- 🔑 *Decisión:* snapshot acotado a 15 → memoria O(15 × cajas), despreciable.
- ✅ *Verde:* `UndoTest` (6, ya corregido en 0.3), `LevelStatsTest.countsDuringPlay`.

**Paso 6 — ✅ HECHO. Observer de eventos (M13).** *Patrón: Observer.*

- `Level.addListener()` + `notify(GameEvent)`.
- Enriquecer `GameEvent`: `EventType type`, `Position position`, `Direction direction`.
- Se emite en: movimiento, empuje, apertura de puerta, victoria, derrota.
- Suscriptores: `BoardView` (repintar), `HudView` (contadores), `SoundService` (audio),
  `VisibilityMap` (recalcular en `DOOR_OPENED`).
- 🔑 *Decisión:* es lo que permite que el modelo **no conozca Swing** → MVC real.
- ✅ *Verde:* `GameEventTest` (5).

**Paso 7 — ✅ HECHO. Strategies de derrota y puntaje.** *Patrón: Strategy.*

- `Level` tiene `List<LossCondition>` y, tras cada movimiento, devuelve la primera que
  se cumple. **Orden importante:** victoria se chequea *antes* que deadlock
  (`LossConditionTest` #3 lo exige).
- `DeadlockCondition`: evaluar **solo la caja empujada y sus vecinas** (test #4), buscando
  esquina no-destino y bloque 2×2.
- `StandardScoreCalculator`: `+1`/seg restante, `+100`/caja, `+100`/puerta, `−1`/empuje,
  `−250`/undo; estrellas ≤50 %→1, ≤85 %→2, resto 3.
- ✅ *Verde:* `LossConditionTest` (6), `ScoreCalculatorTest` (5), `StoragePointTest` (2).

**Paso 8 — ✅ HECHO. State real (M8, M14).** *Patrón: State.*

Convertir los estados anémicos en estados con comportamiento:

```java
public interface GameState {
    boolean acceptsInput();
    GameState onMove(Game game, Direction d);
    GameState onUndo(Game game);
    GameState onNextLevel(Game game);
}
```

- `Game` deja de tener `if`s: delega y reemplaza su estado por el devuelto.
- Transiciones: `Menu --start--> Playing`; `Playing --gana--> Won`;
  `Playing --deadlock--> Blocked`; `Playing --caja rota/tiempo--> Lost`;
  `Blocked --undo--> Playing`; `Won --next--> Playing` o `GameCompleted` tras el nivel 5.
- 🔑 *Decisión a defender:* el bloqueo del teclado es `state.acceptsInput()`, **no** un
  booleano suelto. Eso es State bien usado.
- ✅ *Verde:* `GameStateTest` (6), `WinConditionTest.inputIsBlockedAfterWinning`.

**Paso 9 — 🟡 PARCIAL. Aliviar `Level` (M17).** *Patrón: SRP + Pure Fabrication.*

Quedó a medias y a propósito:

- **El reloj sigue en `Level`** (`elapseSeconds` / `getRemainingSeconds`) porque
  `LossConditionTest.timeOutLoses` los llama ahí. En la Fase 3, `controller/GameClock`
  va a ser quien los invoque una vez por segundo; `Level` solo lleva la cuenta.
- **`VisibilityMap` no quedó como `GameListener`.** Recalcular necesita el `Board`, y
  `GameEvent` no lo lleva — pasárselo sería acoplar el evento al tablero. `Level` la
  recalcula directamente después de cada movimiento y de cada undo. Es una línea y
  evita una indirección que no aporta.
- Lo que sí se logró: `Level` delega la foto en `LevelMemento`, el historial en
  `UndoHistory`, las derrotas en la lista de `LossCondition` y el recorrido en
  `VisibilityMap`. No arma ninguna de esas cosas por su cuenta.

**Paso 10 — ✅ HECHO. Persistencia.** *Patrones: Repository, Adapter, DIP.*
`FileProgressRepository` escribe un `nivel=N` en un archivo de texto; si no existe,
`load()` devuelve `new Progress(1)`.
- ✅ *Verde:* `ProgressRepositoryTest` (2).

---

### Fase 3 — Interfaz y verificación — ✅ COMPLETA

**Paso 11 — ✅ Los 5 niveles (M2)**, con `LevelLoaderTest.realLevelsLoad` ya en verde.
Cada uno introduce un mecanismo:

| Nivel | Tamaño | Introduce | Empujes de la solución |
|---|---|---|---|
| 01 Primeros pasos | 7×8 | Cajas normales + destinos (tutorial) | 6 |
| 02 Carga frágil | 8×10 | Cajas frágiles con los empujes justos | 10 |
| 03 Pista de hielo | 9×11 | Hielo, para el jugador y para las cajas | 4 |
| 04 Bajo llave | 9×11 | Caja llave + cerrojo + muro **y niebla de guerra** | 11 |
| 05 El almacén final | 9×13 | Portales + los tres tipos de caja + muro | 12 |

> **Nota sobre la niebla:** el showcase real es el **nivel 04**, no el 05. En el 04 la
> mitad inferior solo es alcanzable cruzando el muro `A`, así que arranca oculta y se
> revela al abrirlo. En el 05 el par de portales deja ver la zona de abajo desde el
> arranque (la visibilidad atraviesa portales, por `VisibilityMapTest`), así que ahí la
> niebla no esconde nada.

> **Restricción descubierta al diseñarlos:** una caja empujada a un portal queda **sobre**
> el portal de salida y lo bloquea, así que el jugador no puede seguirla. Por eso ningún
> nivel exige teletransportar una caja hacia una zona que esté sellada de otro modo.

**Meta de la fase:** `./mvnw test` → **101/101 en verde**.

---

# 6. Plan de modificaciones de la interfaz (Swing)

La vista no existe: se escribe entera. Objetivo doble — cumplir la consigna y que la
vista **no contamine el modelo**.

## 6.1 Arquitectura de la vista

```
javax.swing.JFrame
└── GameWindow                      (CardLayout: MENU | GAME | SUMMARY)
    ├── MenuView         JPanel     → título, "Jugar", "Continuar", "Salir"
    ├── gamePanel        JPanel(BorderLayout)
    │   ├── HudView      JPanel     NORTH  → nivel, movimientos, empujes, tiempo,
    │   │                                    undos restantes, [Deshacer] [Reiniciar]
    │   └── BoardView    JPanel     CENTER → paintComponent() dibuja la grilla
    └── SummaryView      JPanel     → resumen de fin de nivel
```

| Clase | Responsabilidad | Patrón |
|---|---|---|
| `view/Screen` | `enum { MENU, GAME, SUMMARY }` — claves del `CardLayout` | — |
| `view/GameWindow` | `JFrame`, arma el `CardLayout`, cambia de pantalla | Facade de la vista |
| `view/MenuView` | Pantalla inicial | — |
| `view/BoardView` | `extends JPanel`; `paintComponent(Graphics2D)` recorre el `Board` y dibuja sprite por celda. **Es `GameListener`**: ante cualquier evento, `repaint()` | Observer |
| `view/HudView` | `JPanel` con los `JLabel` de movimientos/empujes/nivel/tiempo y los `JButton` de deshacer y reiniciar. **Es `GameListener`** | Observer |
| `view/SummaryView` | Movimientos, empujes, undos usados, puntaje y estrellas | — |
| `view/SpriteKey` | Clave inmutable del sprite (`record SpriteKey(String name, Direction facing)`) | — |
| `view/SpriteCatalog` | `Map<SpriteKey, BufferedImage>` — carga cada PNG **una sola vez** y lo comparte | **Flyweight** |

## 6.2 Controladores

| Clase | Responsabilidad | Patrón |
|---|---|---|
| `controller/InputController` | `KeyListener`/`KeyBinding` en el `JPanel` raíz. Traduce ← ↑ → ↓ a `Direction` y llama a `game.move(d)`. Consulta `game.isInputBlocked()` antes de propagar | **Controller (GRASP)** |
| `controller/HudController` | `ActionListener` de los botones Deshacer y Reiniciar → `game.undo()` / `game.restart()` | Controller |
| `controller/GameClock` | `javax.swing.Timer` de 1 s que llama a `level.elapseSeconds(1)` y dispara el repintado del HUD | Pure Fabrication |

> ⚠️ **Decisión clave de MVC:** las clases de `controller/` y `view/` importan
> `model.*`, pero **ninguna clase de `model/` importa `javax.swing.*`**. La vista se
> entera de los cambios únicamente por `GameListener`. Esto es exactamente lo que hay
> que poder defender oralmente.

## 6.3 Dibujo del tablero — decisiones

| Tema | Decisión |
|---|---|
| Tamaño de celda | 48 px fijos; la ventana se dimensiona con `rows × 48` y `cols × 48`. |
| Capas de dibujo | 1) baldosa (piso/pared/hielo/destino/cerrojo/muro) → 2) ocupante (caja o jugador) → 3) velo de niebla. |
| Niebla de guerra | Las celdas con `!visibilityMap.isVisible(p)` se pintan con un rectángulo negro (o `AlphaComposite` al 85 %). |
| Caja frágil | Sobre el sprite se dibuja el número de empujes restantes, o un sprite distinto por nivel de daño. |
| Jugador | Sprite según `Player.getFacing()` — requiere hacer `facing` mutable (H7d). |
| Caja guardada | Sprite "caja sobre destino" distinto al de caja suelta, para que se vea el progreso. |
| Antialiasing | `RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR` — es pixel art, no hay que suavizarlo. |
| Hilo | Todo el armado de UI dentro de `SwingUtilities.invokeLater(...)` en `Main`. |

## 6.4 Sonido

`service/SoundService` con `javax.sound.sampled.Clip` (**no** `javafx.media`).
Es `GameListener`: mapea `EventType` → `SoundEffect`.

```java
public enum SoundEffect { MOVE, PUSH, STORE, BREAK, DOOR_OPEN, TELEPORT, WIN, LOSE, UNDO }
```

Cada `Clip` se precarga una vez (mismo criterio Flyweight que los sprites). WAV PCM
16-bit, que es lo que `javax.sound.sampled` soporta nativamente — **no usar MP3**.

## 6.5 Orden de implementación de la UI

1. `SpriteCatalog` + `SpriteKey` con sprites placeholder (cuadrados de color) → permite
   ver el tablero antes de tener arte.
2. `BoardView.paintComponent` dibujando desde el `Board`.
3. `GameWindow` + `Main` con `SwingUtilities.invokeLater`.
4. `InputController` con flechas → el juego ya es jugable.
5. `HudView` + `HudController` (botones Deshacer / Reiniciar).
6. `GameClock` y el tiempo en el HUD.
7. `SummaryView` al ganar el nivel.
8. `MenuView` + `CardLayout` + continuar desde el progreso guardado.
9. Reemplazar los placeholders por sprites CC0 reales.
10. `SoundService` y los efectos.
11. Niebla de guerra sobre el `BoardView`.

## 6.6 Checklist de la consigna sobre UI

- [x] HUD muestra **movimientos** — `HudView`
- [x] HUD muestra **empujes** — `HudView`
- [x] HUD muestra **nivel actual** — `HudView` (más tiempo restante y undos disponibles)
- [x] Botón visible **Deshacer** — `HudView` + atajo `U`
- [x] Botón visible **Reiniciar nivel** — `HudView` + atajo `R`
- [x] **Todos** los elementos del tablero dibujados con **imágenes** — 21 PNG de 48×48
- [x] Efectos de sonido en las acciones del juego — 9 WAV vía `SoundService`
- [x] Todos los assets de **dominio público**, acreditados — `assets/CREDITS.md`
- [x] Resumen de fin de nivel con movimientos, empujes, usos de undo y puntaje final — `SummaryView`
- [x] Movimiento con **teclas direccionales** — `InputController`

## 6.7 Diferencias con el plan original de UI

| Qué | Por qué |
|---|---|
| `InputController` usa **InputMap/ActionMap**, no `KeyListener` | Un `KeyListener` depende de quién tenga el foco; al tocar un botón del HUD las flechas dejaban de andar. Los botones además son `setFocusable(false)`. |
| **No se hace `pack()`** al cambiar de nivel | La ventana saltaba de tamaño entre niveles. El tablero se dibuja centrado en el panel, con tamaño fijo de 900×620 y mínimo de 860×420. |
| Se agregó `EventType.UNDO` | Sin él, deshacer no hacía sonido ni refrescaba el HUD por el Observer. |
| Se agregó `Game.addLevelListener(...)` | Reiniciar o pasar de nivel construye un `Level` nuevo: sin esto la vista y el sonido se quedaban suscritos al nivel viejo. |
| `VisibilityMap` marca las **paredes en diagonal** | Sin eso las esquinas de cada sala quedaban como agujeros negros, porque una pared de esquina no es ortogonalmente adyacente a ninguna celda transparente. |
| Los assets son **generados**, no descargados | Se dibujaron con `Graphics2D` y se sintetizaron con `javax.sound.sampled`: son obra propia, así que el requisito de dominio público se cumple sin depender de terceros. Se pueden reemplazar pisando los archivos. |

---

# 7. Prompt para generar el Diagrama de Clases

➡️ **El prompt vive en [`docs/prompt-diagrama-clases.md`](prompt-diagrama-clases.md).**

Se regeneró leyendo la API pública real de las 89 clases de `src/main/java`, porque el
prompt original de esta sección describía el diseño *previsto* y el código terminó
difiriendo en bastantes puntos (`MoveReport`, `LandingOutcome`, el doble despacho de
`Occupant`, los hooks de `Box`, `EventType.UNDO`, `Empty` en vez de `Void`, y que
`port.LevelSource` nunca se implementó). Ese archivo incluye una tabla con todas las
diferencias.

> Al generar el diagrama, **verificar lo que salga contra el código**. La consigna
> exige poder defender cada decisión, y un diagrama que no coincide con el código es
> peor que no tenerlo.

# Resumen de prioridades

| Orden | Qué | Por qué |
|---|---|---|
| 1 | Migrar a Swing (Paso 0.1) | La consigna lo exige textualmente. Hoy no se cumple. |
| 2 | Reubicar paquetes (Paso 0.2) | Es gratis ahora y caro después. |
| 3 | Corregir `UndoTest` (Paso 0.3) | El test contradice la consigna. |
| 4 | Fases 1 y 2 (Pasos 1–10) | Llevan el modelo a 101/101 tests en verde. |
| 5 | Niveles + UI Swing + assets | Lo que el profesor ve correr en el video. |
| 6 | Diagrama + informe técnico + `git init` + repo público | Entregables formales. |
