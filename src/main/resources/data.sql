-- Datos de ejemplo para desarrollo

-- Usuario administrador de prueba
INSERT INTO usuario (id_usuario, email, password, nombre, avatar, fecha_registro, activo, admin) VALUES
(99, 'admin@example.com', 'admin', 'Admin', NULL, '2025-10-21', true, true);

INSERT INTO usuario (id_usuario, email, password, nombre, avatar, fecha_registro, activo) VALUES
(1, 'alice@example.com', 'pwd', 'Alice', NULL, '2025-10-21', true),
(2, 'bob@example.com', 'pwd', 'Bob', NULL, '2025-10-21', true);

INSERT INTO ruta (id_ruta, id_autor, titulo, descripcion, dificultad, distancia_km, desnivel_m, tiempo_estimado_min, tipo_actividad, ubicacion, coordenadas, fotos, etiquetas, fecha_creacion, activa) VALUES
(1, 1, 'Subida al Pico', 'Ruta corta pero intensa', 'MEDIA', 5.50, 450, 120, 'SENDERISMO', 'Sierra', NULL, NULL, 'montaña,bosque', '2025-10-21', true),
(2, 2, 'Camino del Río', 'Tramo agradable junto al río', 'FACIL', 10.00, 50, 180, 'SENDERISMO', 'Valle', NULL, NULL, 'familia,agua', '2025-10-21', true);

INSERT INTO comentario (id_comentario, id_usuario, id_ruta, texto, fecha_comentario) VALUES
(1, 2, 1, 'Preciosa ruta, con buenos tramos de sombra.', '2025-10-21');

INSERT INTO valoracion (id_valoracion, id_usuario, id_ruta, puntuacion, fecha_valoracion) VALUES
(1, 2, 1, 9, '2025-10-21');
