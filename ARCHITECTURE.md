# Architecture Overview

Este documento descreve a arquitetura completa do Payment Authorization Microservice.

## 📐 Princípios Fundamentais

### 1. Hexagonal Architecture (Ports & Adapters)

A aplicação é organizada em três camadas concêntricas:

```
┌────────────────────────��────────────────────────┐
│          REST API (HTTP Controllers)             │
│                  - DTO Mapping                   │
│                  - Request Validation            │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│        Application Layer (Orquestração)          │
│  - Validation Chain (Chain of Responsibility)   │
│  - Fraud Detection (Strategy)                   │
│  - Payment Processing (Factory + Decorator)     │
└───────────────────┬─────────────────────────────┘
                    │
┌────���──────────────▼─────────────────────────────┐
│      Domain Layer (Business Logic - Puro)       │
│  - Transaction, Card, Merchant (Models)         │
│  - TransactionAuthorizationService              │
│  - Ports (Interfaces - Domain depende delas)   │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│     Infrastructure Layer (Implementações)       │
│  - TransactionRepositoryAdapter (JPA)           │
│  - KafkaTransactionEventPublisher               │
│  - Kafka Consumers                              │
│  - Spring Security, JWT, etc                    │
└─────────────────────────────────────────────────┘
```

### 2. Inversão de Dependência

```
Domain NUNCA depende de:
  ❌ Spring Framework
  ❌ JPA/Hibernate
  ❌ Kafka
  ❌ HTTP clients

Domain DEPENDE DE:
  ✅ Interfaces (Ports)
  ✅ Value Objects (Transaction, Card, Merchant)
  ✅ Exceptions do domínio

Infrastructure IMPLEMENTA:
  ✅ Ports (Adapter Pattern)
  ✅ DTOs para serialização
  ✅ Spring Beans
```

---

## 📦 Estrutura de Diretórios Detalhada

### `src/main/java/com/fiserv/payment/`

```
payment-authorization/
│
├── api/                                    # REST API Layer
│   ├── controller/
│   │   └── TransactionAuthorizationController.java
│   └── dto/
│       ├── AuthorizeTransactionRequest.java
│       └── AuthorizeTransactionResponse.java
│
├── domain/                                 # Business Logic (Core)
│   ├── model/                              # Value Objects
│   │   ├── Transaction.java                # Main aggregate root
│   │   ├── Card.java                       # Card data
│   │   ├── Merchant.java                   # Merchant data
│   │   ├── CardBrand.java                  # Enum VISA, MC, PIX, etc
│   │   ├── Currency.java                   # Enum BRL, USD, EUR
│   │   ├── TransactionStatus.java          # Enum: PENDING, APPROVED, etc
│   │   └── FraudScore.java                 # Enum: LOW, MEDIUM, HIGH
│   │
│   ├── ports/                              # Interfaces (Boundary)
│   │   ├── TransactionRepository.java      # Port: Persistence
│   │   ├── TransactionEventPublisher.java  # Port: Events
│   │   ├── MerchantService.java            # Port: External service
│   │   └── WebhookNotificationPort.java    # Port: Notifications
│   │
│   ├── service/                            # Use Cases (Orquestração pura)
│   │   ├── TransactionAuthorizationService.java  # Main service
│   │   └── FraudThresholdEvaluator.java    # Helper service
│   │
│   └── exception/
│       ├── DomainException.java
│       └── TransactionAuthorizationException.java
│
├── application/                            # Application Layer (Padrões)
│   ├── validation/                         # Chain of Responsibility
│   │   ├── TransactionValidator.java       # Abstract base
│   │   ├── CardExpiryValidator.java        # ✓ Cartão expirado?
│   │   ├── BinValidator.java               # ✓ BIN válido?
│   │   ├── CreditLimitValidator.java       # ✓ Limite de crédito?
│   │   ├── TransactionVelocityValidator.java # ✓ Muitas transações?
│   │   └── SuspiciousHourValidator.java    # ✓ Horário suspeito?
│   │
│   ├── fraud/                              # Strategy Pattern
│   │   ├── FraudDetectionStrategy.java     # Interface
│   │   ├── BasicFraudStrategy.java         # Implementação simples
│   │   └── MLFraudStrategy.java            # Simulação ML
│   │
│   └── processor/                          # Factory + Decorator
│       ├── PaymentProcessor.java           # Interface
│       ├── PaymentProcessorFactory.java    # Factory (cria correto)
│       ├── VisaPaymentProcessor.java       # Implementação Visa
│       ├── MastercardPaymentProcessor.java # Implementação MC
│       └── AuditedPaymentProcessorDecorator.java # Decorator (audit)
│
├── infrastructure/                         # Technical Implementations
│   ├── persistence/
│   │   ├── TransactionEntity.java          # JPA Entity
│   │   ├─�� JpaTransactionRepository.java   # Spring Data interface
│   │   └── TransactionRepositoryAdapter.java # Adapter (impl port)
│   │
│   ├── kafka/
│   │   ├── KafkaTransactionEventPublisher.java # Producer (impl port)
│   │   ├── TransactionEvent.java           # Event DTO
│   │   └── TransactionEventConsumer.java   # Consumer
│   │
│   └── security/                           # (Não implementado neste MVP)
│       └── JwtTokenProvider.java           # JWT helper
│
├── config/                                 # Spring Configuration
│   ├── ApplicationConfig.java              # Bean factory + dependency injection
│   └── OpenApiConfig.java                  # Swagger/OpenAPI
│
└── PaymentAuthorizationApplication.java    # Spring Boot entry point

src/test/java/com/fiserv/payment/          # Tests
├── application/validation/
│   └── TransactionValidatorChainTest.java  # Chain of Responsibility tests
├── application/fraud/
│   └── FraudDetectionStrategyTest.java     # Strategy pattern tests
└── domain/service/
    └── TransactionAuthorizationServiceTest.java # Integration tests
```

