USE `mws_test_db`;

-- Account data
------------------
-- Tenant
INSERT INTO tenant(id, name, active) VALUES (1,'Tenant', true);
INSERT INTO tenant(id, name, active) VALUES (2,'Deleted tenant', false);
INSERT INTO tenant(id, name, active) VALUES (3,'Tenant to delete', true);

-- User
INSERT INTO user(id, role, email, password, first_name, last_name)
    VALUES (1,'USER','user@mwstest.com','$2a$10$m5Kx4KMZ8zvlogNdWh/Ooe1qyjqZITUNMfFWhHp7kxwGCLFpsXfdW','First name','Last name');
INSERT INTO user(id, role, email, password, first_name, last_name)
    VALUES (2,'ADMIN','admin@mwstest.com','$2a$10$m5Kx4KMZ8zvlogNdWh/Ooe1qyjqZITUNMfFWhHp7kxwGCLFpsXfdW','First name','Last name');
INSERT INTO user(id, role, email, password, first_name, last_name)
    VALUES (3,'SUPER','super@mwstest.com','$2a$10$m5Kx4KMZ8zvlogNdWh/Ooe1qyjqZITUNMfFWhHp7kxwGCLFpsXfdW','First name','Last name');


-- Product data
------------------
INSERT INTO product(id, name, image, active)
    VALUES (1,'Product name','product-image', true);
INSERT INTO product(id, name, image, active)
    VALUES (2,'Product to delete','product-image', true);
INSERT INTO product(id, name, image, active)
   VALUES (3,'Deleted product','product-image', false);

