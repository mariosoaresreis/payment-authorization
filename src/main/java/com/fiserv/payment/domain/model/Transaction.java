package com.fiserv.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Core domain model for Payment Transactions.
 * This class contains no Spring or framework dependencies (Pure domain model).
 * It represents the business concept of a transaction in its purest form.
 */
public class Transaction {

    private String transactionId;
    private final Card card;
    private final Merchant merchant;
    private final BigDecimal amount;
    private final Currency currency;
    private final LocalDateTime timestamp;
    private TransactionStatus status;
    private String authorizationCode;
    private String declineReason;
    private FraudScore fraudScore;
    private boolean iplChecked;
    private String iplResponse;

    public Transaction(Card card, Merchant merchant, BigDecimal amount, Currency currency) {
        this.card = Objects.requireNonNull(card, "Card cannot be null");
        this.merchant = Objects.requireNonNull(merchant, "Merchant cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
        this.fraudScore = FraudScore.LOW;
        this.iplChecked = false;
    }

    // Builder pattern for complex object construction
    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public static class TransactionBuilder {
        private Card card;
        private Merchant merchant;
        private BigDecimal amount;
        private Currency currency = Currency.BRL;
        private String transactionId;

        public TransactionBuilder card(Card card) {
            this.card = card;
            return this;
        }

        public TransactionBuilder merchant(Merchant merchant) {
            this.merchant = merchant;
            return this;
        }

        public TransactionBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionBuilder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public TransactionBuilder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Transaction build() {
            Transaction transaction = new Transaction(card, merchant, amount, currency);
            if (transactionId != null) {
                transaction.transactionId = transactionId;
            }
            return transaction;
        }
    }

    public void authorize(String authorizationCode) {
        this.status = TransactionStatus.APPROVED;
        this.authorizationCode = authorizationCode;
    }

    public void decline(String reason) {
        this.status = TransactionStatus.DECLINED;
        this.declineReason = reason;
    }

    public void pending() {
        this.status = TransactionStatus.PENDING;
    }

    public void setFraudScore(FraudScore fraudScore) {
        this.fraudScore = fraudScore;
    }

    public void markIplChecked(String response) {
        this.iplChecked = true;
        this.iplResponse = response;
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public Card getCard() {
        return card;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public FraudScore getFraudScore() {
        return fraudScore;
    }

    public boolean isIplChecked() {
        return iplChecked;
    }

    public String getIplResponse() {
        return iplResponse;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", currency=" + currency +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }
}

