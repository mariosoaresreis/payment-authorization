package com.fiserv.payment.domain.service;

import com.fiserv.payment.domain.model.RiskLevel;
import com.fiserv.payment.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Evaluates if a transaction should be declined based on fraud score
 * Thresholds vary by merchant risk level
 */
public class FraudThresholdEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(FraudThresholdEvaluator.class);

    // Fraud score thresholds for each risk level
    private static final int HIGH_RISK_THRESHOLD = 50;
    private static final int MEDIUM_RISK_THRESHOLD = 70;
    private static final int LOW_RISK_THRESHOLD = 85;

    public boolean shouldDeclineBasedOnFraud(int fraudScore, Transaction transaction) {
        RiskLevel merchantRisk = transaction.getMerchant().getRiskLevel();
        int threshold = getThresholdForRiskLevel(merchantRisk);

        boolean shouldDecline = fraudScore >= threshold;

        if (shouldDecline) {
            logger.warn("Transaction {} should be declined. Fraud score: {}, Threshold for {}: {}",
                transaction.getTransactionId(), fraudScore, merchantRisk, threshold);
        } else {
            logger.debug("Transaction {} passes fraud threshold. Fraud score: {}, Threshold for {}: {}",
                transaction.getTransactionId(), fraudScore, merchantRisk, threshold);
        }

        return shouldDecline;
    }

    private int getThresholdForRiskLevel(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case HIGH -> HIGH_RISK_THRESHOLD;
            case MEDIUM -> MEDIUM_RISK_THRESHOLD;
            case LOW -> LOW_RISK_THRESHOLD;
        };
    }
}

