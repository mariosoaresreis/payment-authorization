package com.fiserv.payment.domain.model;

public enum CardBrand {
    VISA("VISA"),
    MASTERCARD("MASTERCARD"),
    ELO("ELO"),
    AMEX("AMEX"),
    DISCOVER("DISCOVER"),
    PIX("PIX"),
    UNKNOWN("UNKNOWN");

    private final String code;

    CardBrand(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static CardBrand fromBin(String bin) {
        if (bin == null || bin.length() < 2) {
            return UNKNOWN;
        }

        int prefix = Integer.parseInt(bin.substring(0, 2));

        return switch (prefix) {
            case 4 -> VISA;
            case 5, 2 -> MASTERCARD;
            case 3 -> AMEX;
            case 6 -> ELO;
            case 65 -> DISCOVER;
            default -> UNKNOWN;
        };
    }
}

