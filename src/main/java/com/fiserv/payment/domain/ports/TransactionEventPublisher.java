package com.fiserv.payment.domain.ports;

import com.fiserv.payment.domain.model.Transaction;

/**
 * Output port for publishing transaction events.
 * Abstracts the messaging mechanism (Kafka, RabbitMQ, etc).
 */
public interface TransactionEventPublisher {

    void publishTransactionAuthorized(Transaction transaction);

    void publishTransactionDeclined(Transaction transaction);
}

