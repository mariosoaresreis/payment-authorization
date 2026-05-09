package com.fiserv.payment.application.processor;

import com.fiserv.payment.domain.model.Transaction;
import com.fiserv.payment.domain.exception.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;

/**
 * Factory Pattern for Payment Processors
 *
 * Returns the appropriate payment processor based on card brand.
 * This pattern makes it easy to add new payment processors without
 * modifying client code.
 */
public class PaymentProcessorFactory {

    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessorFactory.class);
    private final List<PaymentProcessor> processors;

    public PaymentProcessorFactory() {
        this.processors = Arrays.asList(
            new VisaPaymentProcessor(),
            new MastercardPaymentProcessor()
        );
    }

    public PaymentProcessor getProcessor(Transaction transaction) {
        logger.debug("Looking for processor for card brand: {}",
            transaction.getCard().getCardBrand());

        return processors.stream()
            .filter(processor -> processor.canHandle(transaction))
            .findFirst()
            .orElseThrow(() -> {
                String msg = "No processor available for card brand: " +
                    transaction.getCard().getCardBrand();
                logger.error(msg);
                return new DomainException(msg);
            });
    }
}

