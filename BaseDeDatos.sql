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
    username        VARCHAR(50) NOT NULL UNIQUE,
    contrasena      VARCHAR(100) NOT NULL,
    nombre          VARCHAR(100) NOT NULL,
    apellidos       VARCHAR(150) NOT NULL,
    tipo            VARCHAR(50) NOT NULL,
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
    isbn            VARCHAR(20),
    sinopsis        TEXT,
    num_paginas     INTEGER,
    precio          NUMERIC(10,2),
    num_copias      INTEGER DEFAULT 0,
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
    id_copia                INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    estado                  VARCHAR(50) NOT NULL,
    ubicacion_biblioteca    VARCHAR(100),
    id_libro                INTEGER NOT NULL,
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
-- TABLA: Permisos
-- =========================================
CREATE TABLE permiso (
    id_permiso INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(200)
);

INSERT INTO permiso (nombre, descripcion) VALUES
('gestionar_usuarios', 'Permite crear, editar o eliminar usuarios'),
('gestionar_libros', 'Permite crear, editar o eliminar libros'),
('buscar_libros', 'Permite consultar libros'),
('prestar_libros', 'Permite registrar prestamos'),
('devolver_libros', 'Permite registrar devoluciones');


-- =========================================
-- TABLA: Usuario_permiso
-- =========================================
CREATE TABLE usuario_permiso (
    id_usuario INTEGER NOT NULL,
    id_permiso INTEGER NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_usuario, id_permiso),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_permiso) REFERENCES permiso(id_permiso) ON DELETE CASCADE
);

-- =========================================
-- DATOS INICIALES: Editoriales
-- =========================================
INSERT INTO editorial (nombre, pais, fecha_fundacion) VALUES
('Horizonte Academico', 'Mexico', '1984-05-12'),
('Faro del Conocimiento', 'Colombia', '1992-09-01'),
('Ediciones Aurora', 'Espana', '1978-03-18'),
('Casa Andina', 'Peru', '1988-07-09'),
('Puerto Norte Editores', 'Argentina', '1996-11-21'),
('Delta Tecnica', 'Chile', '2003-02-14'),
('Tinta Universitaria', 'Mexico', '2008-08-25'),
('Brujula Editorial', 'Espana', '1981-01-30'),
('Marea Libros', 'Uruguay', '2001-06-16'),
('Nexo Global Press', 'Estados Unidos', '2010-04-08');

-- =========================================
-- DATOS INICIALES: Autores
-- =========================================
INSERT INTO autor (nombre, nacionalidad, fecha_nacimiento) VALUES
('Elena Robles', 'Mexicana', '1975-02-14'),
('Tomas Villarreal', 'Mexicana', '1968-09-30'),
('Camila Duarte', 'Colombiana', '1982-05-11'),
('Mateo Salcedo', 'Peruana', '1979-12-04'),
('Isabel Ferrer', 'Espanola', '1971-03-27'),
('Andres Quiroga', 'Argentina', '1985-07-19'),
('Laura Benitez', 'Mexicana', '1990-10-08'),
('Joaquin Mena', 'Chilena', '1966-01-22'),
('Sofia Larrain', 'Chilena', '1988-04-15'),
('Daniel Escudero', 'Espanola', '1977-06-02'),
('Paula Rivas', 'Uruguaya', '1984-08-23'),
('Nicolas Serrano', 'Mexicana', '1973-11-13'),
('Teresa Villalobos', 'Costarricense', '1969-02-07'),
('Martin Cedeno', 'Ecuatoriana', '1981-09-28'),
('Renata Godoy', 'Brasilena', '1987-12-19'),
('Hugo Santamaria', 'Mexicana', '1970-05-30'),
('Valeria Ponce', 'Mexicana', '1992-01-17'),
('Sergio Balmaceda', 'Espanola', '1978-07-05'),
('Adriana Leal', 'Colombiana', '1986-10-26'),
('Emilio Navarro', 'Argentina', '1974-04-09');

