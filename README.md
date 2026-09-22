# DRP API - Standard 200 Response + Username/Password Auth

## Response format

All API responses use the same envelope:

```json
{
  "status": 200,
  "message": "...",
  "data": null
}
```

HTTP status is also returned as `200 OK` for application-level success/error results.

## 1. Authentication

### Request

```http
POST /api/v1/auth/token
Content-Type: application/json
```

```json
{
  "username": "fintrex-user",
  "password": "fintrex-password"
}
```

Credentials are configurable using:

```properties
drp.api.username=${DRP_API_USERNAME:fintrex-user}
drp.api.password=${DRP_API_PASSWORD:fintrex-password}
drp.api.scope=${DRP_API_SCOPE:drp-api}
```

### Success

```json
{
  "status": 200,
  "message": "Authentication successful.",
  "data": {
    "access_token": "<jwt>",
    "token_type": "Bearer",
    "expires_in": 3600,
    "scope": "drp-api"
  }
}
```

### Wrong username/password

```json
{
  "status": 200,
  "message": "Invalid username or password.",
  "data": null
}
```

## 2. DRP Search

### Request

```http
POST /api/v1/drp/search
Authorization: Bearer <jwt>
Content-Type: application/json
```

```json
{
  "nic": "901234567V"
}
```

### Success example

```json
{
  "status": 200,
  "message": "DRP customer data retrieved successfully.",
  "data": {
    "drpId": 3,
    "requestId": 9,
    "response": [
      {
        "idCardNumber": "901234567V",
        "frontImage": "<base64>",
        "backImage": "<base64>",
        "photoImage": "<base64>"
      }
    ]
  }
}
```

### Wrong NIC / no customer / no latest request / empty response

```json
{
  "status": 200,
  "message": "No DRP customer found for the supplied NIC.",
  "data": null
}
```

### Missing/invalid JWT

```json
{
  "status": 200,
  "message": "Valid Bearer token is required.",
  "data": null
}
```

## UAT Base64 override

When the `uat` profile is active, existing Base64 values are replaced using:

```text
src/main/resources/uat/nic-front.base64
src/main/resources/uat/nic-back.base64
src/main/resources/uat/photo-image.base64
```

`photo-image.base64` currently contains the same Base64 as NIC FRONT.

Enable UAT:

```text
-Dspring.profiles.active=uat
```

LIVE/default keeps the original DRP response values.

## WAR deployment

This project is configured for external Tomcat:

```xml
<packaging>war</packaging>
```

Deploy the generated WAR as `drp.war` to get context path `/drp`.

Swagger/OpenAPI examples:

```text
http://<server>:<port>/drp/swagger-ui.html
http://<server>:<port>/drp/swagger-default.html
http://<server>:<port>/drp/v3/api-docs
```


---

# JPA Configuration

This version uses Spring Data JPA instead of JdbcTemplate.

Dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

The existing DRP database remains authoritative.

Hibernate schema changes are disabled:

```properties
spring.jpa.hibernate.ddl-auto=none
spring.jpa.generate-ddl=false
spring.jpa.open-in-view=false
```

`ddl-auto=null` is not a valid Hibernate setting. `none` is used to achieve the intended behavior: do not create, validate, update, or drop the existing schema automatically.

The API uses native JPA queries for:

```sql
SELECT id
FROM drp.customer
WHERE nic = ?
ORDER BY id DESC
LIMIT 1;
```

```sql
SELECT id
FROM drp.last_request
WHERE customer = ?
LIMIT 1;
```

```sql
SELECT response
FROM drp.request
WHERE id = ?
LIMIT 1;
```

The `last_request` object remains an existing database view. JPA does not create or modify it.

## Important

JPA is used only for data access.

No automatic DDL update is enabled for UAT or LIVE.
