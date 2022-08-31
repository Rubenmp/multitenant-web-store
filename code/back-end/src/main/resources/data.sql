USE `mws_db`;

--- Account data
------------------
-- Tenant
INSERT INTO tenant(tenant_id, name, active) VALUES (1,'MWS Meta Tenant', true);
INSERT INTO tenant(tenant_id, name, active) VALUES (2,'Tenant', true);

-- User
INSERT INTO user(tenant_id, id, role, email, password, first_name, last_name, active)
    VALUES (1, 1,'SUPER','super@mwstest.com','$2a$10$m5Kx4KMZ8zvlogNdWh/Ooe1qyjqZITUNMfFWhHp7kxwGCLFpsXfdW','First name','Last name', true);
INSERT INTO user(tenant_id, id, role, email, password, first_name, last_name, active)
    VALUES (2, 2,'USER','user@mwstest.com','$2a$10$m5Kx4KMZ8zvlogNdWh/Ooe1qyjqZITUNMfFWhHp7kxwGCLFpsXfdW','First name','Last name', true);
INSERT INTO user(tenant_id, id, role, email, password, first_name, last_name, active)
    VALUES (2, 3,'ADMIN','admin@mwstest.com','$2a$10$m5Kx4KMZ8zvlogNdWh/Ooe1qyjqZITUNMfFWhHp7kxwGCLFpsXfdW','First name','Last name', true);


--- Product data
------------------
INSERT INTO product(tenant_id, id, name, image, description, active)
VALUES (1,10,'Camara reflex','https://img.pccomponentes.com/articles/24/247581/a18.jpg', 'Esta cámara réflex digital es perfecta para mejorar tus capacidades fotográficas respecto a un smartphone o una cámara compacta.', true);
INSERT INTO product(tenant_id, id, name, image, description, active)
VALUES (1,11,'iPhone 12','https://www.backmarket.es/cdn-cgi/image/format=auto,quality=75,width=1920/https://d1eh9yux7w8iql.cloudfront.net/product_images/418121_cdf09ace-c61b-4fbc-897d-061870ca64f4.jpg', 'Apple iPhone 12 llega con un diseño cuatro veces más resistente a las caidas, gracias a su vidrio realizado en Ceramic Shield, y con bordes realizados en aluminio aeroespacial.', true);
INSERT INTO product(tenant_id, id, name, image, description, active)
VALUES (1,12,'iPad pro','https://m.media-amazon.com/images/I/81raq99wqRS._AC_SX679_.jpg', 'El M1 convierte al iPad Pro en el dispositivo más rápido de su categoría. Haz con el iPad lo que quieras gracias a la enorme potencia del chip M1 y a sus tecnologías a medida', true);
INSERT INTO product(tenant_id, id, name, image, description, active)
VALUES (1,13,'Chromecast','https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_84955743/fee_786_587_png', 'La tercera generación del reproductor multimedia más básico de Google ya es oficial. El Google Chromecast 3 llega con ligeros cambios en el diseño, algo más de resolución y compatibilidad.', true);
INSERT INTO product(tenant_id, id, name, image, description, active)
VALUES (1,14, 'Lenovo ThinkPad','https://www.lenovo.com/medias/22tpe15e5n2.png?context=bWFzdGVyfHJvb3R8MjgzMTMxfGltYWdlL3BuZ3xoNmEvaGVjLzE0MTExNzEzODg2MjM4LnBuZ3xlM2I4ZGNiODIzODYxOTc2NTU0NzI4NTFiNWRkMGY4OWRhZTBlNjk1ZGMzZDljOGU2OGQwNmM0YzY1YmVmMDdk', 'No busques más si lo que quieres es un portátil cómodo y que rinda: el portátil ThinkPad E15 de 2.ª generación (Intel) es tu mejor opción.', true);
INSERT INTO product(tenant_id, id, name, image, description, active)
VALUES (1,15,'Monitor ultrawide','https://img.pccomponentes.com/articles/38/389224/175-samsung-ls34j550wqrxen-34-led-ultrawide-qhd-freesync.jpg', 'Con su pantalla ultra ancha de 34 "y su resolución WQHD de 21: 9, el LS34J550WQU tiene todo el espacio de trabajo que necesita para realizar múltiples tareas cómodamente en una sola pantalla.', true);

