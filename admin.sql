-- Password: admin123 (hashed with BCrypt)
INSERT INTO administrators (nombre, apellido, email, password, is_active)
VALUES (
    'Admin',
    'Principal',
    'admin@rollerspeed.com',
    '$2a$10$oEjsWUW1NPkM6sJgJWH2fev9.pz34DGv5GLaHCa8gcZISEz7vlnHW',
    true
); 