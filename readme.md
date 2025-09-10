## Evalaución
```
 
### Arquitectura de Microservicios Propuesta

![plot](./arquitectura_propuesta.png)

### Collection Postman
```

```
 
### Arquitectura de Microservicios (Objetivo)

![plot](./arquitectura_objetivo.png)

### Collection Postman
```
### Microservicios
```
1.- Customer Microservice
2.- Account Microservice
3.- Movement - Report Microservice
```

### Database
```
1.- Customer Database
2.- Account Database
3.- Movement - Report Database
```

### Core Orchestrator
```
1.- ocore Orchestrator
```

### Shell & Microfronend
```
1.- Shell
2.- Customer MFE
3.- Account MFE
4.- Movement MFE
```

### Build and Run Project
```
Create Network for Microservice, Db, Frontend (Comunication)
docker network create bcpc


Docker Account MicroService

docker build . -t account-image:1.0.0
docker run -d  --name account-msa --network=ncore -p 9095:9095 account-image:1.0.0

Docker Movement MicroService
docker build . -t movement-image:1.0.0
docker run -d --name movement-msa --network=ncore -p 9080:9080 movement-image:1.0.0

Docker Customer MicroService
docker build . -t customer-image:1.0.0
docker run -d --name customer-msa --network=ncore -p 9090:9090 customer-image:1.0.0

Docker Orchestrator Middleware
docker run -d --name orchestrator-core-ms --network=ncore -p 8080:8080 ocore-image:1.0.0

Shell
docker build . -t shell-image:1.0.0
docker run -d  --name shell-core --network=ncore -p 4200:4200 shell-image:1.0.0 

Microfrontend
docker build . -t shell-image:1.0.0
docker run -d  --name account-core --network=ncore -p 4202:4202 shell-image:1.0.0 

Microfrontend
docker build . -t shell-image:1.0.0
docker run -d  --name clients-core --network=ncore -p 4201:4201 shell-image:1.0.0 

Microfrontend
docker build . -t shell-image:1.0.0
docker run -d  --name movements-core --network=ncore -p 4201:4201 shell-image:1.0.0 

1.- BPC.postman_collection.json
```
### Script DB and Docker Compose
```
Go to database
docker-compose up --build -d
```