---

## 🔄 Fluxo de Dados

### Request Path (Request → Response Síncrono)

```
HTTP Request
    ↓
TransactionAuthorizationController
    ├─ Map DTO → Domain Models (Card, Merchant, Transaction)
    └─ Call TransactionAuthorizationService
        ↓
    TransactionAuthorizationService.authorizeTransaction()
        ├─ Step 1: validationChain.validate()
        │   └─ CardExpiryValidator → BinValidator → CreditLimitValidator → ...
        │
        ├─ Step 2: fraudStrategy.calculateFraudScore()
        │   └─ BasicFraudStrategy | MLFraudStrategy
        │
        ├─ Step 3: fraudThresholdEvaluator.shouldDeclineBasedOnFraud()
        │
        ├─ Step 4: paymentProcessor.process()
        │   └─ VisaPaymentProcessor | MastercardPaymentProcessor (wrapped with Decorator)
        │
        ├─ Step 5: transactionRepository.save()
        │   └─ TransactionRepositoryAdapter
        │       └─ Convert Domain → JPA Entity
        │           └─ JpaTransactionRepository.save()
        │
        ├─ Step 6: eventPublisher.publishTransactionAuthorized()
        │   └─ KafkaTransactionEventPublisher
        │       └─ Kafka Topic: transactions.authorized
        │
        └─ Return Transaction (updated with status, auth code, fraud score)

    ↓
TransactionAuthorizationController
    ├─ Map Domain Model → Response DTO
    └─ HTTP Response (200 OK)
```

### Async Path (Events → Kafka → Consumer → Webhook)

```
Transaction Authorized Event Published
    ↓
Kafka Topic: transactions.authorized
    ↓
TransactionEventConsumer
    ├─ @KafkaListener on topic
    ├─ Parse TransactionEvent
    ├─ Call WebhookNotificationPort.notifyTransactionAuthorized()
    └─ Log WEBHOOK_CALL

[Consumer failure → Event retried]
```

---

## 🎯 Design Patterns Aplicados

### 1. Chain of Responsibility

**Localização:** `application/validation/`

**Diagrama:**
```
Request
   ↓
CardExpiryValidator
   ↓ (passes)
BinValidator
   ↓ (passes)
CreditLimitValidator
   ↓ (passes)
TransactionVelocityValidator
   ↓ (passes)
SuspiciousHourValidator
   ↓ (passes)
✅ All validation passed
```

**Código:**
```java
// Setup chain
validator1.setNext(validator2);
validator2.setNext(validator3);

// Validate
String error = validator1.validate(transaction);
if (error != null) {
    return DECLINE;
}
```

