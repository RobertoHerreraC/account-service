# account-service

Microservicio responsable de la gestión de cuentas bancarias del sistema.

Tipos de cuenta soportados:

* `SAVINGS`
* `CHECKING`
* `FIXED_TERM`

## Tecnologías

* Java 17
* Spring Boot
* Spring WebFlux
* RxJava
* Spring Data Reactive MongoDB
* MongoDB
* Spring Cloud Config Client
* WebClient
* Maven
* Lombok
* OpenAPI
* Logback

## Puerto

```text
8082
```

## Configuración externa

Este microservicio obtiene su configuración desde `config-server`.

Archivo local mínimo:

```yaml
spring:
  application:
    name: account-service

  config:
    import: optional:configserver:http://localhost:8888
```

Configuración en Config Server:

```yaml
server:
  port: 8082

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/account_db

customer-service:
  base-url: http://localhost:8081

logging:
  level:
    root: INFO
    com.bank.account: DEBUG
```

## Levantar servicios requeridos

Desde el proyecto donde está el `docker-compose.yml`:

```bash
docker compose up -d
```

Orden recomendado:

```text
1. MongoDB
2. config-server
3. customer-service
4. account-service
```

## Ejecutar account-service

```bash
mvn clean spring-boot:run
```

## Verificar Config Server

```http
GET http://localhost:8888/account-service/default
```

## Dependencia con customer-service

`account-service` consume `customer-service` mediante `WebClient` para validar:
 -d

URL configurada:

```yaml
customer-service:
  base-url: http://localhost:8081
```

## Endpoints principales

```http
POST   http://localhost:8082/api/v1/accounts
GET    http://localhost:8082/api/v1/accounts
GET    http://localhost:8082/api/v1/accounts/{id}
PUT    http://localhost:8082/api/v1/accounts/{id}
DELETE http://localhost:8082/api/v1/accounts/{id}
```

## Ejemplo POST

```json
{
  "customerId": "665f1a2b9c1d4e001234abcd",
  "accountType": "SAVINGS",
  "maintenanceFee": 0.00,
  "monthlyMovementLimit": 10,
  "allowedMovementDay": null,
  "holders": [],
  "authorizedSigners": []
}
```

## OpenAPI

Contrato ubicado en:

```text
src/main/resources/openapi/account-api.yml
```

Generar código:

```bash
mvn clean generate-sources
```

## Reglas de negocio iniciales

```text
PERSONAL:
- Puede tener una cuenta SAVINGS.
- Puede tener una cuenta CHECKING.
- Puede tener cuentas FIXED_TERM.

BUSINESS:
- No puede tener SAVINGS.
- No puede tener FIXED_TERM.
- Puede tener múltiples CHECKING.
- Las cuentas empresariales deben tener uno o más titulares.
```

## Logs

Los logs se visualizan en consola al ejecutar el servicio.

Ejemplo:

```text
INFO  Creating account for customer id: ...
INFO  Customer validated successfully
WARN  Business rule violated: ...
ERROR Error creating account: ...
```

