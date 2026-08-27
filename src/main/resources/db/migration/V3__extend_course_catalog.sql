ALTER TABLE courses
    ADD COLUMN ects INTEGER NOT NULL DEFAULT 5 CHECK (ects BETWEEN 1 AND 30),
    ADD COLUMN faculty VARCHAR(160) NOT NULL DEFAULT 'Facultatea de Automatică și Calculatoare',
    ADD COLUMN department VARCHAR(160) NOT NULL DEFAULT 'Calculatoare',
    ADD COLUMN capacity INTEGER NOT NULL DEFAULT 120 CHECK (capacity > 0);

