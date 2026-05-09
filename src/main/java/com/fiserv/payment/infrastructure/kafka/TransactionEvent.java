package com.fiserv.payment.infrastructure.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Kafka event for transaction state changes
 */
public class TransactionEvent {

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("status")
    private String status;

    @JsonProperty("authorization_code")
    private String authorizationCode;

    @JsonProperty("decline_reason")
    private String declineReason;

    @JsonProperty("fraud_score")
    private String fraudScore;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    public TransactionEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public TransactionEvent(String transactionId, String merchantId, String amount,
                           String currency, String status, String authorizationCode,
                           String declineReason) {
        this();
        this.transactionId = transactionId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.authorizationCode = authorizationCode;
        this.declineReason = declineReason;
    }

    // Getters and setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAuthorizationCode() { return authorizationCode; }
    public void setAuthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; }

    public String getDeclineReason() { return declineReason; }
    public void setDeclineReason(String declineReason) { this.declineReason = declineReason; }

    public String getFraudScore() { return fraudScore; }
    public void setFraudScore(String fraudScore) { this.fraudScore = fraudScore; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

