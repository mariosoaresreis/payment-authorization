package com.fiserv.payment.domain.model;

public enum FraudScore {
    LOW(0, 30, "Low risk transaction"),
    MEDIUM(31, 70, "Medium risk transaction"),
    HIGH(71, 100, "High risk transaction");

    private final int minScore;
    private final int maxScore;
    private final String description;

    FraudScore(int minScore, int maxScore, String description) {
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.description = description;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public String getDescription() {
        return description;
    }

    public static FraudScore fromScore(int score) {
        return switch (score) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 -> LOW;
            case 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70 -> MEDIUM;
            default -> HIGH;
        };
    }
}

