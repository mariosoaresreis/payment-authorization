package com.fiserv.payment.domain.service;

import com.fiserv.payment.domain.model.FraudScore;
import com.fiserv.payment.domain.model.Transaction;
import com.fiserv.payment.domain.model.TransactionStatus;
import com.fiserv.payment.domain.exception.TransactionAuthorizationException;
import com.fiserv.payment.domain.ports.TransactionRepository;
import com.fiserv.payment.domain.ports.TransactionEventPublisher;
import com.fiserv.payment.application.validation.TransactionValidator;
import com.fiserv.payment.application.fraud.FraudDetectionStrategy;
import com.fiserv.payment.application.processor.PaymentProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transaction Authorization Service
 *
 * This is a domain service that orchestrates the transaction authorization flow:
 * 1. Validates transaction using Chain of Responsibility
 * 2. Calculates fraud score using Strategy pattern
 * 3. Processes payment using appropriate processor
 * 4. Publishes events
 * 5. Persists transaction
 *
 * This service contains pure business logic and is framework-agnostic.
 */
public class TransactionAuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionAuthorizationService.class);

    private final TransactionValidator validationChain;
    private final FraudDetectionStrategy fraudStrategy;
    private final PaymentProcessor paymentProcessor;
    private final TransactionRepository transactionRepository;
    private final TransactionEventPublisher eventPublisher;
    private final FraudThresholdEvaluator fraudThresholdEvaluator;

    public TransactionAuthorizationService(
            TransactionValidator validationChain,
            FraudDetectionStrategy fraudStrategy,
            PaymentProcessor paymentProcessor,
            TransactionRepository transactionRepository,
            TransactionEventPublisher eventPublisher,
            FraudThresholdEvaluator fraudThresholdEvaluator) {

        this.validationChain = validationChain;
        this.fraudStrategy = fraudStrategy;
        this.paymentProcessor = paymentProcessor;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
        this.fraudThresholdEvaluator = fraudThresholdEvaluator;
    }

    /**
     * Main service method to authorize a transaction
     */
    public Transaction authorizeTransaction(Transaction transaction) {
        logger.info("Starting authorization for transaction {}", transaction.getTransactionId());

        // Step 1: Validate using Chain of Responsibility
        logger.debug("Step 1: Validating transaction using validation chain");
        String validationError = validationChain.validate(transaction);

        if (validationError != null) {
            logger.warn("Transaction validation failed: {}", validationError);
            transaction.decline(validationError);
            transactionRepository.save(transaction);
            eventPublisher.publishTransactionDeclined(transaction);
            return transaction;
        }

        logger.debug("Transaction validation passed");

        // Step 2: Calculate fraud score using Strategy pattern
        logger.debug("Step 2: Calculating fraud score using {} strategy", fraudStrategy.getClass().getSimpleName());
        int fraudScore = fraudStrategy.calculateFraudScore(transaction);
        FraudScore fraudScoreLevel = FraudScore.fromScore(fraudScore);
        transaction.setFraudScore(fraudScoreLevel);

        logger.info("Fraud score calculated: {} (level: {})", fraudScore, fraudScoreLevel);

        // Step 3: Evaluate fraud threshold
        if (fraudThresholdEvaluator.shouldDeclineBasedOnFraud(fraudScore, transaction)) {
            logger.warn("Transaction declined due to high fraud score: {}", fraudScore);
            transaction.decline("High fraud score: " + fraudScore);
            transactionRepository.save(transaction);
            eventPublisher.publishTransactionDeclined(transaction);
            return transaction;
        }

        // Step 4: Process payment
        logger.debug("Step 4: Processing payment with {}", paymentProcessor.getProcessorName());
        try {
            String authorizationCode = paymentProcessor.process(transaction);
            transaction.authorize(authorizationCode);
            logger.info("Transaction authorized with code: {}", authorizationCode);
        } catch (Exception e) {
            logger.error("Payment processing failed", e);
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new TransactionAuthorizationException("Payment processing failed: " + e.getMessage(), e);
        }

        // Step 5: Persist transaction
        logger.debug("Step 5: Persisting transaction");
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Step 6: Publish event
        logger.debug("Step 6: Publishing transaction authorized event");
        eventPublisher.publishTransactionAuthorized(savedTransaction);

        logger.info("Transaction {} successfully authorized", savedTransaction.getTransactionId());
        return savedTransaction;
    }
}

