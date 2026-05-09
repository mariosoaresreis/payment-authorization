package com.fiserv.payment.infrastructure.persistence;

import com.fiserv.payment.domain.model.*;
import com.fiserv.payment.domain.ports.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TransactionRepository port for JPA
 * This adapter translates between domain Transaction objects and JPA entities
 *
 * This is an infrastructure adapter that implements the domain port,
 * keeping the domain layer completely independent from persistence details.
 */
@Repository
public class TransactionRepositoryAdapter implements TransactionRepository {

    private static final Logger logger = LoggerFactory.getLogger(TransactionRepositoryAdapter.class);
    private final JpaTransactionRepository jpaRepository;

    public TransactionRepositoryAdapter(JpaTransactionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        logger.debug("Persisting transaction {}", transaction.getTransactionId());

        TransactionEntity entity = toDomainEntity(transaction);
        TransactionEntity saved = jpaRepository.save(entity);

        logger.debug("Transaction {} persisted successfully", transaction.getTransactionId());
        return fromEntity(saved);
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        logger.debug("Fetching transaction {}", transactionId);
        return jpaRepository.findById(transactionId)
            .map(this::fromEntity);
    }

    @Override
    public List<Transaction> findByMerchantId(String merchantId) {
        logger.debug("Fetching transactions for merchant {}", merchantId);
        return jpaRepository.findByMerchantId(merchantId)
            .stream()
            .map(this::fromEntity)
            .collect(Collectors.toList());
    }

    // Conversion methods
    private TransactionEntity toDomainEntity(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(transaction.getTransactionId());
        entity.setMerchantId(transaction.getMerchant().getMerchantId());
        entity.setCardNumber(transaction.getCard().getCardNumber());
        entity.setCardHolder(transaction.getCard().getCardHolder());
        entity.setBin(transaction.getCard().getBin());
        entity.setCardBrand(transaction.getCard().getCardBrand().name());
        entity.setAmount(transaction.getAmount());
        entity.setCurrency(transaction.getCurrency().name());
        entity.setStatus(convertStatus(transaction.getStatus()));
        entity.setAuthorizationCode(transaction.getAuthorizationCode());
        entity.setDeclineReason(transaction.getDeclineReason());
        entity.setFraudScore(transaction.getFraudScore().name());

        return entity;
    }

    private Transaction fromEntity(TransactionEntity entity) {
        Card card = new Card(
            entity.getCardNumber(),
            entity.getCardHolder(),
            entity.getBin(),
            CardBrand.valueOf(entity.getCardBrand()),
            "",  // expiry month not stored in entity
            ""   // expiry year not stored in entity
        );

        Merchant merchant = new Merchant(
            entity.getMerchantId(),
            "", // merchant name not stored
            "", // mcc not stored
            "", // country not stored
            new BigDecimal("0"), // daily limit not stored
            RiskLevel.MEDIUM    // default risk level
        );

        Transaction transaction = new Transaction(card, merchant, entity.getAmount(),
            Currency.valueOf(entity.getCurrency()));

        transaction.setTransactionId(entity.getTransactionId());
        transaction.setStatus(convertStatus(entity.getStatus()));
        transaction.setFraudScore(FraudScore.valueOf(entity.getFraudScore()));

        if (entity.getAuthorizationCode() != null) {
            transaction.authorize(entity.getAuthorizationCode());
        }

        if (entity.getDeclineReason() != null) {
            transaction.decline(entity.getDeclineReason());
        }

        return transaction;
    }

    private TransactionStatus convertStatus(TransactionEntity.TransactionStatusEntity entity) {
        return switch (entity) {
            case PENDING -> TransactionStatus.PENDING;
            case APPROVED -> TransactionStatus.APPROVED;
            case DECLINED -> TransactionStatus.DECLINED;
            case FAILED -> TransactionStatus.FAILED;
            case CANCELLED -> TransactionStatus.CANCELLED;
        };
    }

    private TransactionEntity.TransactionStatusEntity convertStatus(TransactionStatus status) {
        return switch (status) {
            case PENDING -> TransactionEntity.TransactionStatusEntity.PENDING;
            case APPROVED -> TransactionEntity.TransactionStatusEntity.APPROVED;
            case DECLINED -> TransactionEntity.TransactionStatusEntity.DECLINED;
            case FAILED -> TransactionEntity.TransactionStatusEntity.FAILED;
            case CANCELLED -> TransactionEntity.TransactionStatusEntity.CANCELLED;
        };
    }
}