**Benefício:** Adicionar novo validador sem tocar código existente.

---

### 2. Strategy Pattern

**Localização:** `application/fraud/`

**Diagrama:**
```
FraudDetectionStrategy (interface)
    │
    ├─ BasicFraudStrategy (simples regras)
    └─ MLFraudStrategy (integração ML futura)

// Runtime selection via config
Strategy strategy = (config.fraudStrategy == "ml") 
    ? new MLFraudStrategy()
    : new BasicFraudStrategy();
```

**Benefício:** Trocar estratégia sem alterar código que a usa.

---

### 3. Builder Pattern

**Localização:** `domain/model/Transaction.java`

```java
Transaction tx = Transaction.builder()
    .card(card)
    .merchant(merchant)
    .amount(new BigDecimal("150.50"))
    .currency(Currency.BRL)
    .transactionId("TXN_001")
    .build();
```

**Benefício:** Construção legível de objetos complexos com campos opcionais.

---

### 4. Factory Pattern

**Localização:** `application/processor/PaymentProcessorFactory.java`

```java
PaymentProcessor processor = factory.getProcessor(transaction);
// Retorna VisaPaymentProcessor ou MastercardPaymentProcessor
// baseado no tipo de cartão
```

**Benefício:** Criação centralizada de objetos. Adicionar novo processador é trivial.

---

### 5. Decorator Pattern

**Localização:** `application/processor/AuditedPaymentProcessorDecorator.java`

```java
PaymentProcessor originalProcessor = new VisaPaymentProcessor();
PaymentProcessor auditedProcessor = new AuditedPaymentProcessorDecorator(originalProcessor);

// Using decorated processor
auditedProcessor.process(transaction);  // Logs, audits, measures performance
```

**Benefício:** Adicionar comportamento (logging, auditoria) sem modificar classe original.

---

### 6. Adapter Pattern

**Localização:** `infrastructure/persistence/TransactionRepositoryAdapter.java`

```java
// Adapter translates domain model ↔ JPA entity
class TransactionRepositoryAdapter implements TransactionRepository {
    
    Transaction save(Transaction transaction) {
        TransactionEntity entity = toDomainEntity(transaction);
        saved = jpaRepository.save(entity);
        return fromEntity(saved);
    }
}
```

**Benefício:** Domain layer completamente independent de JPA.

---

### 7. Hexagonal Architecture

**O padrão que une tudo:**

```
Ports (Interfaces):
├─ Input Ports
│  └─ TransactionAuthorizationService (use case entry point)
│
└�� Output Ports
   ├─ TransactionRepository (persistence)
   ├─ TransactionEventPublisher (events)
   ├─ MerchantService (queries)
   └─ WebhookNotificationPort (notifications)

Adapters (Implementations):
├─ API Adapter (HTTP → Domain)
├─ JPA Adapter (Domain → Database)
├─ Kafka Adapter (Domain → Message Queue)
└─ HTTP Adapter (Domain ��� External APIs)
```

---

## 🔌 Ports & Adapters Mapping

### Input Ports (Como o domain é invocado)

| Port | Adapter | Purpose |
|------|---------|---------|
| N/A | `TransactionAuthorizationController` | REST API entry point |

### Output Ports (Como o domain chama o mundo externo)

| Port | Adapter | Purpose |
|------|---------|---------|
| `TransactionRepository` | `TransactionRepositoryAdapter` | Persistência em PostgreSQL via JPA |
| `TransactionEventPublisher` | `KafkaTransactionEventPublisher` | Publica eventos para Kafka |
| `MerchantService` | (Mock) | Consulta dados de merchant |
| `WebhookNotificationPort` | (Mock) | Notifica webhook do merchant |

---

## 📊 Database Schema

```sql
CREATE TABLE transactions (
    transaction_id VARCHAR(100) PRIMARY KEY,
    merchant_id VARCHAR(50) NOT NULL,
    card_number VARCHAR(20) NOT NULL,
    card_holder VARCHAR(100) NOT NULL,
    bin VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,           -- PENDING, APPROVED, DECLINED, FAILED
    authorization_code VARCHAR(50),
    decline_reason VARCHAR(500),
    fraud_score VARCHAR(20),               -- LOW, MEDIUM, HIGH
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    INDEX idx_merchant_id,
    INDEX idx_status,
    INDEX idx_created_at
);

CREATE TABLE transaction_audit (
    audit_id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(100),
    event_type VARCHAR(50),
    old_status VARCHAR(20),
    new_status VARCHAR(20),
    reason TEXT,
    created_at TIMESTAMP
);
```

