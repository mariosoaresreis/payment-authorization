package com.fiserv.payment.application.fraud;

import com.fiserv.payment.domain.model.Transaction;

/**
 * Strategy Pattern for Fraud Detection
 *
 * Allows different fraud detection strategies to be swapped at runtime.
 * Examples: BasicFraudStrategy, MLFraudStrategy, RulesEngineFraudStrategy
 *
 * This demonstrates the Strategy pattern where the algorithm for fraud detection
 * can be selected based on merchant type, transaction amount, or configuration.
 */
public interface FraudDetectionStrategy {

    /**
     * Calculates fraud score for a transaction.
     * @param transaction the transaction to analyze
     * @return fraud score between 0 (safe) and 100 (definite fraud)
     */
    int calculateFraudScore(Transaction transaction);

    /**
     * Strategy name for logging/metrics
     */
    String getStrategyName();
}

