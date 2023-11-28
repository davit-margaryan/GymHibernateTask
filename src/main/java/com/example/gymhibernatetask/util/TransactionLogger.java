package com.example.gymhibernatetask.util;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Data
public class TransactionLogger {

    private final Logger transactionLogger = LoggerFactory.getLogger("transactionLogger");

    public UUID logTransactionRequest(String message) {
        UUID transactionId = UUID.randomUUID();
        transactionLogger.info("Transaction {} - {}", transactionId, message);
        return transactionId;
    }

    public void logTransactionSuccess(String message, UUID transactionId, String username) {
        transactionLogger.info("Transaction {} - {}. Username: {}", transactionId, message, username);
    }

    public void logTransactionMessage(String message, UUID transactionId) {
        transactionLogger.error("Transaction {} - {}", transactionId, message);
    }
}
