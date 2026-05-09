package com.fiserv.payment.application.validation;

import com.fiserv.payment.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates merchant credit limit is not exceeded
 */
public class CreditLimitValidator extends TransactionValidator {

    private static final Logger logger = LoggerFactory.getLogger(CreditLimitValidator.class);

    @Override
    protected String performValidation(Transaction transaction) {
        logger.debug("Validating credit limit");

        var merchant = transaction.getMerchant();
        var amount = transaction.getAmount();
        var dailyLimit = merchant.getDailyLimit();

        if (amount.compareTo(dailyLimit) > 0) {
            logger.warn("Transaction amount {} exceeds daily limit {}", amount, dailyLimit);
            return "[CreditLimitValidator] Transaction amount exceeds daily limit";
        }

        logger.debug("Credit limit validation passed");
        return null;
    }
}

