package com.fiserv.payment.domain.exception;

public class TransactionAuthorizationException extends DomainException {
    public TransactionAuthorizationException(String message) {
        super(message);
    }

    public TransactionAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}

