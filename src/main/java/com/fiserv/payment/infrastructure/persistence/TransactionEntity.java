package com.fiserv.payment.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for Transaction persistence
 * This is an infrastructure concern that bridges the domain model
 * with the database persistence layer.
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_merchant_id", columnList = "merchant_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class TransactionEntity {

    @Id
    private String transactionId;

    @Column(name = "merchant_id", nullable = false)
    private String merchantId;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "card_holder", nullable = false)
    private String cardHolder;

    @Column(name = "bin", nullable = false)
    private String bin;

    @Column(name = "card_brand")
    private String cardBrand;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatusEntity status;

    @Column(name = "authorization_code")
    private String authorizationCode;

    @Column(name = "decline_reason", length = 500)
    private String declineReason;

    @Column(name = "fraud_score")
    private String fraudScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public TransactionEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }

    public String getBin() { return bin; }
    public void setBin(String bin) { this.bin = bin; }

    public String getCardBrand() { return cardBrand; }
    public void setCardBrand(String cardBrand) { this.cardBrand = cardBrand; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public TransactionStatusEntity getStatus() { return status; }
    public void setStatus(TransactionStatusEntity status) { this.status = status; }

    public String getAuthorizationCode() { return authorizationCode; }
    public void setAuthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; }

    public String getDeclineReason() { return declineReason; }
    public void setDeclineReason(String declineReason) { this.declineReason = declineReason; }

    public String getFraudScore() { return fraudScore; }
    public void setFraudScore(String fraudScore) { this.fraudScore = fraudScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum TransactionStatusEntity {
        PENDING, APPROVED, DECLINED, FAILED, CANCELLED
    }
}

