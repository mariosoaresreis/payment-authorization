package com.fiserv.payment.application.validation;

import com.fiserv.payment.domain.model.Transaction;

/**
 * Chain of Responsibility Pattern Implementation
 *
 * Each validator in the chain decides whether to:
 * 1. Accept and pass to the next validator
 * 2. Reject with a reason
 * 3. Skip validation
 *
 * This allows adding new validators without modifying existing code (Open/Closed Principle).
 */
public abstract class TransactionValidator {

    protected TransactionValidator next;

    public void setNext(TransactionValidator next) {
        this.next = next;
    }

    /**
     * Validates the transaction.
     * @return null if valid, or a reason string if invalid
     */
    public String validate(Transaction transaction) {
        String validationResult = performValidation(transaction);

        if (validationResult != null) {
            // Validation failed, reject immediately
            return validationResult;
        }

        // Validation passed, continue to next in chain
        if (next != null) {
            return next.validate(transaction);
        }

        // Chain completed successfully
        return null;
    }

    /**
     * To be implemented by concrete validators
     */
    protected abstract String performValidation(Transaction transaction);

    protected String getValidatorName() {
        return this.getClass().getSimpleName();
    }
}

