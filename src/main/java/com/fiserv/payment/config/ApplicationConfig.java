package com.fiserv.payment.config;

import com.fiserv.payment.domain.service.FraudThresholdEvaluator;
import com.fiserv.payment.domain.service.TransactionAuthorizationService;
import com.fiserv.payment.application.fraud.FraudDetectionStrategy;
import com.fiserv.payment.application.fraud.BasicFraudStrategy;
import com.fiserv.payment.application.processor.PaymentProcessor;
import com.fiserv.payment.application.processor.PaymentProcessorFactory;
import com.fiserv.payment.application.processor.AuditedPaymentProcessorDecorator;
import com.fiserv.payment.application.validation.*;
import com.fiserv.payment.domain.ports.TransactionRepository;
import com.fiserv.payment.domain.ports.TransactionEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration for dependency injection
 * Demonstrates:
 * - Factory pattern for creating processors
 * - Decorator pattern for auditing
 * - Chain of Responsibility pattern for validation
 * - Spring bean lifecycle management
 */
@Configuration
public class ApplicationConfig {

    @Value("${fraud.strategy:basic}")
    private String fraudStrategy;

    /**
     * Constructs the validation chain using Chain of Responsibility pattern
     * Each validator decides whether to pass or reject
     */
    @Bean
    public TransactionValidator transactionValidator() {
        CardExpiryValidator cardExpiryValidator = new CardExpiryValidator();
        BinValidator binValidator = new BinValidator();
        CreditLimitValidator creditLimitValidator = new CreditLimitValidator();
        TransactionVelocityValidator velocityValidator = new TransactionVelocityValidator();
        SuspiciousHourValidator suspiciousHourValidator = new SuspiciousHourValidator();

        // Build the chain: expiry -> bin -> limit -> velocity -> hour
        cardExpiryValidator.setNext(binValidator);
        binValidator.setNext(creditLimitValidator);
        creditLimitValidator.setNext(velocityValidator);
        velocityValidator.setNext(suspiciousHourValidator);

        return cardExpiryValidator;
    }

    /**
     * Fraud detection strategy (Strategy Pattern)
     * Can be switched based on configuration or merchant type
     */
    @Bean
    public FraudDetectionStrategy fraudDetectionStrategy() {
        if ("ml".equalsIgnoreCase(fraudStrategy)) {
            return new com.fiserv.payment.application.fraud.MLFraudStrategy();
        }
        return new BasicFraudStrategy();
    }

    /**
     * Payment processor with auditing decorator (Decorator Pattern + Factory Pattern)
     */
    @Bean
    public PaymentProcessor paymentProcessor(PaymentProcessorFactory factory) {
        // Factory can be enhanced to return different processor based on merchant/transaction
        PaymentProcessor processor = factory.getProcessor(
            new com.fiserv.payment.domain.model.Transaction(
                new com.fiserv.payment.domain.model.Card("4111111111111111", "Test", "411111",
                    com.fiserv.payment.domain.model.CardBrand.VISA, "12", "2025"),
                new com.fiserv.payment.domain.model.Merchant("TEST", "Test", "5411", "BR",
                    new java.math.BigDecimal("10000"), com.fiserv.payment.domain.model.RiskLevel.LOW),
                new java.math.BigDecimal("100"), com.fiserv.payment.domain.model.Currency.BRL
            )
        );

        // Decorate with audit logging
        return new AuditedPaymentProcessorDecorator(processor);
    }

    @Bean
    public PaymentProcessorFactory paymentProcessorFactory() {
        return new PaymentProcessorFactory();
    }

    @Bean
    public FraudThresholdEvaluator fraudThresholdEvaluator() {
        return new FraudThresholdEvaluator();
    }

    /**
     * Main authorization service that orchestrates the flow
     */
    @Bean
    public TransactionAuthorizationService transactionAuthorizationService(
            TransactionValidator validator,
            FraudDetectionStrategy fraudStrategy,
            PaymentProcessor processor,
            TransactionRepository repository,
            TransactionEventPublisher eventPublisher,
            FraudThresholdEvaluator fraudThresholdEvaluator) {

        return new TransactionAuthorizationService(
            validator,
            fraudStrategy,
            processor,
            repository,
            eventPublisher,
            fraudThresholdEvaluator
        );
    }
}

