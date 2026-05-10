package com.fiserv.payment.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO for transaction authorization response
 */
@Schema(description = "Response from transaction authorization")
public record AuthorizeTransactionResponse(

    @JsonProperty("transaction_id")
    @Schema(description = "Unique transaction identifier", example = "TXN20260509123456")
    String transactionId,

    @JsonProperty("status")
    @Schema(description = "Transaction status", example = "APPROVED", allowableValues = {"APPROVED", "DECLINED", "PENDING", "FAILED"})
    String status,

    @JsonProperty("authorization_code")
    @Schema(description = "Authorization code (if approved)", example = "VISA_a1b2c3d4")
    String authorizationCode,

    @JsonProperty("decline_reason")
    @Schema(description = "Reason for decline (if declined)")
    String declineReason,

    @JsonProperty("fraud_score")
    @Schema(description = "Fraud risk score (0-100)", example = "25")
    String fraudScore,

    @JsonProperty("timestamp")
    @Schema(description = "Transaction timestamp")
    LocalDateTime timestamp,

    @JsonProperty("amount")
    @Schema(description = "Transaction amount", example = "150.50")
    String amount,

    @JsonProperty("currency")
    @Schema(description = "Currency code", example = "BRL")
    String currency
) {}

