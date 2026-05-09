package com.fiserv.payment.domain.model;

/**
 * Merchant information. Pure domain model.
 */
public class Merchant {

    private final String merchantId;
    private final String merchantName;
    private final String mcc; // Merchant Category Code
    private final String country;
    private final BigDecimal dailyLimit;
    private final RiskLevel riskLevel;

    public Merchant(String merchantId, String merchantName, String mcc,
                    String country, BigDecimal dailyLimit, RiskLevel riskLevel) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.mcc = mcc;
        this.country = country;
        this.dailyLimit = dailyLimit;
        this.riskLevel = riskLevel;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getMcc() {
        return mcc;
    }

    public String getCountry() {
        return country;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
}

enum RiskLevel {
    LOW, MEDIUM, HIGH
}

