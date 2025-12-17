-- Esquema adaptado a las entidades Java
CREATE DATABASE IF NOT EXISTS `sendalite` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sendalite;

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    avatar VARCHAR(255),
    fecha_registro DATE NOT NULL,
    activo BOOLEAN NOT NULL,
    admin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS ruta (
    id_ruta BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_autor BIGINT NOT NULL,
    titulo VARCHAR(100) NOT NULL,
    descripcion TEXT,
    dificultad ENUM('FACIL','MEDIA','DIFICIL') NOT NULL,
    distancia_km DECIMAL(5,2),
    desnivel_m INT,
    tiempo_estimado_min INT,
    tipo_actividad ENUM('SENDERISMO','CICLISMO','ESCALADA','CORRER','OTRO') NOT NULL,
    ubicacion VARCHAR(255),
    coordenadas VARCHAR(255),
    fotos TEXT,
    etiquetas TEXT,
    fecha_creacion DATE NOT NULL,
    fecha_actualizacion DATE,
    activa BOOLEAN NOT NULL,
    CONSTRAINT fk_ruta_autor FOREIGN KEY (id_autor) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS comentario (
    id_comentario BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    id_ruta BIGINT NOT NULL,
    texto TEXT NOT NULL,
    fecha_comentario DATE NOT NULL,
    fecha_edicion DATE,
    CONSTRAINT fk_comentario_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_comentario_ruta FOREIGN KEY (id_ruta) REFERENCES ruta(id_ruta)
);

CREATE TABLE IF NOT EXISTS valoracion (
    id_valoracion BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    id_ruta BIGINT NOT NULL,
    puntuacion TINYINT NOT NULL CHECK (puntuacion BETWEEN 1 AND 10),
    fecha_valoracion DATE NOT NULL,
    CONSTRAINT uq_valoracion UNIQUE (id_usuario, id_ruta),
    CONSTRAINT fk_valoracion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_valoracion_ruta FOREIGN KEY (id_ruta) REFERENCES ruta(id_ruta)
);

-- Si usas favoritos, etiquetas, etc. puedes añadir las tablas extra aquí
