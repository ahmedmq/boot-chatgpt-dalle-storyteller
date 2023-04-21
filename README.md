# AI Story Teller using Spring Boot, ChatGPT and Dall-E 2 

### Project Structure 

The codebase loosely follows the [application continuum](https://www.appcontinuum.io/) programming style. It consists of 3 top level directories - applications, components and databases


```
|-- applications
|   |-- images-app
|   |-- stories-app
|   |-- pom.xml
|-- components
|   |-- images
|   |-- stories
|   |-- pom.xml
|-- databases
|   |-- stories-database
```

The [applications](./applications) module contains deployable artefacts and their configuration. Applications are designed and modelled around a single [bounded context](https://martinfowler.com/bliki/BoundedContext.html).  
Each application contains little more than 
- A Spring Boot application class
- Spring REST controllers
- Application specific configuration

Each application depends on one more [components](./components) which are loosely coupled and highly cohesive.
A typical components consists of

- Services
- Business Logic
- JPA Repositories
- Support Libraries

Support libraries, suffixed with _*-support_, contain code related to a specific function and avoid any business logic.

The [databases](./databases) directory consists of the data store implementation per microservice. Each data store implementation may consists of the database SQL schema depending on the choice of database technology used.


### Adding connector configuration

From the project root folder

- Run the following `curl` commands to create `Debezium` connectors in `kafka-connect`

```bash
  curl -i -X POST localhost:8083/connectors -H 'Content-Type: application/json' -d @connectors/stories-connector.json
```

- Check the status of the connector by using the [Kafka-UI](http://localhost:8087) or calling `kafka-connect` endpoint

```bash
curl localhost:8083/connectors/stories-connector/status
```

- The state of the connectors and their tasks must be RUNNING. If there is any problem, you can check kafka-connect container logs.

```bash
docker logs kafka-connect
```

- To delete the connector

```bash
curl -X DELETE http://localhost:8083/connectors/stories-connector 
```