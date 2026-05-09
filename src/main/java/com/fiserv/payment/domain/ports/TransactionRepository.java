package com.fiserv.payment.domain.ports;

import com.fiserv.payment.domain.model.Transaction;
import java.util.Optional;

/**
 * Output port for persisting transactions.
 * This interface abstracts the persistence mechanism (database agnostic).
 * The domain does NOT know whether this is JPA, MongoDB, etc.
 */
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(String transactionId);

    java.util.List<Transaction> findByMerchantId(String merchantId);
}

