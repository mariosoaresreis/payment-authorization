package com.fiserv.payment.api.controller;

import com.fiserv.payment.api.dto.AuthorizeTransactionRequest;
import com.fiserv.payment.api.dto.AuthorizeTransactionResponse;
import com.fiserv.payment.domain.model.*;
import com.fiserv.payment.domain.service.TransactionAuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Payment Authorization API
 *
 * Demonstrates:
 * - Clean separation of concerns (API layer doesn't contain business logic)
 * - Proper HTTP semantics
 * - OpenAPI/Swagger documentation
 * - DTO mapping between API and domain layers
 */
@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Payment Authorization", description = "APIs for authorizing payment transactions")
public class TransactionAuthorizationController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionAuthorizationController.class);

    private final TransactionAuthorizationService authorizationService;

    @Autowired
    public TransactionAuthorizationController(TransactionAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping("/authorize")
    @Operation(
        summary = "Authorize a payment transaction",
        description = "Process a payment transaction through fraud detection and validation pipeline"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction processed successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AuthorizeTransactionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuthorizeTransactionResponse> authorizeTransaction(
            @RequestBody AuthorizeTransactionRequest request) {

        logger.info("Received authorization request for merchant: {}", request.getMerchantId());

        try {
            // Build domain models from request DTO
            Card card = new Card(
                request.getCardNumber(),
                request.getCardHolder(),
                request.getBin(),
                CardBrand.fromBin(request.getBin()),
                request.getExpiryMonth(),
                request.getExpiryYear()
            );

            // Mock merchant lookup - in real scenario would query from database
            Merchant merchant = new Merchant(
                request.getMerchantId(),
                "Test Merchant",
                "5411",
                "BR",
                new java.math.BigDecimal("10000"),
                RiskLevel.MEDIUM
            );

            Transaction transaction = new Transaction(card, merchant, request.getAmount(),
                Currency.valueOf(request.getCurrency()));

            // Generate transaction ID
            String transactionId = "TXN_" + System.currentTimeMillis() + "_" +
                UUID.randomUUID().toString().substring(0, 8);
            transaction.setTransactionId(transactionId);

            // Process authorization
            Transaction authorizedTransaction = authorizationService.authorizeTransaction(transaction);

            // Map to response DTO
            AuthorizeTransactionResponse response = new AuthorizeTransactionResponse(
                authorizedTransaction.getTransactionId(),
                authorizedTransaction.getStatus().getCode(),
                authorizedTransaction.getAuthorizationCode(),
                authorizedTransaction.getDeclineReason(),
                authorizedTransaction.getFraudScore().name(),
                authorizedTransaction.getTimestamp(),
                authorizedTransaction.getAmount().toPlainString(),
                authorizedTransaction.getCurrency().name()
            );

            logger.info("Transaction {} processed with status: {}",
                transactionId, authorizedTransaction.getStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing authorization request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction details", description = "Retrieve details of a transaction by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction found"),
        @ApiResponse(responseCode = "404", description = "Transaction not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getTransaction(@PathVariable String transactionId) {
        logger.debug("Fetching transaction: {}", transactionId);
        // Implementation would fetch from repository
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}

