# Multitenant web store front end
Front end of a multitenant web store for any kind of product.


## Run development server
Using docker
```console
docker build -f ./docker/DockerfileRun .
```
then run the docker image.

Using npm:
```console
npm i
ng serve
```

then navigate to `http://localhost:4200/`.


## Run tests
```console
docker build -f ./docker/DockerfileTest .
```