package com.fiserv.payment.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiserv.payment.domain.model.Transaction;
import com.fiserv.payment.domain.ports.TransactionEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka implementation of TransactionEventPublisher
 * Publishes transaction events to Kafka topics for async processing
 */
@Service
public class KafkaTransactionEventPublisher implements TransactionEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTransactionEventPublisher.class);
    private static final String AUTHORIZED_TOPIC = "transactions.authorized";
    private static final String DECLINED_TOPIC = "transactions.declined";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaTransactionEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                         ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishTransactionAuthorized(Transaction transaction) {
        logger.info("Publishing AUTHORIZED event for transaction {}", transaction.getTransactionId());

        try {
            TransactionEvent event = new TransactionEvent(
                transaction.getTransactionId(),
                transaction.getMerchant().getMerchantId(),
                transaction.getAmount().toPlainString(),
                transaction.getCurrency().name(),
                transaction.getStatus().name(),
                transaction.getAuthorizationCode(),
                transaction.getFraudScore().name()
            );

            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(AUTHORIZED_TOPIC, transaction.getTransactionId(), payload);

            logger.info("Transaction authorized event published: {}", transaction.getTransactionId());
        } catch (Exception e) {
            logger.error("Failed to publish transaction authorized event", e);
            throw new RuntimeException("Failed to publish Kafka event", e);
        }
    }

    @Override
    public void publishTransactionDeclined(Transaction transaction) {
        logger.info("Publishing DECLINED event for transaction {}", transaction.getTransactionId());

        try {
            TransactionEvent event = new TransactionEvent(
                transaction.getTransactionId(),
                transaction.getMerchant().getMerchantId(),
                transaction.getAmount().toPlainString(),
                transaction.getCurrency().name(),
                transaction.getStatus().name(),
                null,
                transaction.getDeclineReason()
            );

            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(DECLINED_TOPIC, transaction.getTransactionId(), payload);

            logger.info("Transaction declined event published: {}", transaction.getTransactionId());
        } catch (Exception e) {
            logger.error("Failed to publish transaction declined event", e);
            throw new RuntimeException("Failed to publish Kafka event", e);
        }
    }
}

