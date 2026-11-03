CREATE TABLE prestaciones (
    id           BIGSERIAL PRIMARY KEY,
    nombre       VARCHAR(120) NOT NULL UNIQUE,
    descripcion  VARCHAR(255),
    duracion_min INT          NOT NULL DEFAULT 30,
    activa       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE boxes (
    id      BIGSERIAL PRIMARY KEY,
    nombre  VARCHAR(80) NOT NULL UNIQUE,
    activo  BOOLEAN     NOT NULL DEFAULT TRUE
);

-- Cupos disponibles de un box para una prestación en una fecha y hora.
CREATE TABLE cupos (
    id            BIGSERIAL PRIMARY KEY,
    prestacion_id BIGINT NOT NULL REFERENCES prestaciones (id),
    box_id        BIGINT NOT NULL REFERENCES boxes (id),
    fecha         DATE   NOT NULL,
    hora          TIME   NOT NULL,
    disponibles   INT    NOT NULL CHECK (disponibles >= 0),
    CONSTRAINT uq_cupo UNIQUE (prestacion_id, box_id, fecha, hora)
);
CREATE INDEX idx_cupos_busqueda ON cupos (prestacion_id, fecha, hora);

-- Una reserva por atención: hace idempotente el consumo del comando.
CREATE TABLE reservas (
    id          BIGSERIAL PRIMARY KEY,
    atencion_id UUID      NOT NULL UNIQUE,
    cupo_id     BIGINT    NOT NULL REFERENCES cupos (id),
    creada_en   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
