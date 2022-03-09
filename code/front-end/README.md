# Multitenant web store front end
Front end of a multitenant web store for any kind of product. Using [Angular CLI](https://github.com/angular/angular-cli) version 13.2.3.

## Development server
### Using docker
```console
sudo docker build -f ./docker/DockerfileRun .
```

docker build --build-arg CONFIGURATION=${params.CONFIGURATION} ${containerTagParameter} -f ./docker/${params.DOCKERFILE} .

### Using angular
Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`.