-- =========================================
-- DATOS INICIALES: Libros
-- =========================================
INSERT INTO libro (nombre, tipo, anio, id_editorial) VALUES
('Fundamentos de Programacion Universitaria', 'Tecnologia', 2019, 7),
('Arquitecturas de Datos Modernas', 'Tecnologia', 2021, 6),
('Introduccion a las Redes Seguras', 'Tecnologia', 2020, 6),
('Inteligencia Artificial Aplicada', 'Tecnologia', 2022, 10),
('Analisis Financiero para Emprendedores', 'Negocios', 2018, 5),
('Gestion de Proyectos Academicos', 'Administracion', 2017, 7),
('Economia para no Economistas', 'Economia', 2016, 3),
('Marketing Digital en Accion', 'Negocios', 2021, 10),
('Taller de Escritura Academica', 'Educacion', 2019, 1),
('Didactica para el Aula Hibrida', 'Educacion', 2023, 1),
('Historia Breve del Norte de Mexico', 'Historia', 2015, 2),
('Cronicas del Puerto Antiguo', 'Novela historica', 2014, 5),
('Atlas Cultural de America Latina', 'Historia', 2020, 8),
('Filosofia para Tiempos Digitales', 'Ensayo', 2022, 3),
('Etica Profesional Contemporanea', 'Ensayo', 2018, 7),
('Psicologia del Aprendizaje', 'Psicologia', 2021, 1),
('Bienestar y Habitos Sostenibles', 'Salud', 2020, 9),
('Introduccion a la Biotecnologia', 'Ciencia', 2019, 6),
('Laboratorio de Quimica General', 'Ciencia', 2017, 2),
('Fisica para Ingenierias', 'Ciencia', 2016, 7),
('Calculo y Modelacion Basica', 'Matematicas', 2018, 6),
('Estadistica con Casos Reales', 'Matematicas', 2022, 10),
('Algebra Lineal Paso a Paso', 'Matematicas', 2015, 3),
('Introduccion al Derecho Universitario', 'Derecho', 2019, 4),
('Politicas Publicas y Ciudadania', 'Ciencias sociales', 2021, 8),
('Sociologia de la Vida Cotidiana', 'Ciencias sociales', 2018, 9),
('Investigacion Cualitativa Aplicada', 'Metodologia', 2020, 1),
('Metodos Cuantitativos para Ciencias Sociales', 'Metodologia', 2023, 10),
('Manual de Emprendimiento Universitario', 'Negocios', 2017, 5),
('Contabilidad Basica para Proyectos', 'Negocios', 2016, 4),
('Diseno Centrado en las Personas', 'Diseno', 2022, 9),
('Narrativas Visuales Contemporaneas', 'Arte', 2021, 8),
('Historia del Cine Iberoamericano', 'Arte', 2019, 3),
('Fotografia Documental en Campo', 'Arte', 2020, 5),
('Urbanismo y Espacio Publico', 'Arquitectura', 2018, 4),
('Materiales para Arquitectura Sustentable', 'Arquitectura', 2023, 6),
('Literatura Latinoamericana Actual', 'Literatura', 2022, 8),
('Cuentos del Patio Central', 'Literatura', 2016, 9),
('Novela Corta del Invierno Azul', 'Literatura', 2015, 2),
('Logistica y Cadena de Suministro', 'Ingenieria', 2021, 10),
('Introduccion a los Sistemas Embebidos', 'Ingenieria', 2022, 6),
('Energias Renovables para la Ciudad', 'Ingenieria', 2020, 7),
('Mecanica Aplicada al Diseno', 'Ingenieria', 2018, 4),
('Administracion del Talento Humano', 'Administracion', 2019, 5),
('Liderazgo para Equipos de Alto Desempeno', 'Administracion', 2023, 10),
('Comunicacion Oral y Presentaciones', 'Comunicacion', 2017, 1),
('Produccion Editorial Universitaria', 'Comunicacion', 2018, 7),
('Bases de Datos Relacionales', 'Tecnologia', 2021, 6),
('Desarrollo Web con Buenas Practicas', 'Tecnologia', 2023, 7),
('Ciberseguridad para Instituciones Educativas', 'Tecnologia', 2024, 10);

