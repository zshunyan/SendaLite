-- Forzar UTF-8 en este script de inicialización
SET NAMES 'utf8mb4';
SET CHARACTER SET 'utf8mb4';

-- Datos de ejemplo SendaLite (MySQL 8)
-- Inserta información básica para pruebas
USE sendalite;

-- Usuarios
INSERT INTO usuario (email, password, nombre, avatar, fecha_registro, activo, admin)
VALUES
    ('admin@example.com', 'admin', 'Admin', NULL, '2023-01-10', TRUE, TRUE),
    ('david@example.com', 'hash123', 'David Gragera', NULL, '2023-01-10', TRUE, FALSE),
    ('shunya@example.com', 'hash456', 'Shunya Zhan', NULL, '2023-02-15', TRUE, FALSE),
    ('laura@example.com', 'hash789', 'Laura Gomez', NULL, '2023-03-20', TRUE, FALSE),
    ('jose@example.com', 'hash001', 'José Piñero', NULL, '2023-04-01', TRUE, FALSE),
    ('monica@example.com', 'hash002', 'Mónica López', NULL, '2023-04-02', TRUE, FALSE),
    ('alberto@example.com', 'hash003', 'Alberto Ñúñez', NULL, '2023-04-03', TRUE, FALSE),
    ('ana@example.com', 'hash004', 'Ana María', NULL, '2023-04-04', TRUE, FALSE),
    ('carlos@example.com', 'hash005', 'Carlos Peña', NULL, '2023-04-05', TRUE, FALSE);

-- Si la BD ya existe y quieres aplicar la contraseña/texto plano ahora, ejecuta un UPDATE (ver instrucciones en README o en la respuesta).

-- Rutas (aprox. 20 entradas)
INSERT INTO ruta (id_autor, titulo, descripcion, dificultad, distancia_km, desnivel_m, tiempo_estimado_min, tipo_actividad, ubicacion, coordenadas, fotos, etiquetas, fecha_creacion, fecha_actualizacion, activa)
VALUES
    (9, 'Sendero del Águila', 'Ruta sencilla con vistas al valle.', 'FACIL', 5.2, 120, 90, 'SENDERISMO', 'Valle del Águila', '40.123,-5.123', NULL, 'mirador', '2023-04-01', NULL, TRUE),
    (2, 'Cima del Dragón', 'Ascenso exigente con vistas espectaculares.', 'DIFICIL', 13.4, 900, 300, 'ESCALADA', 'Sierra del Dragón', '40.456,-5.456', NULL, 'montaña', '2023-04-10', NULL, TRUE),
    (3, 'Bosque Encantado', 'Camino entre árboles y pequeños arroyos.', 'MEDIA', 8.1, 350, 180, 'SENDERISMO', 'Bosque Encantado', '40.789,-5.789', NULL, 'bosque', '2023-04-15', NULL, TRUE),
    (4, 'Senda de la Peña', 'Tramo con rocas y buenas vistas del río.', 'MEDIA', 10.0, 420, 200, 'SENDERISMO', 'Peña Alta', '40.200,-5.200', NULL, 'peña', '2023-05-01', NULL, TRUE),
    (5, 'Ruta de la Colmena', 'Paseo corto ideal para familias.', 'FACIL', 3.5, 60, 60, 'SENDERISMO', 'Colmenar', '40.210,-5.210', NULL, 'familiar', '2023-05-02', NULL, TRUE),
    (6, 'Ascenso al Mirador', 'Corto pero con fuerte pendiente.', 'DIFICIL', 6.2, 500, 150, 'SENDERISMO', 'Mirador Norte', '40.220,-5.220', NULL, 'mirador', '2023-05-03', NULL, TRUE),
    (7, 'Camino de las Ánimas', 'Ruta nocturna recomendada con precaución.', 'MEDIA', 12.0, 300, 240, 'SENDERISMO', 'Cerro Negro', '40.230,-5.230', NULL, 'nocturna', '2023-05-04', NULL, TRUE),
    (8, 'Travesía del Roble', 'Paso por bosque y praderas abiertas.', 'MEDIA', 9.5, 200, 210, 'SENDERISMO', 'Valle Roble', '40.240,-5.240', NULL, 'bosque', '2023-05-05', NULL, TRUE),
    (9, 'Camino del Agua', 'Sendero con varios arroyos y zonas húmedas.', 'FACIL', 7.0, 100, 150, 'SENDERISMO', 'Vega del Agua', '40.250,-5.250', NULL, 'agua', '2023-05-06', NULL, TRUE),
    (2, 'Sierra de la Nube', 'Ruta larga con vistas panorámicas.', 'DIFICIL', 20.0, 1200, 480, 'SENDERISMO', 'Sierra Alta', '40.260,-5.260', NULL, 'panorámica', '2023-05-07', NULL, TRUE),
    (3, 'Pico del Halcón', 'Pequeña escalada y cresta aérea.', 'DIFICIL', 5.6, 650, 180, 'ESCALADA', 'Pico Halcón', '40.270,-5.270', NULL, 'escalada', '2023-05-08', NULL, TRUE),
    (4, 'Ruta de los Molinos', 'Recorrido con molinos restaurados.', 'FACIL', 4.2, 80, 70, 'SENDERISMO', 'Molinos Viejos', '40.280,-5.280', NULL, 'cultural', '2023-05-09', NULL, TRUE),
    (5, 'Sendero del Robledal', 'Ideal en otoño por el colorido.', 'FACIL', 6.8, 150, 120, 'SENDERISMO', 'Robledal', '40.290,-5.290', NULL, 'otoño', '2023-05-10', NULL, TRUE),
    (6, 'Camino del Pastoreo', 'Ruta rural entre campos y ganado.', 'FACIL', 11.0, 200, 180, 'SENDERISMO', 'Campos Verdes', '40.300,-5.300', NULL, 'rural', '2023-05-11', NULL, TRUE),
    (7, 'La Senda Perdida', 'Tramo técnico con señalización limitada.', 'DIFICIL', 14.5, 800, 360, 'SENDERISMO', 'Páramo', '40.310,-5.310', NULL, 'técnico', '2023-05-12', NULL, TRUE),
    (8, 'Ruta de la Cueva', 'Entrada a zona kárstica y miradores.', 'MEDIA', 9.0, 320, 200, 'SENDERISMO', 'Cueva del Sol', '40.320,-5.320', NULL, 'cueva', '2023-05-13', NULL, TRUE),
    (4, 'Vuelta al Lago', 'Circuito plano alrededor del lago.', 'FACIL', 2.5, 15, 40, 'SENDERISMO', 'Lago Azul', '40.330,-5.330', NULL, 'lago', '2023-05-14', NULL, TRUE),
    (5, 'Senda de los Puentes', 'Varias pasarelas sobre el río.', 'MEDIA', 7.7, 180, 140, 'SENDERISMO', 'Puentes', '40.340,-5.340', NULL, 'puentes', '2023-05-15', NULL, TRUE),
    (6, 'Cañón del Silencio', 'Tramo espectacular y sonoro.', 'MEDIA', 10.4, 400, 220, 'SENDERISMO', 'Cañón', '40.350,-5.350', NULL, 'cañón', '2023-05-16', NULL, TRUE),
    (7, 'Ruta del Olivar', 'Pistas entre olivares centenarios.', 'FACIL', 8.3, 90, 160, 'SENDERISMO', 'Olivar Viejo', '40.360,-5.360', NULL, 'olivar', '2023-05-17', NULL, TRUE);

