USE `mws_test_db`;

--- Account data
------------------
-- Tenant
INSERT INTO tenant(tenant_id, name, active) VALUES (1,'MWS Tenant', true);
INSERT INTO tenant(tenant_id, name, active) VALUES (2,'Deleted tenant', false);
INSERT INTO tenant(tenant_id, name, active) VALUES (3,'Tenant to delete', true);

-- User
INSERT INTO user(tenant_id, id, role, email, password, first_name, last_name)
    VALUES (1, 1,'USER','user@mwstest.com','$2a$10$m5Kx4KMZ8zvlogNdWh/Ooe1qyjqZITUNMfFWhHp7kxwGCLFpsXfdW','First name','Last name');
INSERT INTO user(tenant_id, id, role, email, password, first_name, last_name)
    VALUES (1, 2,'ADMIN','admin@mwstest.com','$2a$10$m5Kx4KMZ8zvlogNdWh/Ooe1qyjqZITUNMfFWhHp7kxwGCLFpsXfdW','First name','Last name');
INSERT INTO user(tenant_id, id, role, email, password, first_name, last_name)
    VALUES (1, 3,'SUPER','super@mwstest.com','$2a$10$m5Kx4KMZ8zvlogNdWh/Ooe1qyjqZITUNMfFWhHp7kxwGCLFpsXfdW','First name','Last name');


--- Product data
------------------
INSERT INTO product(tenant_id, id, name, image, description, active) VALUES (1,1,'Product name','image1', 'desc.1', true);
INSERT INTO product(tenant_id, id, name, image, description, active) VALUES (1,2,'Product to delete','image2', 'desc.2', true);
INSERT INTO product(tenant_id, id, name, image, description, active) VALUES (1,3,'Deleted product','image3', 'desc.3', false);
