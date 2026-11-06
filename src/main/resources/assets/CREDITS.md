# Creditos de los assets

## Imagenes — `assets/images/` (21 sprites de 48x48 px)

Sprites originales del proyecto, dibujados programaticamente con `java.awt.Graphics2D`
y exportados a PNG. **Obra propia del equipo de desarrollo, liberada como dominio
publico (CC0 1.0).** No se tomo arte de terceros, asi que no hay ninguna licencia
de por medio.

| Archivo | Elemento del tablero |
|---|---|
| `floor.png` | Piso |
| `wall.png` | Pared |
| `empty.png` | Terreno vacio inaccesible |
| `ice.png` | Terreno resbaladizo |
| `portal.png` | Portal |
| `keytile.png` | Casillero cerrojo |
| `door_closed.png` / `door_open.png` | Muro cerrado / abierto |
| `storage_normal.png` / `storage_weak.png` / `storage_key.png` | Destinos por tipo de caja |
| `box_normal.png` / `box_weak.png` / `box_key.png` | Cajas normal, fragil y llave |
| `box_*_stored.png` | Las mismas cajas ya guardadas en su destino |
| `player_up/down/left/right.png` | Sokoban mirando en cada direccion |

## Sonidos — `assets/audio/` (9 efectos WAV)

Efectos originales del proyecto, sintetizados con `javax.sound.sampled`
(WAV PCM 16 bits, mono, 44.1 kHz). **Obra propia, dominio publico (CC0 1.0).**

| Archivo | Cuando suena |
|---|---|
| `move.wav` | El Sokoban da un paso |
| `push.wav` | Empuja una caja |
| `store.wav` | Una caja entra en su destino |
| `break.wav` | Se rompe una caja fragil |
| `door_open.wav` | Se abre un muro |
| `teleport.wav` | Alguien atraviesa un portal |
| `undo.wav` | Se usa el boton deshacer |
| `win.wav` / `lose.wav` | Fin de nivel |

## Como reemplazarlos

`SpriteCatalog` y `SoundService` leen los archivos del classpath por nombre.
Para cambiar el arte alcanza con pisar el PNG o el WAV respetando el nombre y,
en el caso de las imagenes, los 48x48 px. Si se usa arte de terceros hay que
anotar aca la fuente y la licencia, que la consigna exige dominio publico.