-- Comentarios (ejemplos variados)
INSERT INTO comentario (id_usuario, id_ruta, texto, fecha_comentario, fecha_edicion)
VALUES
    (9, 1, '¡Preciosa ruta, muy recomendable!', '2023-04-02', NULL),
    (2, 2, 'Difícil pero merece la pena por las vistas.', '2023-04-11', NULL),
    (3, 3, 'Ideal para ir en familia.', '2023-04-16', NULL),
    (4, 4, 'Rocas resbaladizas en ciertos tramos, llevar cuidado.', '2023-05-02', NULL),
    (5, 5, 'Perfecta para un paseo con niños.', '2023-05-03', NULL),
    (6, 6, 'Muy exigente, recomiendo llevar agua extra.', '2023-05-04', NULL),
    (7, 7, 'Hermosa al atardecer.', '2023-05-05', NULL),
    (8, 8, 'Mucho bosque y sombra, ideal en verano.', '2023-05-06', NULL),
    (9, 9, 'Bastante húmeda en primavera.', '2023-05-07', NULL),
    (2, 10, 'Larga pero con tramos de descanso.', '2023-05-08', NULL);

-- Valoraciones (ejemplos)
INSERT INTO valoracion (id_usuario, id_ruta, puntuacion, fecha_valoracion)
VALUES
    (9, 1, 8, '2023-04-02'),
    (2, 1, 9, '2023-04-03'),
    (3, 2, 10, '2023-04-11'),
    (9, 3, 7, '2023-04-16'),
    (4, 4, 7, '2023-05-02'),
    (5, 5, 9, '2023-05-03'),
    (6, 6, 6, '2023-05-04'),
    (7, 7, 8, '2023-05-05'),
    (8, 8, 8, '2023-05-06'),
    (9, 9, 7, '2023-05-07'),
    (2, 10, 6, '2023-05-08'),
    (3, 11, 9, '2023-05-09'),
    (4, 12, 8, '2023-05-10'),
    (5, 13, 7, '2023-05-11'),
    (6, 14, 8, '2023-05-12'),
    (7, 15, 9, '2023-05-13'),
    (8, 16, 7, '2023-05-14'),
    (9, 17, 8, '2023-05-15'),
    (2, 18, 9, '2023-05-16'),
    (3, 19, 8, '2023-05-17');
