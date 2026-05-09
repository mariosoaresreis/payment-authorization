package com.fiserv.payment.application.processor;

import com.fiserv.payment.domain.model.Transaction;

/**
 * Processor interface for different payment types
 * Processed by different acquirers (Visa, MasterCard, Pix, etc)
 */
public interface PaymentProcessor {

    /**
     * Processes the transaction
     * @return authorization code if approved, null if declined
     */
    String process(Transaction transaction);

    /**
     * Checks if this processor can handle the transaction
     */
    boolean canHandle(Transaction transaction);

    /**
     * Returns the processor name
     */
    String getProcessorName();
}

