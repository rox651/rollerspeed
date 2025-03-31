DROP USER IF EXISTS 'user_rollerspeed'@'localhost';

DROP DATABASE IF EXISTS rollerspeed;
CREATE DATABASE rollerspeed;
CREATE USER 'user_rollerspeed'@'localhost' IDENTIFIED BY '12345';

GRANT ALL PRIVILEGES ON rollerspeed.* TO 'user_rollerspeed'@'localhost';

FLUSH PRIVILEGES;

SHOW GRANTS FOR 'user_rollerspeed'@'localhost';