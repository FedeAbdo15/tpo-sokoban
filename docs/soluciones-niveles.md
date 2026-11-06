# Soluciones de referencia de los 5 niveles

Secuencias verificadas a mano al diseñar cada nivel. Sirven para tres cosas:

1. Grabar el video de gameplay sin trabarse.
2. Escribir, cuando el motor esté completo, un test de integración que reproduzca
   la secuencia y verifique `level.isWon()`.
3. Confirmar que ningún nivel es irresoluble.

Notación: `D` derecha, `I` izquierda, `A` arriba, `B` abajo. `D×3` = tres veces a la derecha.
Las posiciones son `(fila, columna)` con origen en la esquina superior izquierda del mapa.

> ✅ **Verificado contra el motor.** `LevelSolutionTest` reproduce estas secuencias y
> comprueba que cada nivel quede ganado, con la cantidad exacta de empujes y de muros
> abiertos, y que después del nivel 5 el juego pase a `GameCompletedState`.
> Si alguien toca un `.txt` y lo vuelve irresoluble, ese test se pone en rojo.

| Nivel | Secuencia completa | Movimientos | Empujes |
|---|---|---|---|
| 01 | `IIA DDD BIIIB DDD` | 14 | 6 |
| 02 | `IIA DD BIIB DDDD BBIIIIA DDDD` | 24 | 10 |
| 03 | `D BIIIIB D BB DDD` | 13 | 4 |
| 04 | `DDD AD BBBB III B IB DDD` | 18 | 11 |
| 05 | `DD B ADDD B BBBB AIIIIB DDDDDD` | 24 | 12 |

---

## nivel01 — Primeros pasos (7×8)

Dos cajas normales, sin mecanismos. Enseña empujar y que no se puede tirar.

| # | Movimientos | Qué pasa |
|---|---|---|
| 1 | `I, I, A` | El jugador (3,3) rodea hasta (2,1) |
| 2 | `D×3` | Empuja la caja (2,2) → (2,5) **guardada** |
| 3 | `B, I, I, I, B` | Baja hasta (4,1) |
| 4 | `D×3` | Empuja la caja (4,2) → (4,5) **guardada** → victoria |

Total: 14 movimientos, 6 empujes.

---

## nivel02 — Carga frágil (8×10)

Introduce las cajas frágiles y sus destinos propios. Cada caja tiene exactamente los
empujes que necesita: no sobra ninguno.

| # | Movimientos | Qué pasa |
|---|---|---|
| 1 | `I, I, A` | Jugador (3,4) → (2,2) |
| 2 | `D, D` | Empuja la frágil `2` de (2,3) a (2,5) **guardada** (usa sus 2 empujes) |
| 3 | `B, I, I, B` | Jugador → (4,2) |
| 4 | `D×4` | Empuja la caja normal (4,3) → (4,7) **guardada** |
| 5 | `B, B, I, I, I, I, A` | Jugador rodea por la fila 6 hasta (5,2) |
| 6 | `D×4` | Empuja la frágil `4` de (5,3) a (5,7) **guardada** (usa sus 4 empujes) → victoria |

Total: ~21 movimientos, 10 empujes.
⚠️ Un empuje de más rompe la caja: es el nivel donde se ve `BrokenBoxCondition`.

---

## nivel03 — Pista de hielo (9×11)

Introduce el terreno resbaladizo, para el jugador y para las cajas.

| # | Movimientos | Qué pasa |
|---|---|---|
| 1 | `D` | El jugador entra al hielo en (2,2) y **se desliza** hasta (2,6) |
| 2 | `B, I, I, I, I, B` | Baja a (4,2) |
| 3 | `D` | Empuja la caja (4,3) al hielo: **se desliza** por (4,4)…(4,6) y frena en el destino (4,7) **guardada** |
| 4 | `B, B` | Jugador (4,3) → (6,3) |
| 5 | `D×3` | Empuja la caja (6,4) → (6,7) **guardada** → victoria |

Total: ~13 movimientos, 4 empujes.
Un solo empuje mueve la caja 4 casillas: se ve que el deslizamiento **no** suma empujes.

---

## nivel04 — Bajo llave (9×11)

Introduce la caja llave, el casillero cerrojo y los muros. **Es el nivel que muestra la
niebla de guerra**: la mitad inferior solo es alcanzable por el muro `A`, así que arranca
oculta y se revela al abrirlo.

| # | Movimientos | Qué pasa |
|---|---|---|
| 1 | `D×3` | Empuja la caja llave de (2,3) hasta (2,6) = casillero `a` → **abre el muro `A`** en (4,6), la baldosa se consume, se revela la zona de abajo |
| 2 | `A, D` | Jugador (2,5) → (1,6) |
| 3 | `B×4` | Empuja la llave por la columna 6: (3,6), muro abierto (4,6), (5,6) y (6,6) = `;` **guardada** |
| 4 | `I, I, I` | Jugador (5,6) → (5,3) |
| 5 | `B` | Empuja la caja normal (6,3) → (7,3) |
| 6 | `I, B` | Jugador (6,3) → (7,2) |
| 7 | `D×3` | Empuja la caja (7,3) → (7,6) = `.` **guardada** → victoria |

Total: ~22 movimientos, 11 empujes, 1 muro abierto.
⚠️ Si la llave llegara a `;` sin haber pisado `a`, se pierde por `EARLY_KEY_STORAGE`.

---

## nivel05 — El almacén final (9×13)

Combina los tres tipos de caja, el muro con casillero y un par de portales.
La caja llave usa el **mismo** camino que abre: cae sobre `b`, el muro `B` se abre
justo debajo y la caja sigue bajando por la columna 6 hasta su destino.

### Camino principal

| # | Movimientos | Qué pasa |
|---|---|---|
| 1 | `D, D` | Jugador (1,1) → (1,3) |
| 2 | `B` | Empuja la frágil `2` de (2,3) a (3,3) = `:` **guardada** |
| 3 | `A, D, D, D` | Jugador (2,3) → (1,6) |
| 4 | `B` | Empuja la llave (2,6) → (3,6) = casillero `b` → **abre el muro `B`** en (4,6) |
| 5 | `B×4` | La llave sigue bajando: muro (4,6), (5,6), (6,6) y (7,6) = `;` **guardada** |
| 6 | `A, I, I, I, I, B` | Jugador (6,6) rodea por la fila 5 hasta (6,2) |
| 7 | `D×6` | Empuja la caja normal (6,3) → (6,9) = `.` **guardada** → victoria |

Total: ~22 movimientos, 12 empujes, 1 muro abierto.

### El atajo por los portales

Los portales `P` de (1,11) y (7,1) conectan la esquina superior derecha con la inferior
izquierda. Permiten una segunda solución: ir primero a (1,11), teletransportarse a (7,1)
y resolver la caja normal **antes** de abrir el muro, volviendo después por el mismo
portal. Sirve para mostrar en el video que el portal funciona en los dos sentidos.

⚠️ Una caja empujada a un portal queda **sobre** el portal de salida y lo bloquea:
por eso ningún nivel exige teletransportar una caja a través de una zona sellada.
