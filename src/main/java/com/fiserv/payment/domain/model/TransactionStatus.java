package com.fiserv.payment.domain.model;

public enum TransactionStatus {
    PENDING("PENDING", "Transaction pending authorization"),
    APPROVED("APPROVED", "Transaction authorized successfully"),
    DECLINED("DECLINED", "Transaction was declined"),
    FAILED("FAILED", "Transaction processing failed"),
    CANCELLED("CANCELLED", "Transaction was cancelled");

    private final String code;
    private final String description;

    TransactionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

