[![Build Status](https://travis-ci.org/sambamitra/api-gateway.svg?branch=master)](https://travis-ci.org/sambamitra/api-gateway)
# API Gateway
This is an Api Gateway project used for orchestration between microservices.

## Prerequisites
- JDK 1.8 or later
- Maven 3.x
- Docker - Download and install : <https://www.docker.com/products/overview>

## How to run
* Clone the project
* Build the project using : __mvn clean install -DskipDockerBuild__
* Run the component using : __java -jar api-gateway-0.1.0-SNAPSHOT.jar__
* Go to <https://localhost:8080/swagger-ui.html>. This should show the documentation of all the endpoints exposed.

## Microservice architecture model
This project is part of an example microservice architecture model. For more details, go to [Microservice Example](https://github.com/sambamitra/microservice-example)
