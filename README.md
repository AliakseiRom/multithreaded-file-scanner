# Multithreaded file scanner
This project is a REST API for scanning directories
to search for files by mask

## Getting Started

Postman collection is located in the root folder 
**Multithreaded file scanner rest api.postman_collection.json**

### Requirements
To build and run this project, you will need the following:

* **JDK**: Install Java Develompment Kit 24
* **Maven**: Install Apache Maven 4.0.0
* **Postgres**: Create database with your postgres user. Use command: `CREATE DATABASE file_scanner WITH OWNER postgres`
* **application.yml**: Set up your credentials to Postgres DB with your username and password
* **mvn clean install**: Perform mvn clean install
* **Running project**: Run the Project

## API Endpoints

### Base URL
**http://localhost:8080**

### Scan Controller
* GET `/scan`: get files from directories by mask

### File Controller
* DEL `/file`: delete all files from db
