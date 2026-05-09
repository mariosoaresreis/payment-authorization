package com.fiserv.payment.application.fraud;

import com.fiserv.payment.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic fraud detection strategy using simple rule-based checks
 */
public class BasicFraudStrategy implements FraudDetectionStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BasicFraudStrategy.class);

    @Override
    public int calculateFraudScore(Transaction transaction) {
        int score = 0;

        // Rule 1: High transaction amount (above 5000) = 25 points
        if (transaction.getAmount().compareTo(new java.math.BigDecimal("5000")) > 0) {
            score += 25;
            logger.debug("High amount transaction: +25 points");
        }

        // Rule 2: International transaction (non-BRL currency) = 15 points
        if (transaction.getCurrency() != com.fiserv.payment.domain.model.Currency.BRL) {
            score += 15;
            logger.debug("International transaction: +15 points");
        }

        // Rule 3: High-risk merchant = 20 points
        if (transaction.getMerchant().getRiskLevel() == com.fiserv.payment.domain.model.RiskLevel.HIGH) {
            score += 20;
            logger.debug("High-risk merchant: +20 points");
        }

        // Rule 4: Transaction at suspicious hour = 10 points
        int hour = transaction.getTimestamp().getHour();
        if (hour >= 2 && hour < 5) {
            score += 10;
            logger.debug("Suspicious hour transaction: +10 points");
        }

        logger.debug("BasicFraudStrategy score for transaction {}: {}",
            transaction.getTransactionId(), score);

        return Math.min(score, 100); // Cap at 100
    }

    @Override
    public String getStrategyName() {
        return "BasicFraudStrategy";
    }
}

