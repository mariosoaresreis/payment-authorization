package com.fiserv.payment.application.fraud;

import com.fiserv.payment.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simulated ML-based fraud detection strategy
 * In a real scenario, this would call an ML model API or use a trained model
 */
public class MLFraudStrategy implements FraudDetectionStrategy {

    private static final Logger logger = LoggerFactory.getLogger(MLFraudStrategy.class);

    @Override
    public int calculateFraudScore(Transaction transaction) {
        // Simulated ML scoring - in reality would call ML service
        int score = (int) (Math.random() * 100);

        // Apply some heuristics to make it more realistic
        if (transaction.getMerchant().getRiskLevel() == com.fiserv.payment.domain.model.RiskLevel.HIGH) {
            score = Math.min(score + 20, 100);
        }

        if (transaction.getAmount().compareTo(new java.math.BigDecimal("10000")) > 0) {
            score = Math.min(score + 15, 100);
        }

        logger.debug("MLFraudStrategy score for transaction {}: {}",
            transaction.getTransactionId(), score);

        return score;
    }

    @Override
    public String getStrategyName() {
        return "MLFraudStrategy";
    }
}

