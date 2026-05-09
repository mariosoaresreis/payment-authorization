package com.fiserv.payment.application.fraud;

import com.fiserv.payment.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Fraud Detection Strategies
 */
public class FraudDetectionStrategyTest {

    private FraudDetectionStrategy basicStrategy;
    private FraudDetectionStrategy mlStrategy;
    private Merchant lowRiskMerchant;
    private Merchant highRiskMerchant;

    @BeforeEach
    public void setup() {
        basicStrategy = new BasicFraudStrategy();
        mlStrategy = new MLFraudStrategy();

        lowRiskMerchant = new Merchant("LOW_RISK", "Safe Store", "5411",
            "BR", new BigDecimal("10000"), RiskLevel.LOW);
        highRiskMerchant = new Merchant("HIGH_RISK", "Risky Store", "5411",
            "BR", new BigDecimal("10000"), RiskLevel.HIGH);
    }

    @Test
    public void testBasicStrategyLowScoreForLowRiskTransaction() {
        Card card = new Card("4111111111111111", "John Doe", "411111",
            CardBrand.VISA, "12", "2025");
        Transaction transaction = new Transaction(card, lowRiskMerchant,
            new BigDecimal("100"), Currency.BRL);

        int score = basicStrategy.calculateFraudScore(transaction);

        assertTrue(score < 30, "Low-risk transaction should have low fraud score");
    }

    @Test
    public void testBasicStrategyHighScoreForHighAmountTransaction() {
        Card card = new Card("4111111111111111", "John Doe", "411111",
            CardBrand.VISA, "12", "2025");
        Transaction transaction = new Transaction(card, lowRiskMerchant,
            new BigDecimal("10000"), Currency.BRL);  // High amount

        int score = basicStrategy.calculateFraudScore(transaction);

        assertTrue(score >= 25, "High amount transaction should add fraud points");
    }

    @Test
    public void testBasicStrategyHighScoreForHighRiskMerchant() {
        Card card = new Card("4111111111111111", "John Doe", "411111",
            CardBrand.VISA, "12", "2025");
        Transaction transaction = new Transaction(card, highRiskMerchant,
            new BigDecimal("100"), Currency.BRL);

        int score = basicStrategy.calculateFraudScore(transaction);

        assertTrue(score >= 20, "High-risk merchant should add fraud points");
    }

    @Test
    public void testMLStrategyReturnsValidScore() {
        Card card = new Card("4111111111111111", "John Doe", "411111",
            CardBrand.VISA, "12", "2025");
        Transaction transaction = new Transaction(card, lowRiskMerchant,
            new BigDecimal("100"), Currency.BRL);

        int score = mlStrategy.calculateFraudScore(transaction);

        assertTrue(score >= 0 && score <= 100, "ML strategy should return score between 0-100");
    }

    @Test
    public void testStrategyNameReturnable() {
        assertNotNull(basicStrategy.getStrategyName());
        assertNotNull(mlStrategy.getStrategyName());
    }
}

