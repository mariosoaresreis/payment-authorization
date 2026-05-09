package com.fiserv.payment.application.validation;

import com.fiserv.payment.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Detects transactions at suspicious hours (unusual times for the merchant)
 */
public class SuspiciousHourValidator extends TransactionValidator {

    private static final Logger logger = LoggerFactory.getLogger(SuspiciousHourValidator.class);
    private static final int SUSPICIOUS_HOUR_START = 2;  // 2 AM
    private static final int SUSPICIOUS_HOUR_END = 5;    // 5 AM

    @Override
    protected String performValidation(Transaction transaction) {
        logger.debug("Validating transaction hour");

        int hour = transaction.getTimestamp().getHour();

        if (hour >= SUSPICIOUS_HOUR_START && hour < SUSPICIOUS_HOUR_END) {
            logger.warn("Transaction at suspicious hour: {}", hour);
            // Note: We're just logging here, not rejecting
            // In a real scenario, we might increase fraud score instead
        }

        logger.debug("Transaction hour validation passed");
        return null;
    }
}

