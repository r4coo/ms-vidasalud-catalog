# ms-vidasalud-catalog

Catálogo: prestaciones, boxes y cupos. Consume `q.atenciones.confirmar`, descuenta cupo de forma idempotente y publica `atencion.confirmada/rechazada`.

- Puerto: **8087**
- Stack: Spring Boot 4, Java 25, Gradle.
- Base de datos: PostgreSQL (db_catalog) con Flyway (incluye datos semilla).

## Ejecutar
```bash
./gradlew test
./gradlew bootRun     # requiere RabbitMQ/PostgreSQL (ver repo de infraestructura: vidasalud-infra)
docker build -t ms-vidasalud-catalog .
```
Variables de entorno: ver `src/main/resources/application.properties` y el `compose.yaml` del repo de infraestructura.
No subir `.env` ni credenciales.
