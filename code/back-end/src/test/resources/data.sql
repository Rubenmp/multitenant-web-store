USE `mws_test_db`;

-- Account data
INSERT INTO user(id, email, password, first_name, last_name)
    VALUES (1,'user@mwstest.com','$2a$10$m5Kx4KMZ8zvlogNdWh/Ooe1qyjqZITUNMfFWhHp7kxwGCLFpsXfdW','First name','Last name');


-- Product data
INSERT INTO product(id, name, image, active)
    VALUES (1,'Product name','product-image', true);

INSERT INTO product(id, name, image, active)
    VALUES (2,'Product to delete','product-image', true);

