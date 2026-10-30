-- Extensión necesaria para la restricción anti-empalmes
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- Personal del consultorio que inicia sesión
CREATE TABLE usuarios (
    id             BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    rol            VARCHAR(20)  NOT NULL CHECK (rol IN ('ADMIN', 'RECEPCIONISTA', 'DENTISTA')),
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE dentistas (
    id            BIGSERIAL PRIMARY KEY,
    usuario_id    BIGINT UNIQUE REFERENCES usuarios(id),
    nombre        VARCHAR(100) NOT NULL,
    especialidad  VARCHAR(100),
    activo        BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE servicios (
    id                   BIGSERIAL PRIMARY KEY,
    nombre               VARCHAR(100) NOT NULL UNIQUE,
    descripcion          TEXT,
    duracion_minutos     INT NOT NULL CHECK (duracion_minutos > 0),
    precio               NUMERIC(10, 2) NOT NULL CHECK (precio >= 0),
    reservable_en_linea  BOOLEAN NOT NULL DEFAULT TRUE,
    activo               BOOLEAN NOT NULL DEFAULT TRUE
);

-- Horario semanal de cada dentista (1 = lunes ... 7 = domingo)
CREATE TABLE horarios_laborales (
    id           BIGSERIAL PRIMARY KEY,
    dentista_id  BIGINT NOT NULL REFERENCES dentistas(id),
    dia_semana   SMALLINT NOT NULL CHECK (dia_semana BETWEEN 1 AND 7),
    hora_inicio  TIME NOT NULL,
    hora_fin     TIME NOT NULL,
    CHECK (hora_fin > hora_inicio)
);

-- Vacaciones, cursos o comidas en las que el dentista no atiende
CREATE TABLE bloqueos (
    id           BIGSERIAL PRIMARY KEY,
    dentista_id  BIGINT NOT NULL REFERENCES dentistas(id),
    inicio       TIMESTAMP NOT NULL,
    fin          TIMESTAMP NOT NULL,
    motivo       VARCHAR(200),
    CHECK (fin > inicio)
);

CREATE TABLE pacientes (
    id                BIGSERIAL PRIMARY KEY,
    nombre            VARCHAR(100) NOT NULL,
    apellidos         VARCHAR(100) NOT NULL,
    telefono          VARCHAR(20)  NOT NULL,
    email             VARCHAR(150),
    fecha_nacimiento  DATE,
    alergias          TEXT,
    creado_en         TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE citas (
    id              BIGSERIAL PRIMARY KEY,
    paciente_id     BIGINT NOT NULL REFERENCES pacientes(id),
    dentista_id     BIGINT NOT NULL REFERENCES dentistas(id),
    servicio_id     BIGINT NOT NULL REFERENCES servicios(id),
    inicio          TIMESTAMP NOT NULL,
    fin             TIMESTAMP NOT NULL,
    estado          VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                    CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'ATENDIDA', 'CANCELADA', 'NO_ASISTIO')),
    motivo          VARCHAR(255),
    token_publico   VARCHAR(64) NOT NULL UNIQUE,
    creado_en       TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado_en  TIMESTAMP NOT NULL DEFAULT NOW(),
    CHECK (fin > inicio),
    -- Un dentista no puede tener dos citas activas que se encimen
    CONSTRAINT citas_sin_empalmes EXCLUDE USING gist (
        dentista_id WITH =,
        tsrange(inicio, fin) WITH &&
    ) WHERE (estado <> 'CANCELADA')
);

CREATE INDEX idx_citas_paciente    ON citas (paciente_id);
CREATE INDEX idx_horarios_dentista ON horarios_laborales (dentista_id, dia_semana);
CREATE INDEX idx_bloqueos_dentista ON bloqueos (dentista_id, inicio);