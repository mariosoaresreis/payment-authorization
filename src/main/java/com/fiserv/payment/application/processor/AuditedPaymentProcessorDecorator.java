package com.fiserv.payment.application.processor;

import com.fiserv.payment.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator Pattern for Payment Processing
 *
 * Wraps a PaymentProcessor to add cross-cutting concerns:
 * - Logging
 * - Auditing
 * - Metrics
 * - Exception handling
 *
 * This keeps the main processor logic clean and allows composition
 * of multiple decorators.
 */
public class AuditedPaymentProcessorDecorator implements PaymentProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AuditedPaymentProcessorDecorator.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    private final PaymentProcessor delegate;
    private final java.time.Clock clock;

    public AuditedPaymentProcessorDecorator(PaymentProcessor delegate) {
        this(delegate, java.time.Clock.systemDefaultZone());
    }

    public AuditedPaymentProcessorDecorator(PaymentProcessor delegate, java.time.Clock clock) {
        this.delegate = delegate;
        this.clock = clock;
    }

    @Override
    public String process(Transaction transaction) {
        long startTime = java.time.Instant.now(clock).toEpochMilli();

        logger.debug("Starting payment processing with audit decorator for transaction {}",
            transaction.getTransactionId());

        auditLogger.info("PAYMENT_AUTHORIZED_START|transactionId={}|merchant={}|amount={}|currency={}",
            transaction.getTransactionId(),
            transaction.getMerchant().getMerchantId(),
            transaction.getAmount(),
            transaction.getCurrency());

        try {
            String result = delegate.process(transaction);

            long duration = java.time.Instant.now(clock).toEpochMilli() - startTime;

            auditLogger.info("PAYMENT_AUTHORIZED_SUCCESS|transactionId={}|authCode={}|durationMs={}|processor={}",
                transaction.getTransactionId(),
                result,
                duration,
                delegate.getProcessorName());

            logger.debug("Payment processing completed successfully in {}ms", duration);

            return result;

        } catch (Exception e) {
            long duration = java.time.Instant.now(clock).toEpochMilli() - startTime;

            auditLogger.error("PAYMENT_AUTHORIZED_FAILED|transactionId={}|error={}|durationMs={}|processor={}",
                transaction.getTransactionId(),
                e.getMessage(),
                duration,
                delegate.getProcessorName());

            logger.error("Payment processing failed after {}ms", duration, e);
            throw e;
        }
    }

    @Override
    public boolean canHandle(Transaction transaction) {
        return delegate.canHandle(transaction);
    }

    @Override
    public String getProcessorName() {
        return "[Audited] " + delegate.getProcessorName();
    }
}

