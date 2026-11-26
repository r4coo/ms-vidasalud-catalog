# ms-vidasalud-catalog

Catálogo: prestaciones, boxes y cupos. Consume `q.atenciones.confirmar`, descuenta cupo de forma idempotente y publica `atencion.confirmada/rechazada`. Tras cada reserva publica los cupos restantes en Kafka.

- Puerto: **8087**
- Stack: Spring Boot 4, Java 25, Gradle.
- Base de datos: PostgreSQL (db_catalog) con Flyway (incluye datos semilla).
- RabbitMQ: consume `q.atenciones.confirmar` (ACK manual, reintentos y DLQ); un `DlqListener` registra los mensajes muertos. Se desactiva con `DLQ_LISTENER_ENABLED=false` para ver la DLQ en la consola de RabbitMQ.
- Kafka (productor): publica en `vidasalud.catalogo.cupos` (tópico `compact`) con clave `prestacionId|fecha|hora`, de modo que Kafka conserva el último valor de cupos disponibles por horario.

## Ejecutar
```bash
./gradlew test
./gradlew bootRun     # requiere RabbitMQ/PostgreSQL (ver repo de infraestructura: vidasalud-infra)
docker build -t ms-vidasalud-catalog .
```
Variables de entorno: ver `src/main/resources/application.properties` y el `compose.yaml` del repo de infraestructura.
No subir `.env` ni credenciales.
