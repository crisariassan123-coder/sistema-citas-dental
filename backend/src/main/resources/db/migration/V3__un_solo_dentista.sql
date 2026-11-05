-- Por ahora el consultorio trabaja con un solo dentista (Dra. Laura Méndez, nombre de ejemplo).
-- El segundo dentista de ejemplo se desactiva, no se borra: así se conserva su información
-- y basta con poner activo = TRUE si en el futuro se integra al consultorio.
UPDATE dentistas SET activo = FALSE WHERE id = 2;

-- Para agregar otro dentista en el futuro:
--   INSERT INTO dentistas (nombre, especialidad) VALUES ('Nombre', 'Especialidad');
--   y después sus filas en horarios_laborales.
