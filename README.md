# Sistema_Admin_Condominios — Condominio Vista Verde

Desktop application for administering a 30-house condominium. It manages property
owners, monthly fee (cuota) payments, fee configuration, account statements,
general reports, and automatic email notifications.

Built with Java Swing, it uses a local SQLite database and sends transactional
emails through an SMTP provider (Brevo).

---

## Features

The main menu (`MenuInicio`) provides seven options:

| Option | Class | Description |
|--------|-------|-------------|
| **Registro de Propietario** | `RPropietario` | Register a new owner (name, phone, email) and assign an available house (1–30). Validates email format and that the house is free. |
| **Registro de Pagos** | `RPagos` | Record a monthly payment for a house. Prevents duplicate payments, enforces month sequence, and sends a confirmation email to the owner. |
| **Configuración de Cuotas** | `ConfiguraciónCuotas` | Update the monthly fee. Applies to future payments and emails every owner about the new amount. |
| **Estado de cuentas** | `Estadodecuenta` | View one house's payment status month by month (paid / pending) with yearly totals. |
| **Reporte General** | `General` | Overall report of all 30 houses: owner, current month status, yearly total paid, and collection summary. |
| **Casas Morosas** | `CasasM` | List of houses with their owner, phone and email. |
| **Cerrar sesión** | — | Log out and return to the login screen. |

---

## Tech Stack