---

## 📡 Kafka Topics

### Topics Definidos

| Topic | Purpose | Partition | Replication |
|-------|---------|-----------|-------------|
| `transactions.authorized` | Notifică aprovação | 3 | 2 |
| `transactions.declined` | Notifica recusa | 3 | 2 |

### Message Format

```json
{
  "transaction_id": "TXN_...",
  "merchant_id": "MERCHANT123",
  "amount": "150.50",
  "currency": "BRL",
  "status": "APPROVED",
  "authorization_code": "VISA_abc123",
  "fraud_score": "LOW",
  "timestamp": "2026-05-09T10:30:00"
}
```

---

## 🔒 Security Layers

### 1. API Layer
```
Request → Spring Security Filter Chain
    ├─ Authentication (JWT, KeyCloak, etc)
    ├─ Authorization (roles, permissions)
    └─ Rate Limiting
```

### 2. Domain Layer
```
Transaction → Validation Chain
    ├─ Business rules validation
    ├─ Fraud detection
    └─ Limit checks
```

### 3. Data Layer
```
Database → Row-level security
    ├─ Merchant can only see own transactions
    └─ Support team has read-only access
```

---

## 🚀 Performance Optimizations

### Database
- Índices em `merchant_id`, `status`, `created_at`
- Connection pooling (HikariCP): max 20 connections
- Query caching para merchants (vide `MerchantService`)

### Kafka
- Compression: **snappy**
- Batch size: **20 messages**
- Linger time: **10ms**

### Application
- G1 Garbage Collector (Java 17)
- Metrics collection (Micrometer + Prometheus)

---

## 📈 Scalability Considerations

### Horizontal Scaling
```
Load Balancer
    ├─ Payment Service Instance 1
    ├─ Payment Service Instance 2
    └─ Payment Service Instance N

Shared:
├─ PostgreSQL (managed database)
├─ Kafka Cluster
└─ Redis (cache, rate limit)
```

### Database Scaling
- Read replicas para queries de autorização
- Sharding por merchant ID se necessário

### Message Queue
- Múltiplos partitions em Kafka
- Consumer groups para paralelismo

---

## 🧪 Test Strategy

### Unit Tests
- Domain models e services
- Validators e fraud strategies
- Adapters com mocks

### Integration Tests
- Full request → response cycle
- Database operations
- Kafka publishing

### End-to-End Tests
- API via HTTP
- Database persistence verification
- Kafka event propagation

---

## 📊 Observability

### Metrics (Prometheus)
```
payment_authorization_total{status="APPROVED"|"DECLINED"}
payment_authorization_duration_seconds{quantile="0.95"|"0.99"}
payment_fraud_score_distribution{score_range="LOW"|"MEDIUM"|"HIGH"}
```

### Logs
- **Application logs**: Fluxo de negócio
- **Audit logs**: Transações e mudanças de status
- **Webhook logs**: Tentativas de notificação

### Traces
- OpenTelemetry (not in this MVP)
- Would trace: validation chain → fraud calc → processing → db → kafka

---

## 🎓 Decisões Arquiteturais

Veja `ARCHITECTURE_DECISIONS.md` para:
- ADR-001: Hexagonal Architecture
- ADR-002: Chain of Responsibility para Validação
- ADR-003: Strategy Pattern para Fraude
- ADR-004: Kafka para Async Processing
- ADR-005: Builder Pattern para Transaction
- ADR-006: Decorator Pattern para Auditoria
- ADR-007: Adapter Pattern para Persistência
- ADR-008: OpenAPI para Documentação

---

## 📚 Referências

- [Hexagonal Architecture - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design - Eric Evans](https://www.domainlanguage.com/ddd/)
- [Design Patterns - Gang of Four](https://en.wikipedia.org/wiki/Design_Patterns)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kafka Best Practices](https://kafka.apache.org/documentation.html)

---

**Última atualização:** Maio 9, 2026

