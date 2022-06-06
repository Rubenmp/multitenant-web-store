# Multitenant Web Store back end
Back end services for the project.


## Requirements
- Java 17
- [MySQL](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-20-04)
    Create local databases "mws_db" (main database) and "mws_test_db" (database used for integration tests) with user "mws_dev" and password "password"
    ```roomsql
    sudo mysql
    ```
    ```roomsql
    CREATE USER 'mws_dev' IDENTIFIED BY 'password';
    DROP DATABASE IF EXISTS mws_db;
    CREATE DATABASE mws_db;
    DROP DATABASE IF EXISTS mws_test_db;
    CREATE DATABASE mws_test_db;
    GRANT ALL PRIVILEGES ON mws_db.* TO 'mws_dev';
    GRANT ALL PRIVILEGES ON mws_test_db.* TO 'mws_dev';
    FLUSH PRIVILEGES;
    ```
    then you will be able to connect mysql locally using
    ```console
    mysql -u mws_dev -p mws_db # Enter user password here
    ```


## Run development server
Build the image using docker:
```console
docker build -f ./docker/DockerfileRun .
```
then run it with docker.

Using maven wrapper:
```console
./mvnw spring-boot:run
```

then navigate to `http://localhost:8080/`.
