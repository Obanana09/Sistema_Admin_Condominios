# Sistema de Administración de Condominio "Vista Verde"
**Proyecto Final - Programación I (2026)**

[cite_start]Este repositorio contiene la infraestructura base y la lógica de persistencia para el sistema de gestión del **Condominio Vista Verde**, diseñado para administrar el control de cobros de **30 casas**[cite: 91].

## Infraestructura de Base de Datos
[cite_start]Como parte de los **puntos extra (+5 pts)** por persistencia de datos, este proyecto utiliza un motor de base de datos relacional para asegurar que la información no se pierda al cerrar el programa[cite: 92, 158].

* [cite_start]**Motor:** SQLite[cite: 74, 93].
* [cite_start]**Archivo de DB:** `condominio_vistaverde.db` (ubicado en la raíz del proyecto)[cite: 7, 79, 93].
* **Estructura de la Tabla `Casas`**:
    * [cite_start]`id_casa`: Identificador único autoincremental[cite: 94].
    * [cite_start]`numero_casa`: Número correlativo de la casa (1 al 30)[cite: 95, 116].
    * [cite_start]`nombre_encargado`: Nombre completo del propietario registrado[cite: 96, 125].
    * [cite_start]`telefono`: Número de contacto[cite: 97, 125].
    * [cite_start]`estado_pago`: Estado actual del mes (Pagado/Pendiente)[cite: 97, 133].

## Contenido de la Entrega Actual
1.  [cite_start]**`pom.xml`**: Configuración de Maven con la dependencia `sqlite-jdbc` necesaria para la conexión[cite: 98, 174].
2.  [cite_start]**`logic/ConexionDB.java`**: Implementación de la conexión mediante el patrón **Singleton**, utilizando una **ruta relativa** para garantizar la portabilidad del proyecto[cite: 5, 104].
3.  [cite_start]**`condominio_vistaverde.db`**: Base de datos pre-cargada con los **30 registros obligatorios** de las casas para pruebas iniciales[cite: 55, 100].

## Equipo de Trabajo
| Integrante | Rol | Responsabilidad DB |
| :--- | :--- | :--- |
| **Integrante 1** | Scrum Master / DB Admin | [cite_start]Diseño de DB, Conexión JDBC y Git[cite: 106, 184]. |
| **Integrante 3** | Backend Developer | [cite_start]Modelos de datos y lógica CRUD[cite: 107, 176]. |

##  Instrucciones para el Catedrático
Para ejecutar el proyecto correctamente:
1. [cite_start]Asegurarse de que el archivo `condominio_vistaverde.db` permanezca en la **carpeta raíz** del proyecto (al mismo nivel que el `pom.xml`)[cite: 7, 102].
2. [cite_start]Ejecutar **Clean and Build** en NetBeans para descargar automáticamente el driver de SQLite vía Maven[cite: 103, 161].
