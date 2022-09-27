# Todolist

## Env preparation

* MongoDB:  
You can run following command in docker.  
`docker run --name db -d -p 27017:27017 mongo`  
more details via [Mongo - Official Image](https://hub.docker.com/_/mongo)

## Install dependencis

Run `cd frontend` & `npm install`  
Run `cd backend` & `mvn install`  

## Running unit tests

Run `cd backend` & `mvn test`

## Start Server

Run `cd frontend` & `npm start`  
Run `cd backend` & `java -jar target/todolist-0.0.1-SNAPSHOT.jar`  
Navigate to `http://localhost:8081/`
 
