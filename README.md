# Sistema de Gestión de Residencia Geriátrica

Aplicación web desarrollada en **Java y Spring Boot** para administrar residentes, enfermeros y habitaciones de una residencia geriátrica.

El proyecto fue realizado con fines académicos, aplicando conceptos de **programación orientada a objetos (POO), arquitectura en capas, persistencia de datos mediante JPA/Hibernate y desarrollo web con Spring MVC y Thymeleaf**.

## 1. Tecnologías utilizadas

| Tecnología | Función |
|---|---|
| Java | Lenguaje de programación |
| Spring Boot | Framework principal |
| Spring Web (Spring MVC) | Controladores y gestión de peticiones HTTP |
| Spring Data JPA | Capa de acceso a datos |
| Hibernate | Mapeo objeto-relacional (ORM) |
| MySQL | Base de datos relacional |
| Thymeleaf | Generación de vistas HTML dinámicas |
| HTML y CSS | Interfaz de usuario |
| Maven | Gestión de dependencias y compilación |
| JUnit 5 | Pruebas unitarias |
| Mockito | Simulación de dependencias en las pruebas |
| NetBeans | Entorno de desarrollo utilizado |

## 2. Descripción del sistema

El sistema permite a un administrativo gestionar residentes, enfermeros y habitaciones, así como administrar las relaciones entre ellos.

La aplicación proporciona una interfaz web con un menú principal desde el cual se accede a los distintos módulos.

### Gestión de residentes

- Registrar nuevos residentes.
- Consultar y listar residentes.
- Modificar sus datos.
- Eliminar residentes.
- Evitar DNI duplicados.
- Impedir la eliminación de residentes con habitaciones asignadas.

### Gestión de enfermeros

- Registrar nuevos enfermeros.
- Consultar y listar enfermeros.
- Modificar sus datos.
- Eliminar enfermeros.
- Evitar DNI duplicados.
- Impedir la eliminación de enfermeros con habitaciones asignadas.

### Gestión de habitaciones

- Registrar habitaciones con códigos autogenerados.
- Consultar todas las habitaciones.
- Filtrar habitaciones por estado.
- Visualizar residentes y enfermeros asociados.
- Eliminar habitaciones sin relaciones activas.

Los estados posibles son `LIBRE` y `OCUPADA`.

### Gestión de reservas

- Asignar un residente a una habitación disponible.
- Impedir que un residente ocupe varias habitaciones.
- Impedir la reserva de habitaciones ocupadas.
- Consultar las reservas activas.
- Cancelar reservas y liberar habitaciones.

### Gestión de asignaciones

- Asignar enfermeros a habitaciones.
- Permitir que un enfermero sea responsable de varias habitaciones.
- Impedir que una habitación tenga varios enfermeros simultáneamente.
- Cambiar el enfermero responsable.
- Desasignar enfermeros.
- Consultar las habitaciones de un enfermero.

### Consultas específicas

- Consultar la habitación asignada a un residente.
- Consultar los residentes atendidos por un enfermero.
- Consultar el enfermero responsable de un residente.

## 3. Modelo de datos

El sistema utiliza tres entidades persistentes:

**Residente**
- `id`
- `dni`
- `nombre`
- `apellido`

**Enfermero**
- `id`
- `dni`
- `nombre`
- `apellido`

**Habitacion**
- `codigo`
- `estado`
- `residente`
- `enfermero`

### Relaciones JPA

**Residente ↔ Habitación (1:1)**

Un residente puede ocupar como máximo una habitación, y una habitación puede alojar como máximo un residente.

Se implementa mediante `@OneToOne`, utilizando la clave foránea `residente_id` en la tabla `habitaciones`.

**Enfermero → Habitaciones (1:N)**

Un enfermero puede estar asignado a múltiples habitaciones, pero cada habitación puede tener un único enfermero responsable.

Se implementa mediante `@ManyToOne` en `Habitacion` y `@OneToMany(mappedBy = "enfermero")` en `Enfermero`.

### Reservas y asignaciones

Las reservas y asignaciones no se representan mediante tablas independientes.

En cambio, se implementan como operaciones de negocio que modifican las relaciones de la entidad `Habitacion`.

Estas operaciones son administradas por `ReservaService` y `AsignacionService`, respectivamente.

Este diseño permite mantener un modelo de datos sencillo, sin tablas intermedias innecesarias para las relaciones actuales.

## 4. Arquitectura del proyecto

La aplicación utiliza una arquitectura en capas:

