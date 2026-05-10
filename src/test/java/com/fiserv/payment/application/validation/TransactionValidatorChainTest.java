package com.fiserv.payment.application.validation;

import com.fiserv.payment.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Chain of Responsibility validation
 */
public class TransactionValidatorChainTest {

    private TransactionValidator validationChain;
    private Transaction validTransaction;
    private String validExpiryYear;

    @BeforeEach
    public void setup() {
        validExpiryYear = String.valueOf(Year.now().plusYears(2).getValue());

        // Build validation chain
        CardExpiryValidator cardExpiryValidator = new CardExpiryValidator();
        BinValidator binValidator = new BinValidator();
        CreditLimitValidator creditLimitValidator = new CreditLimitValidator();

        cardExpiryValidator.setNext(binValidator);
        binValidator.setNext(creditLimitValidator);

        validationChain = cardExpiryValidator;

        // Create valid transaction
        Card card = new Card("4111111111111111", "John Doe", "411111",
            CardBrand.VISA, "12", validExpiryYear);
        Merchant merchant = new Merchant("MERCHANT123", "Test Merchant", "5411",
            "BR", new BigDecimal("10000"), RiskLevel.MEDIUM);
        validTransaction = new Transaction(card, merchant, new BigDecimal("100"), Currency.BRL);
    }

    @Test
    public void testValidTransactionPassesAllValidators() {
        String result = validationChain.validate(validTransaction);
        assertNull(result, "Valid transaction should pass all validators");
    }

    @Test
    public void testExpiredCardFails() {
        Card expiredCard = new Card("4111111111111111", "John Doe", "411111",
            CardBrand.VISA, "01", "2020");  // Expired
        Merchant merchant = new Merchant("MERCHANT123", "Test Merchant", "5411",
            "BR", new BigDecimal("10000"), RiskLevel.MEDIUM);
        Transaction expiredTransaction = new Transaction(expiredCard, merchant,
            new BigDecimal("100"), Currency.BRL);

        String result = validationChain.validate(expiredTransaction);
        assertNotNull(result, "Expired card should fail validation");
        assertTrue(result.contains("CardExpiryValidator"), "Should fail card expiry validation");
    }

    @Test
    public void testHighAmountExceedsLimit() {
        Card card = new Card("4111111111111111", "John Doe", "411111",
            CardBrand.VISA, "12", validExpiryYear);
        Merchant merchant = new Merchant("MERCHANT123", "Test Merchant", "5411",
            "BR", new BigDecimal("5000"), RiskLevel.MEDIUM);  // Daily limit 5000
        Transaction highAmountTransaction = new Transaction(card, merchant,
            new BigDecimal("10000"), Currency.BRL);  // Amount exceeds limit

        String result = validationChain.validate(highAmountTransaction);
        assertNotNull(result, "High amount should fail credit limit validation");
        assertTrue(result.contains("CreditLimitValidator"), "Should fail credit limit validation");
    }
}

