INSERT INTO servicios (nombre, descripcion, duracion_minutos, precio, reservable_en_linea) VALUES
('Valoración',        'Revisión inicial y diagnóstico',          30,  300.00, TRUE),
('Limpieza dental',   'Profilaxis y eliminación de sarro',        45,  600.00, TRUE),
('Blanqueamiento',    'Blanqueamiento dental en consultorio',     60, 2500.00, TRUE),
('Resina',            'Restauración de caries con resina',        60,  800.00, FALSE),
('Extracción simple', 'Extracción de pieza dental sin cirugía',   45,  700.00, FALSE),
('Endodoncia',        'Tratamiento de conductos',                 90, 3500.00, FALSE);

INSERT INTO dentistas (nombre, especialidad) VALUES
('Dra. Laura Méndez', 'Odontología general'),
('Dr. Jorge Ramírez', 'Endodoncia');

-- Lunes a viernes: 9:00-14:00 y 16:00-19:00
INSERT INTO horarios_laborales (dentista_id, dia_semana, hora_inicio, hora_fin)
SELECT d.id, dia, h.inicio, h.fin
FROM dentistas d
CROSS JOIN generate_series(1, 5) AS dia
CROSS JOIN (VALUES (TIME '09:00', TIME '14:00'),
                   (TIME '16:00', TIME '19:00')) AS h(inicio, fin);

-- Sábado: 9:00-14:00
INSERT INTO horarios_laborales (dentista_id, dia_semana, hora_inicio, hora_fin)
SELECT id, 6, TIME '09:00', TIME '14:00' FROM dentistas;