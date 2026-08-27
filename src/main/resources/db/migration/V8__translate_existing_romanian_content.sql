UPDATE courses
SET name = 'Computer Architecture',
    faculty = 'Computer Science and Information Technology'
WHERE name = 'Arhitectura Calculatoarelor';

UPDATE notifications SET message = replace(message, 'Algoritmi fundamentali', 'Fundamental Algorithms');
UPDATE notifications SET message = replace(message, 'Baze de date', 'Databases');
UPDATE notifications SET message = replace(message, 'Programare orientată pe obiecte', 'Object-Oriented Programming');
UPDATE notifications SET message = replace(message, 'Sisteme de operare', 'Operating Systems');
UPDATE notifications SET message = replace(message, 'Arhitectura Calculatoarelor', 'Computer Architecture');
