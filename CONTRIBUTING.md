# Contributing to Payment Authorization Service

Obrigado por contribuir! Este documento descreve como fazer isso.

## 🎯 Filosophia do Projeto

- **Clean Code**: Código legível vale mais que código esperto
- **Domain-Driven**: Lógica de negócio importante fica no domain
- **Test-Driven**: Testes guiam o design (TDD quando possível)
- **SOLID**: Princípios sólidos de design orientado a objetos
- **Documentation**: Código bem documentado é código bem pensado

## 🔧 Setup Desenvolvimento

```bash
# Clone
git clone <repo>
cd payment-authorization

# Build
mvn clean install

# Testes
mvn test

# Run local
./start.sh  # escolha opção 1
```

## 📝 Antes de Enviar PR

### 1. Código

```bash
# Testes devem passar
mvn test

# Teste de integração (requirements: Docker + Kafka)
mvn test -Dgroups=integration

# Sem warnings de código
mvn checkstyle:check pmd:check spotbugs:check
```

### 2. Commit

```bash
# Mensagens claras
git commit -m "feat(validation): add email validator to chain

- Validates email format before updating user profile
- Implements TransactionValidator interface
- Adds unit tests"

# ou (para bug fixes)
git commit -m "fix(fraud): prevent null pointer in scoring"
```

### 3. Pull Request

- Título claro: `feat:`, `fix:`, `docs:`, `test:`, `refactor:`, `perf:`
- Descrição do que foi mudado e por quê
- Linkar issues se relacionado
- Screenshots se for UI
- Adicionar reviewer

## 🏗️ Estrutura para Contribuições

### Novo Validator

```java
// application/validation/MyValidator.java
public class MyValidator extends TransactionValidator {
    
    @Override
    protected String performValidation(Transaction transaction) {
        // implementação
        if (invalid) {
            return "[MyValidator] Reason for decline";
        }
        return null;  // passa
    }
}

// config/ApplicationConfig.java - adicionar à chain
validationChain.setNext(new MyValidator());

// test/TransactionValidatorChainTest.java
@Test
public void testMyValidatorRejects() {
    // teste
}
```

### Nova Estratégia de Fraude

```java
// application/fraud/MyFraudStrategy.java
public class MyFraudStrategy implements FraudDetectionStrategy {
    
    @Override
    public int calculateFraudScore(Transaction transaction) {
        // return 0-100
    }
    
    @Override
    public String getStrategyName() {
        return "MyFraudStrategy";
    }
}
```

### Novo Processador de Pagamento

```java
// application/processor/MyPaymentProcessor.java
public class MyPaymentProcessor implements PaymentProcessor {
    
    @Override
    public String process(Transaction transaction) {
        // return authCode ou throw exception
    }
    
    @Override
    public boolean canHandle(Transaction transaction) {
        // return true if can handle
    }
    
    @Override
    public String getProcessorName() {
        return "MyPaymentProcessor";
    }
}

// application/processor/PaymentProcessorFactory.java
// adicionar à lista de processadores
```

## 🧪 Testes

### Naming Convention

```java
// Controllers
public class TransactionAuthorizationControllerTest

// Services
public class TransactionAuthorizationServiceTest

// Validators
public class TransactionValidatorChainTest

// Strategies
public class FraudDetectionStrategyTest
```

### Padrão @Test

```java
@Test
public void testShouldDescribeWhatHappens() {
    // Arrange - Setup
    Transaction transaction = createTestTransaction();
    
    // Act - Execute
    String result = validator.validate(transaction);
    
    // Assert - Verify
    assertNotNull(result);
    assertTrue(result.contains("expected"));
}
```

### Mocks

```java
// Use Mockito para dependências externas
@Mock
private TransactionRepository repository;

@Test
public void testShouldSaveTransaction() {
    when(repository.save(any(Transaction.class)))
        .thenReturn(transaction);
    
    // test code
}
```

## 📊 Padrões Aceitáveis

### ✅ Bom

```java
// Chain of Responsibility
validator1.setNext(validator2);
String error = validator1.validate(tx);

// Strategy
FraudDetectionStrategy strategy = getStrategyByType(merchant);
int score = strategy.calculateFraudScore(tx);

// Builder
Transaction tx = Transaction.builder()
    .card(card)
    .amount(amount)
    .build();

// Adapter
Transaction domain = adapter.fromEntity(jpaEntity);

// Decorator
PaymentProcessor decorated = new AuditedPaymentProcessorDecorator(original);
```

### ❌ Evitar

```java
// God Classes
public class TransactionService {
    // 500+ linhas fazendo tudo
}

// Anemic Models
public class Transaction {
    public String transactionId;
    public BigDecimal amount;
    // getters/setters apenas
}

// Mixing concerns
public class TransactionService {
    void process() {
        // validação
        // fraude
        // processamento  ← 3 coisas em uma função
        // persistência
        // kafka
    }
}

// Direct instantiation
new VisaPaymentProcessor()  // use factory

// Hardcoded values
if (fraudScore >= 70) {...}  // use constant
```

## 📚 Documentação

### Code

```java
/**
 * Validates if transaction amount exceeds daily limit
 * Part of Chain of Responsibility validation pipeline
 * 
 * @see TransactionValidator
 */
public class CreditLimitValidator extends TransactionValidator {
    
    /**
     * Performs the validation
     * @param transaction the transaction to validate
     * @return null if valid, decline reason if invalid
     */
    @Override
    protected String performValidation(Transaction transaction) {
        // implementation
    }
}
```

### README

- Adicionar caso de uso se for feature nova
- Atualizar arquivos ADR se for mudança arquitetzral grande
- Adicionar diagrama se for complexo

## 🚀 Release Process

```bash
# 1. Update version in pom.xml
mvn versions:set -DnewVersion=1.0.1

# 2. Commit
git commit -m "chore: bump version to 1.0.1"

# 3. Tag
git tag -a v1.0.1 -m "Release 1.0.1: descrição"

# 4. Push
git push origin main --tags

# 5. Build e publish
mvn clean deploy
```

## 🔴 CI/CD Checklist

Antes de dizer que está pronto, certifique-se:

- [ ] `mvn test` passa em 100%
- [ ] Sem warnings do Maven
- [ ] SonarQube score ok
- [ ] Dependências sem CVE
- [ ] Docker builds sem erro
- [ ] Documentação atualizada
- [ ] ADR updated if needed
- [ ] Code review de 1+ person

## 📞 Help & Questions

- [GitHub Issues](../issues/) → bugs e features
- [Discussions](../discussions/) → dúvidas
- Email: team@fiserv.com

## 📜 License

MIT - veja LICENSE.md

---

Obrigado por contribuir! 🙏

