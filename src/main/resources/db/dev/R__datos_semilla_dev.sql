-- SOLO perfil dev. Todas las contrasenas son: Test1234*
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO usuario (id, nombre, correo, telefono, contrasena_hash, rol, estado_cuenta) VALUES
 ('00000000-0000-0000-0000-000000000001','Admin Demo','admin@marketplace.test','3000000001',
   crypt('Test1234*', gen_salt('bf', 10)),'ADMINISTRADOR','ACTIVA'),
 ('00000000-0000-0000-0000-000000000002','Anfitrion Demo','anfitrion@marketplace.test','3000000002',
   crypt('Test1234*', gen_salt('bf', 10)),'ANFITRION','ACTIVA'),
 ('00000000-0000-0000-0000-000000000003','Huesped Demo','huesped@marketplace.test','3000000003',
   crypt('Test1234*', gen_salt('bf', 10)),'HUESPED','ACTIVA'),
 ('00000000-0000-0000-0000-000000000004','Huesped Restringido','restringido@marketplace.test','3000000004',
   crypt('Test1234*', gen_salt('bf', 10)),'HUESPED','RESTRINGIDA'),
 ('00000000-0000-0000-0000-000000000005','Huesped Desactivado','desactivado@marketplace.test','3000000005',
   crypt('Test1234*', gen_salt('bf', 10)),'HUESPED','DESACTIVADA'),
 ('00000000-0000-0000-0000-000000000006','Anfitrion Ajeno','anfitrion2@marketplace.test','3000000006',
   crypt('Test1234*', gen_salt('bf', 10)),'ANFITRION','ACTIVA')
ON CONFLICT DO NOTHING;

INSERT INTO publicacion (id, anfitrion_id, politica_cancelacion_id, titulo, descripcion, ciudad,
                         ubicacion_textual, tipo, capacidad, precio_noche, tarifa_limpieza,
                         reglas, modo_reserva, estado)
SELECT '00000000-0000-0000-0000-0000000000a1',
       '00000000-0000-0000-0000-000000000002',
       (SELECT id FROM politica_cancelacion WHERE nombre = 'FLEXIBLE'),
       'Apartamento demo en Chapinero', 'Publicacion de prueba', 'Bogota',
       'Chapinero, Bogota', 'APARTAMENTO', 4, 180000, 40000, 'No fumar',
       'INMEDIATA', 'ACTIVA'
ON CONFLICT DO NOTHING;

INSERT INTO periodo_disponible (id, publicacion_id, fecha_inicio, fecha_fin) VALUES
 ('00000000-0000-0000-0000-0000000000b1','00000000-0000-0000-0000-0000000000a1','2026-12-01','2026-12-31')
ON CONFLICT DO NOTHING;

INSERT INTO reserva (id, huesped_id, publicacion_id, politica_cancelacion_id, fecha_llegada,
                     fecha_salida, numero_huespedes, estado, clave_idempotencia)
SELECT '00000000-0000-0000-0000-0000000000c1',
       '00000000-0000-0000-0000-000000000003',
       '00000000-0000-0000-0000-0000000000a1',
       (SELECT id FROM politica_cancelacion WHERE nombre = 'FLEXIBLE'),
       '2026-12-10','2026-12-15', 2, 'CONFIRMADA', 'seed-reserva-1'
ON CONFLICT DO NOTHING;
