package com.fiserv.payment.domain.model;

public enum Currency {
    BRL("986", 2),
    USD("840", 2),
    EUR("978", 2);

    private final String numericCode;
    private final int decimalPlaces;

    Currency(String numericCode, int decimalPlaces) {
        this.numericCode = numericCode;
        this.decimalPlaces = decimalPlaces;
    }

    public String getNumericCode() {
        return numericCode;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }
}

