package com.fiserv.payment.application.validation;

import com.fiserv.payment.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates card BIN (Bank Identification Number) against a list of valid BINs
 */
public class BinValidator extends TransactionValidator {

    private static final Logger logger = LoggerFactory.getLogger(BinValidator.class);
    private static final java.util.Set<String> BLOCKED_BINS = java.util.Set.of(
        "000000", "111111", "999999"
    );

    @Override
    protected String performValidation(Transaction transaction) {
        logger.debug("Validating BIN");

        String bin = transaction.getCard().getBin();

        if (BLOCKED_BINS.contains(bin)) {
            logger.warn("BIN {} is in blocked list", bin);
            return "[BinValidator] Invalid or blocked BIN";
        }

        if (bin.length() < 6) {
            logger.warn("BIN {} length is less than 6", bin);
            return "[BinValidator] Invalid BIN length";
        }

        logger.debug("BIN {} is valid", bin);
        return null;
    }
}

