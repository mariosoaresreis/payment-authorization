package com.fiserv.payment.application.validation;

import com.fiserv.payment.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates card expiry date
 */
public class CardExpiryValidator extends TransactionValidator {

    private static final Logger logger = LoggerFactory.getLogger(CardExpiryValidator.class);

    @Override
    protected String performValidation(Transaction transaction) {
        logger.debug("Validating card expiry");

        var card = transaction.getCard();
        int month = Integer.parseInt(card.getExpiryMonth());
        int year = Integer.parseInt(card.getExpiryYear());

        java.time.YearMonth currentYearMonth = java.time.YearMonth.now();

        if (year < currentYearMonth.getYear()) {
            logger.warn("Card expired: year {} < current year {}", year, currentYearMonth.getYear());
            return "[CardExpiryValidator] Card has expired";
        }

        if (year == currentYearMonth.getYear() && month < currentYearMonth.getMonthValue()) {
            logger.warn("Card expired: month {} < current month {}", month, currentYearMonth.getMonthValue());
            return "[CardExpiryValidator] Card has expired";
        }

        logger.debug("Card expiry valid");
        return null;
    }
}

