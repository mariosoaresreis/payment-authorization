package com.fiserv.payment.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * DTO for creating authorization requests
 */
@Schema(description = "Request to authorize a payment transaction")
public record AuthorizeTransactionRequest(

    @JsonProperty("card_number")
    @Schema(description = "Masked or full card number", example = "4111111111111111")
    String cardNumber,

    @JsonProperty("card_holder")
    @Schema(description = "Name of the card holder", example = "John Doe")
    String cardHolder,

    @JsonProperty("bin")
    @Schema(description = "Bank Identification Number", example = "411111")
    String bin,

    @JsonProperty("expiry_month")
    @Schema(description = "Card expiry month (MM)", example = "12")
    String expiryMonth,

    @JsonProperty("expiry_year")
    @Schema(description = "Card expiry year (YYYY)", example = "2025")
    String expiryYear,

    @JsonProperty("merchant_id")
    @Schema(description = "Unique merchant identifier", example = "MERCHANT123")
    String merchantId,

    @JsonProperty("amount")
    @Schema(description = "Transaction amount", example = "150.50")
    BigDecimal amount,

    @JsonProperty("currency")
    @Schema(description = "ISO 4217 currency code", example = "BRL", allowableValues = {"BRL", "USD", "EUR"})
    String currency
) {}

