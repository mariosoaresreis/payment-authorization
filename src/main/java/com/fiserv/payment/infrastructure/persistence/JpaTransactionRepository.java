package com.fiserv.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA repository interface
 */
@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, String> {
    List<TransactionEntity> findByMerchantId(String merchantId);
}

