UPDATE courses SET name = CASE name
    WHEN 'Algoritmi fundamentali' THEN 'Fundamental Algorithms'
    WHEN 'Baze de date' THEN 'Databases'
    WHEN 'Programare orientată pe obiecte' THEN 'Object-Oriented Programming'
    WHEN 'Sisteme de operare' THEN 'Operating Systems'
    ELSE name END,
description = CASE description
    WHEN 'Structuri de date și analiza algoritmilor' THEN 'Data structures and algorithm analysis'
    WHEN 'Modelare relațională, SQL și PostgreSQL' THEN 'Relational modelling, SQL and PostgreSQL'
    WHEN 'Java, principii OOP și proiectare software' THEN 'Java, OOP principles and software design'
    WHEN 'Procese, fire de execuție și gestionarea memoriei' THEN 'Processes, threads and memory management'
    ELSE description END,
faculty = CASE WHEN faculty = 'Facultatea de Automatică și Calculatoare' THEN 'Faculty of Automation and Computer Science' ELSE faculty END,
department = CASE WHEN department = 'Calculatoare' THEN 'Computer Science' ELSE department END;

UPDATE assignments SET title = CASE title
    WHEN 'Proiect: aplicație Java' THEN 'Project: Java Application'
    WHEN 'Laborator moștenire și interfețe' THEN 'Inheritance and Interfaces Lab'
    WHEN 'Tema SQL: interogări și join-uri' THEN 'SQL Assignment: Queries and Joins'
    WHEN 'Proiectarea schemei relaționale' THEN 'Relational Schema Design'
    WHEN 'Implementare grafuri' THEN 'Graph Implementation'
    WHEN 'Laborator procese și thread-uri' THEN 'Processes and Threads Lab'
    ELSE title END,
description = CASE WHEN description = 'Detaliile complete vor fi publicate la curs.' THEN 'Full details will be published in the course.' ELSE description END;

UPDATE notifications SET title = CASE title
    WHEN 'Material nou' THEN 'New material'
    WHEN 'Temă nouă' THEN 'New assignment'
    WHEN 'Deadline apropiat' THEN 'Upcoming deadline'
    ELSE title END;