```text
src/
├── main/
│   ├── java/com/residencia/geriatrica/
│   │   ├── model/
│   │   │   ├── Residente.java
│   │   │   ├── Enfermero.java
│   │   │   ├── Habitacion.java
│   │   │   └── EstadoHabitacion.java
│   │   │
│   │   ├── repository/
│   │   │   ├── ResidenteRepository.java
│   │   │   ├── EnfermeroRepository.java
│   │   │   └── HabitacionRepository.java
│   │   │
│   │   ├── service/
│   │   │   ├── ResidenteService.java
│   │   │   ├── EnfermeroService.java
│   │   │   ├── HabitacionService.java
│   │   │   ├── ReservaService.java
│   │   │   └── AsignacionService.java
│   │   │
│   │   └── controller/
│   │       ├── HomeController.java
│   │       ├── ResidenteController.java
│   │       ├── EnfermeroController.java
│   │       ├── HabitacionController.java
│   │       ├── ReservaController.java
│   │       └── AsignacionController.java
│   │
│   └── resources/
│       ├── templates/
│       │   ├── index.html
│       │   ├── residentes/
│       │   ├── enfermeros/
│       │   ├── habitaciones/
│       │   ├── reservas/
│       │   └── asignaciones/
│       │
│       ├── static/css/
│       │   └── estilos.css
│       │
│       └── application.properties
│
└── test/
    └── java/com/residencia/geriatrica/service/
        ├── ResidenteServiceTest.java
        ├── EnfermeroServiceTest.java
        ├── HabitacionServiceTest.java
        ├── ReservaServiceTest.java
        └── AsignacionServiceTest.java
```

### Responsabilidades de las capas

- **Model:** representa las entidades y sus relaciones JPA.
- **Repository:** proporciona las operaciones de persistencia utilizando Spring Data JPA.
- **Service:** implementa las reglas de negocio y las validaciones.
- **Controller:** gestiona las solicitudes HTTP y comunica los servicios con las vistas.
- **View:** presenta los datos mediante Thymeleaf, HTML y CSS.

## 5. Instalación y configuración

### Requisitos previos

Para ejecutar el proyecto se necesita:

- Java compatible con la versión configurada en el `pom.xml`.
- Maven.
- MySQL Server.
- Un IDE compatible con proyectos Maven/Spring Boot, como NetBeans o IntelliJ IDEA.

El proyecto fue desarrollado utilizando Java 25 y NetBeans 24.

### Obtener el proyecto

Clonar el repositorio:

```bash
git clone https://github.com/iang-inspt/residencia-geriatrica
```

Ingresar al directorio del proyecto:

```bash
cd geriatrica
```

### Crear la base de datos

Desde MySQL Workbench o la consola de MySQL:

```sql
CREATE DATABASE residencia_db;
```

No es necesario crear las tablas manualmente, ya que Hibernate puede generarlas a partir de las entidades JPA.

### Configurar application.properties

Archivo ubicado en:

`src/main/resources/application.properties`

```properties
spring.application.name=geriatrica

# Servidor web
server.port=8080

# Conexion MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/residencia_db
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD}

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Thymeleaf
spring.thymeleaf.cache=false
```

Es necesario configurar la variable de entorno `DB_PASSWORD` con la contraseña del usuario MySQL. Opcionalmente, `DB_USER` permite establecer otro usuario; si no se define, se utiliza `root`.

Se recomienda utilizar un usuario de base de datos específico para la aplicación y no almacenar contraseñas reales en el repositorio.

La propiedad `spring.jpa.hibernate.ddl-auto=update` se utiliza para facilitar el desarrollo académico. No se recomienda como mecanismo de migración de bases de datos en producción.

### Ejecutar la aplicación

Desde NetBeans:

1. Abrir el proyecto Maven.
2. Verificar que MySQL esté iniciado.
3. Comprobar la configuración de `application.properties` y las variables de entorno.
4. Ejecutar **Clean and Build**.
5. Ejecutar **Run**.

Como alternativa, desde una terminal con Maven instalado:

```bash
mvn spring-boot:run
```

Acceder desde el navegador a:

`http://localhost:8080/`

## 6. Navegación y rutas principales

| Ruta | Funcionalidad |
|---|---|
| `/` | Panel administrativo |
| `/residentes` | Gestión de residentes |
| `/enfermeros` | Gestión de enfermeros |
| `/habitaciones` | Gestión de habitaciones |
| `/reservas` | Gestión de reservas |
| `/asignaciones` | Gestión de asignaciones |
| `/asignaciones/consultas` | Consultas de residentes y enfermeros |

La interfaz permite acceder a los formularios y operaciones desde cada módulo, sin necesidad de introducir manualmente las rutas HTTP.


## 7. Posibles mejoras futuras

Entre las mejoras que podrían incorporarse se encuentran:

- Autenticación y autorización mediante Spring Security.
- Validaciones de formularios con Bean Validation.
- Manejo centralizado de excepciones.
- Historial de reservas y asignaciones con fechas.
- Pruebas de integración con una base de datos de pruebas.
- Mejoras en las consultas JPA para optimizar el acceso a los datos.
- Paginación y búsqueda de residentes y enfermeros.
- Gestión de habitaciones con capacidad para múltiples residentes.
- Auditoría de operaciones administrativas.
- Uso de DTOs para separar las entidades persistentes de los formularios web.

## 11. Finalidad académica

El objetivo principal del proyecto es integrar los conceptos estudiados durante el desarrollo de aplicaciones Java, desde el modelado orientado a objetos hasta la implementación de una aplicación web con persistencia relacional.

Se busca demostrar la interacción entre Spring MVC, Spring Data JPA, Hibernate, Thymeleaf y MySQL, así como la importancia de separar la lógica de negocio, el acceso a datos y la presentación.

---

**Proyecto académico — Sistema de Gestión de Residencia Geriátrica**

Desarrollado con Java, Spring Boot, Spring Data JPA, Hibernate, MySQL y Thymeleaf.
