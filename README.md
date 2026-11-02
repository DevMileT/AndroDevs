# LUCKY SEVENS

Aplicación Android de juego de azar (tragaperras de tres rodillos con monedas
virtuales), desarrollada en Kotlin con Jetpack Compose y SQLite para el ciclo
de FP DAM. Proyecto académico del grupo **AndroDevs**.

> Solo se usan monedas virtuales: no hay dinero real, compras ni apuestas económicas.

## Autores

- Milena ([@DevMileT](https://github.com/DevMileT))
- Emanuel ([@DevEmanuelRodriguez](https://github.com/DevEmanuelRodriguez))

## Tecnologías

- Kotlin y Jetpack Compose (interfaz)
- Navigation Compose (navegación entre pantallas)
- SQLite con `SQLiteOpenHelper` (sin Room)
- RxJava 3 para las llamadas asíncronas a la base de datos
- Corrutinas para las animaciones
- Android mínimo: API 26 (solo orientación vertical)

## Estado de la Tarea 1

La práctica se ha realizado en seis fases y todas están cerradas. 

### Funcionalidades implementadas y verificadas en un móvil real

- **Pantalla de bienvenida** y **menú** con botones de navegación y desplegable.
- **Juego**: selector de apuesta limitado por el saldo, tres rodillos animados
  que se detienen de forma progresiva, tabla de premios y panel de resultado.
- **Saldo y récord personal** guardados en SQLite: se conservan al cerrar la app.
- **Historial de partidas** de más reciente a más antigua, con filtro por
  resultado (victoria, empate, derrota).
- **Estadísticas** calculadas desde el historial (total, victorias, empates,
  derrotas, balance neto y récord).
- **Bonus diario** de 50 monedas, una vez por día, protegido con un `UPDATE`
  condicionado en SQLite.
- Cada giro se guarda en una **transacción atómica** (saldo, récord e historial).

### Arquitectura

Cuatro capas: interfaz (Compose), estado (ViewModel), dominio (Kotlin puro, sin
dependencias de Android) y datos (SQLite). Los repositorios son interfaces del
dominio implementadas en la capa de datos.

```
app/src/main/java/com/androdevs/luckysevens/
├── domain/   Reglas del juego, premios, bonus e interfaces de repositorio
├── data/     SQLiteOpenHelper y repositorios con RxJava
├── ui/
│   ├── state/       ViewModels y estados de pantalla
│   ├── screens/     Una pantalla por archivo
│   ├── components/  Piezas reutilizables (barras superiores)
│   ├── navigation/  Rutas y navegación
│   ├── theme/       Colores y tema
│   └── util/        Formato de números y fechas, símbolos
├── LuckySevensApp.kt
└── MainActivity.kt
```

### Pruebas automáticas

- Dominio: 71 pruebas unitarias en el PC.
- ViewModels (`ui/state`): 19 pruebas unitarias en el PC.
- Base de datos y repositorios: 24 pruebas instrumentadas en un móvil real.

.