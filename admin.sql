-- Insert initial admin user
-- Password: admin123 (hashed with BCrypt)
INSERT INTO administrators (nombre, apellido, email, password, is_active)
VALUES (
    'Admin',
    'Principal',
    'admin@rollerspeed.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    true
); 