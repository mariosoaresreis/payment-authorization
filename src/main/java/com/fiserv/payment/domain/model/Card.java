package com.fiserv.payment.domain.model;

/**
 * Payment card representation. Pure domain model without framework dependencies.
 */
public class Card {

    private final String cardNumber;
    private final String cardHolder;
    private final String bin; // Bank Identification Number
    private final CardBrand cardBrand;
    private final String expiryMonth;
    private final String expiryYear;

    public Card(String cardNumber, String cardHolder, String bin, CardBrand cardBrand,
                String expiryMonth, String expiryYear) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.bin = bin;
        this.cardBrand = cardBrand;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public String getBin() {
        return bin;
    }

    public CardBrand getCardBrand() {
        return cardBrand;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public boolean isExpired(int month, int year) {
        int expiryYearInt = Integer.parseInt(expiryYear);
        int expiryMonthInt = Integer.parseInt(expiryMonth);

        if (year > expiryYearInt) return true;
        if (year == expiryYearInt && month > expiryMonthInt) return true;

        return false;
    }

    public String getMaskedCardNumber() {
        if (cardNumber.length() < 4) return "****";
        return "****" + cardNumber.substring(cardNumber.length() - 4);
    }
}

