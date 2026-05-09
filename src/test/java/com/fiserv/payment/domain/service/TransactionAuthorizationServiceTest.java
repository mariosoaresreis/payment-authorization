package com.fiserv.payment.domain.service;

import com.fiserv.payment.domain.model.*;
import com.fiserv.payment.domain.ports.TransactionRepository;
import com.fiserv.payment.domain.ports.TransactionEventPublisher;
import com.fiserv.payment.application.fraud.BasicFraudStrategy;
import com.fiserv.payment.application.processor.VisaPaymentProcessor;
import com.fiserv.payment.application.validation.TransactionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionAuthorizationService
 * Demonstrates TDD approach with mock dependencies
 */
public class TransactionAuthorizationServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionEventPublisher eventPublisher;

    @Mock
    private TransactionValidator validationChain;

    private TransactionAuthorizationService authorizationService;
    private Transaction testTransaction;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        authorizationService = new TransactionAuthorizationService(
            validationChain,
            new BasicFraudStrategy(),
            new VisaPaymentProcessor(),
            transactionRepository,
            eventPublisher,
            new FraudThresholdEvaluator()
        );

        // Setup test transaction
        Card card = new Card("4111111111111111", "John Doe", "411111",
            CardBrand.VISA, "12", "2025");
        Merchant merchant = new Merchant("MERCHANT123", "Test Merchant", "5411",
            "BR", new BigDecimal("10000"), RiskLevel.LOW);
        testTransaction = new Transaction(card, merchant, new BigDecimal("100"), Currency.BRL);
        testTransaction.setTransactionId("TXN_TEST_001");
    }

    @Test
    public void testValidationFailureDeclines() {
        // Arrange
        when(validationChain.validate(testTransaction))
            .thenReturn("Card has expired");
        when(transactionRepository.save(testTransaction))
            .thenReturn(testTransaction);

        // Act
        Transaction result = authorizationService.authorizeTransaction(testTransaction);

        // Assert
        assertEquals(TransactionStatus.DECLINED, result.getStatus());
        assertNotNull(result.getDeclineReason());
        verify(eventPublisher, times(1)).publishTransactionDeclined(testTransaction);
        verify(transactionRepository, times(1)).save(testTransaction);
    }

    @Test
    public void testSuccessfulAuthorization() {
        // Arrange
        when(validationChain.validate(testTransaction))
            .thenReturn(null);  // Validation passes
        when(transactionRepository.save(any(Transaction.class)))
            .thenReturn(testTransaction);

        // Act
        Transaction result = authorizationService.authorizeTransaction(testTransaction);

        // Assert
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertNotNull(result.getAuthorizationCode());
        verify(eventPublisher, times(1)).publishTransactionAuthorized(any(Transaction.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testFraudScoreCalculated() {
        // Arrange
        when(validationChain.validate(testTransaction))
            .thenReturn(null);
        when(transactionRepository.save(any(Transaction.class)))
            .thenReturn(testTransaction);

        // Act
        Transaction result = authorizationService.authorizeTransaction(testTransaction);

        // Assert
        assertNotNull(result.getFraudScore());
    }
}

