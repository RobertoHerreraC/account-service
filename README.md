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
* RxJava 3
* Spring Data Reactive MongoDB
* MongoDB
* Spring Cloud Config Client
* WebClient
* OpenAPI Generator
* Lombok
* Logback
* Maven

---

## Puerto

```text
8082
```

---

## Configuración externa

Este microservicio obtiene su configuración desde `config-server`.

### application.yml

```yaml
spring:
  application:
    name: account-service

  config:
    import: optional:configserver:http://localhost:8888
```

### Config Server

Archivo:

```text
account-service.yml
```

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

---

## Dependencias externas

Este microservicio consume:

```text
customer-service
```

Para validar:

```text
• Existencia del cliente
• Tipo de cliente: PERSONAL o BUSINESS
```

---

## Orden de levantamiento

```text
1. MongoDB
2. config-server
3. customer-service
4. account-service
```

---

## Verificar Config Server

```http
GET http://localhost:8888/account-service/default
```

---

## Ejecutar aplicación

```bash
mvn clean spring-boot:run
```

---

## Generación OpenAPI

Contrato:

```text
src/main/resources/openapi/account-api.yml
```

Generar código:

```bash
mvn clean generate-sources
```

---

## Modelo de dominio

### AccountType

```text
SAVINGS
CHECKING
FIXED_TERM
```

---

## Reglas de negocio implementadas

### Cliente PERSONAL

```text
• Puede tener cuenta SAVINGS.
• Puede tener cuenta CHECKING.
• Puede tener cuenta FIXED_TERM.
• Solo puede tener una cuenta SAVINGS activa.
• Solo puede tener una cuenta CHECKING activa.
```

### Cliente BUSINESS

```text
• No puede tener cuenta SAVINGS.
• No puede tener cuenta FIXED_TERM.
• Puede tener múltiples cuentas CHECKING.
• Debe tener uno o más titulares.
• Puede tener cero o más firmantes autorizados.
```

### Operaciones

```text
• El depósito debe ser mayor a cero.
• El retiro debe ser mayor a cero.
• El retiro no puede superar el saldo disponible.
• Se utiliza borrado lógico con active=false.
```

---

## Endpoints

### CRUD

```http
POST   /api/v1/accounts
GET    /api/v1/accounts
GET    /api/v1/accounts/{id}
PUT    /api/v1/accounts/{id}
DELETE /api/v1/accounts/{id}
```

### Operaciones de cuenta

```http
POST   /api/v1/accounts/{id}/deposits
POST   /api/v1/accounts/{id}/withdrawals
GET    /api/v1/accounts/{id}/balance
GET    /api/v1/accounts/customer/{customerId}
```

---

## Logs

Ejemplos:

```text
INFO  Creating account for customerId: ...
INFO  Customer validated successfully
INFO  Depositing money into account with id: ...
INFO  Withdrawing money from account with id: ...
INFO  Account balance found successfully
WARN  Business rule violation
ERROR Unexpected error
```
