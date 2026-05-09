package com.fiserv.payment.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * DTO for creating authorization requests
 */
@Schema(description = "Request to authorize a payment transaction")
public class AuthorizeTransactionRequest {

    @JsonProperty("card_number")
    @Schema(description = "Masked or full card number", example = "4111111111111111")
    private String cardNumber;

    @JsonProperty("card_holder")
    @Schema(description = "Name of the card holder", example = "John Doe")
    private String cardHolder;

    @JsonProperty("bin")
    @Schema(description = "Bank Identification Number", example = "411111")
    private String bin;

    @JsonProperty("expiry_month")
    @Schema(description = "Card expiry month (MM)", example = "12")
    private String expiryMonth;

    @JsonProperty("expiry_year")
    @Schema(description = "Card expiry year (YYYY)", example = "2025")
    private String expiryYear;

    @JsonProperty("merchant_id")
    @Schema(description = "Unique merchant identifier", example = "MERCHANT123")
    private String merchantId;

    @JsonProperty("amount")
    @Schema(description = "Transaction amount", example = "150.50")
    private BigDecimal amount;

    @JsonProperty("currency")
    @Schema(description = "ISO 4217 currency code", example = "BRL", allowableValues = {"BRL", "USD", "EUR"})
    private String currency;

    // Constructors
    public AuthorizeTransactionRequest() {}

    public AuthorizeTransactionRequest(String cardNumber, String cardHolder, String bin,
                                      String expiryMonth, String expiryYear, String merchantId,
                                      BigDecimal amount, String currency) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.bin = bin;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.merchantId = merchantId;
        this.amount = amount;
        this.currency = currency;
    }

    // Getters and setters
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }

    public String getBin() { return bin; }
    public void setBin(String bin) { this.bin = bin; }

    public String getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }

    public String getExpiryYear() { return expiryYear; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}

