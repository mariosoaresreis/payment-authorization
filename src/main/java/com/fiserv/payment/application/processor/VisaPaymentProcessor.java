package com.fiserv.payment.application.processor;

import com.fiserv.payment.domain.model.CardBrand;
import com.fiserv.payment.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor for Visa transactions
 */
public class VisaPaymentProcessor implements PaymentProcessor {

    private static final Logger logger = LoggerFactory.getLogger(VisaPaymentProcessor.class);

    @Override
    public String process(Transaction transaction) {
        logger.info("Processing Visa transaction {}", transaction.getTransactionId());

        // Simulate processing
        String authCode = "VISA_" + java.util.UUID.randomUUID().toString().substring(0, 8);
        logger.debug("Visa authorization code: {}", authCode);

        return authCode;
    }

    @Override
    public boolean canHandle(Transaction transaction) {
        return transaction.getCard().getCardBrand() == CardBrand.VISA;
    }

    @Override
    public String getProcessorName() {
        return "VisaPaymentProcessor";
    }
}

