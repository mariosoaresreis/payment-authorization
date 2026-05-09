package com.fiserv.payment.domain.ports;

import com.fiserv.payment.domain.model.Transaction;

/**
 * Output port for notifying webhooks about transaction events.
 */
public interface WebhookNotificationPort {

    void notifyTransactionAuthorized(String merchantId, Transaction transaction);

    void notifyTransactionDeclined(String merchantId, Transaction transaction);
}

