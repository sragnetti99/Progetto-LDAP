# Spring-boot-rest-api

Servizio avanzato per l'esposizione di CRUD rest documentate con Swagger.

Tra gli aspetti indirizzati:
 - 01
   - Servizio rest Base
 - 02
   - Utilizzo di Lombok
   - Gestione errori (Error Handler)
   - Logging
   - Unit Test
 - 03  
   - Correlation ID
   - Access Log
 - **04**
   - **Swagger**
 - 05
   - Gestione Sicurezza tramite JWT


## Link principali
- **Swagger UI** - http://localhost:8082/swagger-ui/index.html#/
- **Api doc** - http://localhost:8082/v2/api-docs


## Curl per provare il servizio (su windows usare gitbash)

```shell
# getDocuments 
curl "http://localhost:8082/api/v1/document" -s

# getDocuments 
curl "http://localhost:8082/api/v1/document/doc-1" -s

# delete 
curl -X DELETE "http://localhost:8082/api/v1/document/doc-1" -s

# post
curl -X POST "http://localhost:8082/api/v1/document" -d "{\"id\":\"doc-4\",\"name\":\"Appendice\",\"description\":\"Appendice 2\"}" -H "accept: application/json" -H "Content-Type: application/json" -s 

# put
curl -X PUT "http://localhost:8082/api/v1/document" -d "{\"id\":\"doc-4\",\"name\":\"Appendice\",\"description\":\"Appendice 2 - correzione\"}" -H "accept: application/json" -H "Content-Type: application/json" -s
```

## Code quality

```shell
# Report Jacoco + SonarQube
mvn org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report
mvn sonar:sonar "-Dsonar.projectKey=allitude-spring-boot-restapi" "-Dsonar.host.url=http://localhost:9000" "-Dsonar.login=67c3ece48b0c72e568899e4cb4fd5be3d61416da"
```

## Note operative

### Swagger

- Aggiornare il pom:

  ```xml
          <!-- swagger -->
          <dependency>
              <groupId>io.springfox</groupId>
              <artifactId>springfox-boot-starter</artifactId>
              <version>3.0.0</version>
          </dependency>
  ```

- Aggiornare il **model**
  - `Document`
  - `ResponseMessage`

- Aggiungere l'interfaccia per le Api `DocumentApi`

