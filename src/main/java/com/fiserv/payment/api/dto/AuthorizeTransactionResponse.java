package com.fiserv.payment.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO for transaction authorization response
 */
@Schema(description = "Response from transaction authorization")
public class AuthorizeTransactionResponse {

    @JsonProperty("transaction_id")
    @Schema(description = "Unique transaction identifier", example = "TXN20260509123456")
    private String transactionId;

    @JsonProperty("status")
    @Schema(description = "Transaction status", example = "APPROVED", allowableValues = {"APPROVED", "DECLINED", "PENDING", "FAILED"})
    private String status;

    @JsonProperty("authorization_code")
    @Schema(description = "Authorization code (if approved)", example = "VISA_a1b2c3d4")
    private String authorizationCode;

    @JsonProperty("decline_reason")
    @Schema(description = "Reason for decline (if declined)")
    private String declineReason;

    @JsonProperty("fraud_score")
    @Schema(description = "Fraud risk score (0-100)", example = "25")
    private String fraudScore;

    @JsonProperty("timestamp")
    @Schema(description = "Transaction timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("amount")
    @Schema(description = "Transaction amount", example = "150.50")
    private String amount;

    @JsonProperty("currency")
    @Schema(description = "Currency code", example = "BRL")
    private String currency;

    // Constructors
    public AuthorizeTransactionResponse() {}

    public AuthorizeTransactionResponse(String transactionId, String status, String authorizationCode,
                                       String declineReason, String fraudScore, LocalDateTime timestamp,
                                       String amount, String currency) {
        this.transactionId = transactionId;
        this.status = status;
        this.authorizationCode = authorizationCode;
        this.declineReason = declineReason;
        this.fraudScore = fraudScore;
        this.timestamp = timestamp;
        this.amount = amount;
        this.currency = currency;
    }

    // Getters and setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

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

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}

