# Microsserviço de pedidos

Administração de pedidos e clientes.

Consulte a documentação completa no [repositório principal do grupo](https://github.com/FIAP-3SOAT-G15/tech-challenge).

### Análise estática

Projeto no SonarCloud: [https://sonarcloud.io/project/overview?id=FIAP-3SOAT-G15_orders-api](https://sonarcloud.io/project/overview?id=FIAP-3SOAT-G15_orders-api)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=FIAP-3SOAT-G15_orders-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=FIAP-3SOAT-G15_orders-api)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=FIAP-3SOAT-G15_orders-api&metric=coverage)](https://sonarcloud.io/summary/new_code?id=FIAP-3SOAT-G15_orders-api)

### Executar localmente

```bash
docker compose up
```

### Mappers

```
mvn clean compile
```

### Testes

```
mvn clean verify
```

Testes de integração:

```
mvn clean verify -DskipITs=false
```

### ktlint

```
mvn antrun:run@ktlint-format
```
