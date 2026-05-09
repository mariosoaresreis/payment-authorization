package com.fiserv.payment.infrastructure.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for transaction events
 * Processes authorized and declined transactions asynchronously
 * In a real scenario, this would call webhook endpoints to notify merchants
 */
@Service
public class TransactionEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionEventConsumer.class);
    private static final Logger webhookLogger = LoggerFactory.getLogger("WEBHOOK");

    @KafkaListener(topics = "transactions.authorized", groupId = "payment-auth-group")
    public void handleTransactionAuthorized(String message) {
        logger.info("Received transaction.authorized event: {}", message);

        try {
            // Parse message
            // In real scenario, would deserialize and call webhook
            webhookLogger.info("WEBHOOK_CALL|event=transaction.authorized|payload={}", message);

            logger.debug("Successfully processed authorized transaction event");
        } catch (Exception e) {
            logger.error("Failed to process transaction authorized event", e);
            // In production, would retry or publish to DLQ
        }
    }

    @KafkaListener(topics = "transactions.declined", groupId = "payment-auth-group")
    public void handleTransactionDeclined(String message) {
        logger.info("Received transaction.declined event: {}", message);

        try {
            // Parse message
            // In real scenario, would deserialize and call webhook
            webhookLogger.info("WEBHOOK_CALL|event=transaction.declined|payload={}", message);

            logger.debug("Successfully processed declined transaction event");
        } catch (Exception e) {
            logger.error("Failed to process transaction declined event", e);
            // In production, would retry or publish to DLQ
        }
    }
}

