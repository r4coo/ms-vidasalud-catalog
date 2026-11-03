INSERT INTO prestaciones (nombre, descripcion, duracion_min) VALUES
 ('Medicina general', 'Consulta médica general', 30),
 ('Pediatría', 'Atención infantil', 30),
 ('Cardiología', 'Control y evaluación cardiovascular', 40),
 ('Ginecología', 'Salud de la mujer', 30),
 ('Salud mental', 'Psicología y psiquiatría', 45),
 ('Dermatología', 'Consulta de piel', 30);

INSERT INTO boxes (nombre) VALUES ('Box 1'), ('Box 2'), ('Box 3'), ('Box 4');

-- Cupos de los próximos 45 días (lunes a sábado), un box por prestación.
INSERT INTO cupos (prestacion_id, box_id, fecha, hora, disponibles)
SELECT p.id, ((p.id - 1) % 4) + 1, d::date, h.hora, 1
FROM prestaciones p
CROSS JOIN generate_series(CURRENT_DATE, CURRENT_DATE + 45, INTERVAL '1 day') AS d
CROSS JOIN (VALUES ('09:00'::time), ('09:30'), ('10:00'), ('10:30'), ('11:00'), ('11:30'),
                   ('14:00'), ('14:30'), ('15:00')) AS h(hora)
WHERE EXTRACT(ISODOW FROM d) <> 7;
