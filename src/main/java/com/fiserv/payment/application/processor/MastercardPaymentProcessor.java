package com.fiserv.payment.application.processor;

import com.fiserv.payment.domain.model.CardBrand;
import com.fiserv.payment.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor for MasterCard transactions
 */
public class MastercardPaymentProcessor implements PaymentProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MastercardPaymentProcessor.class);

    @Override
    public String process(Transaction transaction) {
        logger.info("Processing MasterCard transaction {}", transaction.getTransactionId());

        // Simulate processing
        String authCode = "MC_" + java.util.UUID.randomUUID().toString().substring(0, 8);
        logger.debug("MasterCard authorization code: {}", authCode);

        return authCode;
    }

    @Override
    public boolean canHandle(Transaction transaction) {
        return transaction.getCard().getCardBrand() == CardBrand.MASTERCARD;
    }

    @Override
    public String getProcessorName() {
        return "MastercardPaymentProcessor";
    }
}

