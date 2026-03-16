-- =========================================
-- CREACIÓN DE TABLAS - BIBLIOTECA
-- PostgreSQL
-- $env:DB_URL="jdbc:postgresql://localhost:5432/mibase"
-- $env:DB_USERNAME="postgres"
-- $env:DB_PASSWORD="mi_password"
-- =========================================

CREATE DATABASE biblioteca;
-- \c biblioteca;

-- =========================================
-- TABLA: Usuario
-- =========================================
CREATE TABLE usuario (
    id_usuario      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    apellidos       VARCHAR(150) NOT NULL,
    direccion       VARCHAR(200)
);

-- =========================================
-- TABLA: Editorial
-- =========================================
CREATE TABLE editorial (
    id_editorial        INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre              VARCHAR(150) NOT NULL,
    pais                VARCHAR(100),
    fecha_fundacion     DATE
);

-- =========================================
-- TABLA: Autor
-- =========================================
CREATE TABLE autor (
    id_autor            INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre              VARCHAR(150) NOT NULL,
    nacionalidad        VARCHAR(100),
    fecha_nacimiento    DATE
);

-- =========================================
-- TABLA: Libro
-- =========================================
CREATE TABLE libro (
    id_libro        INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR(200) NOT NULL,
    tipo            VARCHAR(100),
    anio            INTEGER,
    id_editorial    INTEGER NOT NULL,
    CONSTRAINT fk_libro_editorial
        FOREIGN KEY (id_editorial)
        REFERENCES editorial(id_editorial)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_libro_anio
        CHECK (anio IS NULL OR anio >= 0)
);

-- =========================================
-- TABLA: LibroAutor
-- Tabla puente M:N entre Libro y Autor
-- =========================================
CREATE TABLE libroautor (
    id_libro    INTEGER NOT NULL,
    id_autor    INTEGER NOT NULL,
    PRIMARY KEY (id_libro, id_autor),
    CONSTRAINT fk_libroautor_libro
        FOREIGN KEY (id_libro)
        REFERENCES libro(id_libro)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_libroautor_autor
        FOREIGN KEY (id_autor)
        REFERENCES autor(id_autor)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- =========================================
-- TABLA: Copia
-- =========================================
CREATE TABLE copia (
    id_copia     INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    estado       VARCHAR(50) NOT NULL,
    id_libro     INTEGER NOT NULL,
    CONSTRAINT fk_copia_libro
        FOREIGN KEY (id_libro)
        REFERENCES libro(id_libro)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- =========================================
-- TABLA: Prestamo
-- =========================================
CREATE TABLE prestamo (
    id_prestamo      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fecha_inicio     DATE NOT NULL,
    fecha_fin        DATE,
    id_usuario       INTEGER NOT NULL,
    id_copia         INTEGER NOT NULL,
    CONSTRAINT fk_prestamo_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_prestamo_copia
        FOREIGN KEY (id_copia)
        REFERENCES copia(id_copia)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_prestamo_fechas
        CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
);

-- =========================================
-- TABLA: Multa
-- =========================================
CREATE TABLE multa (
    id_multa        INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fecha           DATE NOT NULL,
    estatus         VARCHAR(50) NOT NULL,
    id_usuario      INTEGER NOT NULL,
    id_prestamo     INTEGER NOT NULL UNIQUE,
    CONSTRAINT fk_multa_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_multa_prestamo
        FOREIGN KEY (id_prestamo)
        REFERENCES prestamo(id_prestamo)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- =========================================
-- ÍNDICES RECOMENDADOS
-- =========================================
CREATE INDEX idx_libro_id_editorial ON libro(id_editorial);
CREATE INDEX idx_copia_id_libro ON copia(id_libro);
CREATE INDEX idx_prestamo_id_usuario ON prestamo(id_usuario);
CREATE INDEX idx_prestamo_id_copia ON prestamo(id_copia);
CREATE INDEX idx_multa_id_usuario ON multa(id_usuario);
CREATE INDEX idx_multa_id_prestamo ON multa(id_prestamo);
CREATE INDEX idx_libroautor_id_autor ON libroautor(id_autor);