- **Language:** Java 26
- **UI:** Java Swing (forms built with the NetBeans GUI designer)
- **Look & Feel:** [FlatLaf](https://www.formdev.com/flatlaf/) (FlatDarculaLaf — dark theme)
- **Database:** SQLite (local file, accessed via JDBC — no ORM)
- **Email:** JavaMail over SMTP (Brevo)
- **Build tool:** Apache Maven

### Dependencies (`pom.xml`)

| Dependency | Version | Purpose |
|------------|---------|---------|
| `org.xerial:sqlite-jdbc` | 3.45.1.0 | SQLite JDBC driver |
| `com.sun.mail:javax.mail` | 1.6.2 | JavaMail implementation (SMTP sending) |
| `com.formdev:flatlaf` | 3.7.1 | Modern Swing look and feel |

---

## Project Structure

```
Sistema_Admin_Condominios/
├── pom.xml                         Maven configuration
├── README.md                       This file
├── .gitignore                      Ignores target/ and the real email.properties
├── BD/
│   └── condominio_vistaverde.db    SQLite database
└── src/main/
    ├── java/com/mycompany/registropropietario/
    │   ├── RegistroPropietario.java    Entry point: sets theme and opens Login
    │   ├── Login.java                  Login screen (credential validation)
    │   ├── MenuInicio.java             Main menu (7 options)
    │   ├── RPropietario.java           Owner registration
    │   ├── RPagos.java                 Payment registration (+ confirmation email)
    │   ├── ConfiguraciónCuotas.java    Fee configuration (+ mass email)
    │   ├── Estadodecuenta.java         Per-house account statement
    │   ├── General.java                General report of all houses
    │   ├── CasasM.java                 Houses / owners listing
    │   ├── Morosos.java                Launcher related to CasasM
    │   ├── ConexionDB.java             SQLite connection helper
    │   ├── EmailConfig.java            Loads SMTP config from email.properties
    │   └── EnviarCorreo.java           Email sending utility
    └── resources/
        ├── email.properties.example   SMTP config template (committed)
        └── email.properties           Real credentials (git-ignored, you create it)
```

---

## Database

The application uses a local SQLite file at `BD/condominio_vistaverde.db`.
`ConexionDB.java` connects with `jdbc:sqlite:BD/condominio_vistaverde.db`, so the
app must be run from the project root so the relative path resolves.

Schema:

```sql
CREATE TABLE Casas (
    numero_casa     INTEGER PRIMARY KEY,   -- 1..30
    id_propietario  INTEGER,               -- FK to Propietarios (nullable)
    correo          TEXT
);

CREATE TABLE Propietarios (
    id_propietario  INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre          TEXT NOT NULL,
    telefono        TEXT,
    correo          TEXT
);

CREATE TABLE Pagos (
    id_pago      INTEGER PRIMARY KEY AUTOINCREMENT,
    numero_casa  INTEGER,                  -- FK to Casas
    mes          INTEGER,                  -- 1..12
    año          INTEGER,
    monto        REAL,
    FOREIGN KEY (numero_casa) REFERENCES Casas(numero_casa)
);

CREATE TABLE Configuracion (
    id     INTEGER PRIMARY KEY,
    cuota  REAL NOT NULL                   -- current monthly fee
);
```

---

## Build & Run

The entry point is `RegistroPropietario.main()`, which applies the FlatLaf theme
and opens the `Login` window.

```bash
# Compile
mvn clean compile

# Run from sources
mvn exec:java -Dexec.mainClass="com.mycompany.registropropietario.RegistroPropietario"

# Build an executable JAR
mvn clean package
# -> target/RegistroPropietario-1.0-SNAPSHOT.jar

# Run the JAR (from the project root, so BD/ resolves)
java -jar target/RegistroPropietario-1.0-SNAPSHOT.jar
```

> Run commands from the **project root** so the relative database path
> `BD/condominio_vistaverde.db` is found.

---

## Access (Login)

The login screen validates a fixed user defined in `Login.java`:

- **User:** `iusr_vistaverde`
- **Password:** `R3sidencial2026%`

Without the correct credentials the app does not reach the main menu.

---

## Email Notifications

Emails are sent through **Brevo** SMTP (`smtp-relay.brevo.com`, port `587`, TLS).
The logic lives in `EnviarCorreo.java`, and the configuration in `EmailConfig.java`.

When are emails sent?

1. **On payment registration (`RPagos`)** — *blocking*. After inserting the
   payment, a confirmation email is sent to the owner. The operation runs inside
   a transaction: if the email fails to send, the payment is **rolled back** and
   not saved. This guarantees that a registered payment always has a sent
   confirmation.
2. **On fee change (`ConfiguraciónCuotas`)** — *mass send*. After updating the
   fee, a notification is sent to every owner that has an email on file. Failures
   do not block the change; the app reports how many emails were sent and how
   many failed.

Emails are sent as `multipart/alternative` (plain text + a styled HTML version)
and include deliverability headers (`List-Unsubscribe`, `Auto-Submitted`).

### SMTP credentials (configuration)

Credentials are **not** stored in the source code. `EmailConfig.java` reads them
at startup from `src/main/resources/email.properties`, a file that is **ignored
by git** (see `.gitignore`). The repository only ships a template,
`email.properties.example`, with placeholder values.

This keeps secrets out of the public repository while letting each developer use
their own (or the team's shared) Brevo credentials.

**First-time setup (per machine):**

```bash
# 1. Copy the template to the real config file
cp src/main/resources/email.properties.example src/main/resources/email.properties

# 2. Edit email.properties and fill in your real Brevo values:
#    email.smtp.usuario   -> Brevo SMTP login (xxxx@smtp-brevo.com)
#    email.smtp.clave     -> Brevo SMTP key
#    email.remitente      -> a sender verified in Brevo
#    email.nombre.remitente -> display name
```

If `email.properties` is missing, the app fails at startup with a clear message
explaining that you must copy the template.

> **Sharing credentials with your team:** never put the real `email.properties`
> in the repo. Share the values through a private channel (1Password, a private
> drive, a direct message), and each developer pastes them into their own local
> `email.properties`.

---

## Possible Improvements

Ideas to harden and grow the project beyond the academic scope:

**Security**
- The login user/password are hardcoded in `Login.java` and the password is
  compared in plain text. Move them out of the source and store a *hash*
  (e.g. BCrypt) instead of the literal password.
- Support multiple users with roles instead of a single fixed account.

**Database**
- The connection path `jdbc:sqlite:BD/...` is relative, so the app only works
  when launched from the project root. Resolve it from a configurable location
  or the user's home directory.
- Add a real schema-creation/migration step so the DB can be rebuilt from
  scratch (today it depends on the committed `.db` file).
- Enable SQLite foreign-key enforcement (`PRAGMA foreign_keys = ON`).

**Architecture / code**
- Separate concerns: UI (Swing) is mixed with SQL and business logic. Extract a
  data-access layer (DAO/repository) and a service layer.
- Centralize DB access through `ConexionDB` everywhere and use prepared
  statements consistently to avoid SQL-injection risks.
- Reduce duplication across the screens (shared helpers for tables, validation,
  and email building).

**Email**
- Run the mass send and the payment-confirmation email off the UI thread
  (`SwingWorker`) so the interface doesn't freeze while sending.
- Make the blocking behavior on payment configurable (today a failed email rolls
  back the payment, which may not always be desirable).

**Build & quality**
- Add unit tests (validation, fee calculations, account-statement logic).
- Configure the Maven Shade/Assembly plugin to produce a self-contained runnable
  JAR with dependencies bundled.
- Add a CI workflow (build + tests on push).

---

## Notes & Limitations

- Academic group project.
- Swing forms were generated with the NetBeans GUI designer (`initComponents`
  blocks marked with `//GEN-` comments).
- Single-window navigation: opening a screen disposes the previous one.
- Dark theme (FlatDarculaLaf) is applied by default.

---

## Authors

- _(complete with the team members)_

---
---

# Versión en Español

# Sistema_Admin_Condominios — Condominio Vista Verde

Aplicación de escritorio para administrar un condominio de 30 casas. Gestiona
propietarios, pagos de la cuota mensual, configuración de la cuota, estados de
cuenta, reportes generales y notificaciones automáticas por correo.

Está construida con Java Swing, usa una base de datos local SQLite y envía
correos transaccionales a través de un proveedor SMTP (Brevo).

---

## Funcionalidades

El menú principal (`MenuInicio`) ofrece siete opciones:

| Opción | Clase | Descripción |
|--------|-------|-------------|
| **Registro de Propietario** | `RPropietario` | Registra un nuevo propietario (nombre, teléfono, correo) y le asigna una casa disponible (1–30). Valida el formato del correo y que la casa esté libre. |
| **Registro de Pagos** | `RPagos` | Registra un pago mensual de una casa. Evita pagos duplicados, exige la secuencia de meses y envía un correo de confirmación al propietario. |
| **Configuración de Cuotas** | `ConfiguraciónCuotas` | Actualiza la cuota mensual. Aplica a pagos futuros y notifica por correo a todos los propietarios el nuevo monto. |
| **Estado de cuentas** | `Estadodecuenta` | Muestra el estado de pagos de una casa mes a mes (pagado / pendiente) con totales del año. |
| **Reporte General** | `General` | Reporte general de las 30 casas: propietario, estado del mes actual, total pagado en el año y resumen de recaudación. |
| **Casas Morosas** | `CasasM` | Lista de casas con su propietario, teléfono y correo. |
| **Cerrar sesión** | — | Cierra sesión y vuelve a la pantalla de inicio. |

---

## Tecnologías

- **Lenguaje:** Java 26
- **Interfaz:** Java Swing (formularios hechos con el diseñador de NetBeans)
- **Apariencia:** [FlatLaf](https://www.formdev.com/flatlaf/) (FlatDarculaLaf — tema oscuro)
- **Base de datos:** SQLite (archivo local, vía JDBC — sin ORM)
- **Correo:** JavaMail sobre SMTP (Brevo)
- **Construcción:** Apache Maven

### Dependencias (`pom.xml`)

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| `org.xerial:sqlite-jdbc` | 3.45.1.0 | Driver JDBC de SQLite |
| `com.sun.mail:javax.mail` | 1.6.2 | Implementación de JavaMail (envío SMTP) |
| `com.formdev:flatlaf` | 3.7.1 | Apariencia moderna para Swing |

---

## Estructura del Proyecto

```
Sistema_Admin_Condominios/
├── pom.xml                         Configuración de Maven
├── README.md                       Este archivo
├── .gitignore                      Ignora target/ y el email.properties real
├── BD/
│   └── condominio_vistaverde.db    Base de datos SQLite
└── src/main/
    ├── java/com/mycompany/registropropietario/
    │   ├── RegistroPropietario.java    Punto de entrada: aplica el tema y abre Login
    │   ├── Login.java                  Pantalla de inicio de sesión (valida credenciales)
    │   ├── MenuInicio.java             Menú principal (7 opciones)
    │   ├── RPropietario.java           Registro de propietarios
    │   ├── RPagos.java                 Registro de pagos (+ correo de confirmación)
    │   ├── ConfiguraciónCuotas.java    Configuración de cuota (+ correo masivo)
    │   ├── Estadodecuenta.java         Estado de cuenta por casa
    │   ├── General.java                Reporte general de todas las casas
    │   ├── CasasM.java                 Listado de casas / propietarios
    │   ├── Morosos.java                Lanzador relacionado con CasasM
    │   ├── ConexionDB.java             Ayudante de conexión a SQLite
    │   ├── EmailConfig.java            Carga la config SMTP desde email.properties
    │   └── EnviarCorreo.java           Utilidad de envío de correos
    └── resources/
        ├── email.properties.example   Plantilla de config SMTP (sí se sube)
        └── email.properties           Credenciales reales (ignorado por git, lo creas tú)
```

---

## Base de Datos

La aplicación usa un archivo SQLite local en `BD/condominio_vistaverde.db`.
`ConexionDB.java` se conecta con `jdbc:sqlite:BD/condominio_vistaverde.db`, por lo
que la app debe ejecutarse desde la raíz del proyecto para que la ruta relativa
se resuelva.

Esquema:

```sql
CREATE TABLE Casas (
    numero_casa     INTEGER PRIMARY KEY,   -- 1..30
    id_propietario  INTEGER,               -- FK a Propietarios (puede ser nulo)
    correo          TEXT
);

CREATE TABLE Propietarios (
    id_propietario  INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre          TEXT NOT NULL,
    telefono        TEXT,
    correo          TEXT
);

CREATE TABLE Pagos (
    id_pago      INTEGER PRIMARY KEY AUTOINCREMENT,
    numero_casa  INTEGER,                  -- FK a Casas
    mes          INTEGER,                  -- 1..12
    año          INTEGER,
    monto        REAL,
    FOREIGN KEY (numero_casa) REFERENCES Casas(numero_casa)
);

CREATE TABLE Configuracion (
    id     INTEGER PRIMARY KEY,
    cuota  REAL NOT NULL                   -- cuota mensual vigente
);
```

---

## Compilar y Ejecutar

El punto de entrada es `RegistroPropietario.main()`, que aplica el tema FlatLaf
y abre la ventana de `Login`.

```bash
# Compilar
mvn clean compile

# Ejecutar desde el código fuente
mvn exec:java -Dexec.mainClass="com.mycompany.registropropietario.RegistroPropietario"

# Generar un JAR ejecutable
mvn clean package
# -> target/RegistroPropietario-1.0-SNAPSHOT.jar

# Ejecutar el JAR (desde la raíz del proyecto, para que BD/ se resuelva)
java -jar target/RegistroPropietario-1.0-SNAPSHOT.jar
```

> Ejecuta los comandos desde la **raíz del proyecto** para que se encuentre la
> ruta relativa de la base de datos `BD/condominio_vistaverde.db`.

---

## Acceso (Inicio de Sesión)

La pantalla de inicio de sesión valida un usuario fijo definido en `Login.java`:

- **Usuario:** `iusr_vistaverde`
- **Contraseña:** `R3sidencial2026%`

Sin las credenciales correctas, la aplicación no llega al menú principal.

---

## Notificaciones por Correo

Los correos se envían a través de **Brevo** SMTP (`smtp-relay.brevo.com`, puerto
`587`, TLS). La lógica está en `EnviarCorreo.java` y la configuración en
`EmailConfig.java`.

¿Cuándo se envían correos?

1. **Al registrar un pago (`RPagos`)** — *bloqueante*. Después de insertar el
   pago, se envía un correo de confirmación al propietario. La operación se
   ejecuta dentro de una transacción: si el correo falla, el pago se **revierte**
   y no se guarda. Así se garantiza que todo pago registrado tiene su
   confirmación enviada.
2. **Al cambiar la cuota (`ConfiguraciónCuotas`)** — *envío masivo*. Después de
   actualizar la cuota, se envía un aviso a cada propietario que tenga correo
   registrado. Los fallos no bloquean el cambio; la app informa cuántos correos
   se enviaron y cuántos fallaron.

Los correos se envían como `multipart/alternative` (texto plano + una versión
HTML con estilo) e incluyen cabeceras de entregabilidad (`List-Unsubscribe`,
`Auto-Submitted`).

### Credenciales SMTP (configuración)

Las credenciales **no** están en el código fuente. `EmailConfig.java` las lee al
arrancar desde `src/main/resources/email.properties`, un archivo que está
**ignorado por git** (ver `.gitignore`). El repositorio solo incluye una
plantilla, `email.properties.example`, con valores de ejemplo.

Así los secretos no quedan en el repositorio público, y cada desarrollador puede
usar sus propias credenciales de Brevo (o las compartidas del equipo).

**Configuración inicial (en cada máquina):**

```bash
# 1. Copia la plantilla al archivo de configuración real
cp src/main/resources/email.properties.example src/main/resources/email.properties

# 2. Edita email.properties y coloca tus valores reales de Brevo:
#    email.smtp.usuario   -> login SMTP de Brevo (xxxx@smtp-brevo.com)
#    email.smtp.clave     -> clave SMTP de Brevo
#    email.remitente      -> un remitente verificado en Brevo
#    email.nombre.remitente -> nombre visible
```

Si falta `email.properties`, la app no arranca y muestra un mensaje claro
indicando que debes copiar la plantilla.

> **Compartir credenciales con tu equipo:** nunca subas el `email.properties`
> real al repo. Comparte los valores por un canal privado (1Password, un drive
> privado, un mensaje directo), y cada desarrollador los pega en su propio
> `email.properties` local.

---

## Mejoras Posibles

Ideas para reforzar y hacer crecer el proyecto más allá del alcance académico:

**Seguridad**
- El usuario y contraseña de login están escritos en `Login.java` y la
  contraseña se compara en texto plano. Conviene sacarlos del código y guardar
  un *hash* (por ejemplo BCrypt) en lugar de la contraseña literal.
- Soportar varios usuarios con roles en lugar de una sola cuenta fija.

**Base de datos**
- La ruta de conexión `jdbc:sqlite:BD/...` es relativa, así que la app solo
  funciona si se ejecuta desde la raíz del proyecto. Conviene resolverla desde
  una ubicación configurable o desde la carpeta del usuario.
- Agregar un paso real de creación/migración del esquema para reconstruir la BD
  desde cero (hoy depende del archivo `.db` que está en el repo).
- Activar la verificación de claves foráneas de SQLite (`PRAGMA foreign_keys = ON`).

**Arquitectura / código**
- Separar responsabilidades: la UI (Swing) está mezclada con SQL y lógica de
  negocio. Conviene extraer una capa de acceso a datos (DAO/repositorio) y una
  capa de servicios.
- Centralizar el acceso a la BD a través de `ConexionDB` en todos lados y usar
  *prepared statements* de forma consistente para evitar inyección SQL.
- Reducir la duplicación entre pantallas (utilidades compartidas para tablas,
  validaciones y armado de correos).

**Correo**
- Ejecutar el envío masivo y el correo de confirmación de pago fuera del hilo de
  la UI (`SwingWorker`) para que la interfaz no se congele mientras envía.
- Hacer configurable el comportamiento bloqueante del pago (hoy un correo
  fallido revierte el pago, lo cual no siempre es deseable).

**Construcción y calidad**
- Agregar pruebas unitarias (validaciones, cálculo de cuotas, lógica del estado
  de cuenta).
- Configurar el plugin Shade/Assembly de Maven para producir un JAR ejecutable
  autocontenido con las dependencias incluidas.
- Añadir un flujo de CI (compilar + pruebas en cada push).

---

## Notas y Limitaciones

- Proyecto académico grupal.
- Los formularios Swing se generaron con el diseñador de NetBeans (bloques
  `initComponents` marcados con comentarios `//GEN-`).
- Navegación de una sola ventana: abrir una pantalla cierra la anterior.
- El tema oscuro (FlatDarculaLaf) se aplica por defecto.

---

## Autores

- _(completar con los integrantes del equipo)_