-- =========================================
-- DATOS INICIALES: Relacion Libro-Autor
-- =========================================
INSERT INTO libroautor (id_libro, id_autor) VALUES
(1, 7),
(2, 1),
(3, 12),
(4, 17),
(5, 6),
(6, 20),
(7, 5),
(8, 15),
(9, 13),
(10, 17),
(11, 2),
(12, 11),
(13, 18),
(14, 10),
(15, 16),
(16, 3),
(17, 19),
(18, 4),
(19, 8),
(20, 12),
(21, 2),
(22, 1),
(23, 16),
(24, 13),
(25, 11),
(26, 19),
(27, 5),
(28, 14),
(29, 20),
(30, 6),
(31, 15),
(32, 9),
(33, 18),
(34, 17),
(35, 10),
(36, 4),
(37, 5),
(38, 11),
(39, 7),
(40, 20),
(41, 12),
(42, 14),
(43, 8),
(44, 3),
(45, 1),
(46, 13),
(47, 18),
(48, 16),
(49, 7),
(50, 12);

-- =========================================
-- DATOS INICIALES: Copias
-- =========================================
INSERT INTO copia (estado, id_libro)
SELECT
    CASE
        WHEN id_libro % 11 = 0 THEN 'mantenimiento'
        WHEN id_libro % 7 = 0 THEN 'prestado'
        ELSE 'disponible'
    END,
    id_libro
FROM libro;

INSERT INTO copia (estado, id_libro)
SELECT 'disponible', id_libro
FROM libro
WHERE id_libro <= 20;

-- =========================================
-- DATOS GENÉRICOS: Campos nuevos de libro
-- =========================================
UPDATE libro SET
    isbn         = 'ISBN-978-' || LPAD(id_libro::TEXT, 2, '0') || '-'
                   || LPAD((COALESCE(anio, 2000) % 100)::TEXT, 2, '0') || '-001',
    sinopsis     = 'Este libro aborda temas relacionados con ' || tipo
                   || '. Una obra esencial para quienes desean profundizar en la materia y ampliar sus conocimientos.',
    num_paginas  = 120 + ((id_libro * 17) % 380),
    precio       = ROUND(((50 + (id_libro * 13) % 450))::NUMERIC, 2);

-- num_copias refleja el conteo real de copias insertadas
UPDATE libro SET num_copias = (
    SELECT COUNT(*) FROM copia WHERE copia.id_libro = libro.id_libro
);

-- =========================================
-- DATOS GENÉRICOS: Ubicación de cada copia
-- =========================================
UPDATE copia SET
    ubicacion_biblioteca = 'Estante '
        || CHR(64 + ((id_copia - 1) % 8 + 1))
        || '-'
        || LPAD(((id_copia - 1) % 12 + 1)::TEXT, 2, '0');

-- =========================================
-- DATOS INICIALES: Usuarios demo
-- Contrasenas en texto plano solo para desarrollo
-- =========================================
INSERT INTO usuario (username, contrasena, nombre, apellidos, tipo, direccion) VALUES
('usuario', '111', 'Lucia', 'Martinez Vega', 'usuario', 'Residencias Norte 120, Monterrey, NL'),
('bibliotecario', '222', 'Rafael', 'Ortega Salinas', 'bibliotecario', 'Av. Biblioteca 45, Monterrey, NL'),
('admin', '333', 'Valeria', 'Campos Lozano', 'admin', 'Blvd. Rectorado 900, Monterrey, NL');

-- =========================================
-- DATOS INICIALES: Permisos por usuario
-- =========================================
INSERT INTO usuario_permiso (id_usuario, id_permiso, activo)
SELECT u.id_usuario, p.id_permiso, TRUE
FROM usuario u
JOIN permiso p ON p.nombre IN ('buscar_libros', 'prestar_libros', 'devolver_libros')
WHERE u.username = 'usuario';

INSERT INTO usuario_permiso (id_usuario, id_permiso, activo)
SELECT u.id_usuario, p.id_permiso, TRUE
FROM usuario u
JOIN permiso p ON p.nombre IN ('gestionar_libros', 'buscar_libros', 'prestar_libros', 'devolver_libros')
WHERE u.username = 'bibliotecario';

INSERT INTO usuario_permiso (id_usuario, id_permiso, activo)
SELECT u.id_usuario, p.id_permiso, TRUE
FROM usuario u
JOIN permiso p ON TRUE
WHERE u.username = 'admin';

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
