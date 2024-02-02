package com.example.gymhibernatetask.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransactionLoggerTest {
    private Logger logger;
    private Appender<ILoggingEvent> mockAppender;
    private TransactionLogger transactionLogger;

    @BeforeEach
    public void setup() {
        logger = (Logger) LoggerFactory.getLogger("transactionLogger");
        mockAppender = mock(Appender.class);
        logger.addAppender(mockAppender);
        transactionLogger = new TransactionLogger();
    }

    @AfterEach
    public void cleanup() {
        logger.detachAppender(mockAppender);
    }

    @Test
    public void logTransactionRequestTest() {
        String message = "Test message";
        UUID testTransactionId = transactionLogger.logTransactionRequest(message);
        verify(mockAppender, times(1)).doAppend(argThat(argument -> {
            return argument.getFormattedMessage().contains("Transaction " + testTransactionId + " - " + message);
        }));
    }

    @Test
    public void logTransactionSuccessTest() {
        String message = "Test message";
        String username = "testUser";
        UUID uuid = UUID.randomUUID();
        transactionLogger.logTransactionSuccess(message, uuid, username);
        verify(mockAppender, times(1)).doAppend(argThat(argument -> {
            return argument.getFormattedMessage().contains("Transaction " + uuid + " - " + message);
        }));
    }

    @Test
    public void logTransactionMessageTest() {
        String message = "Test message";
        UUID uuid = UUID.randomUUID();
        transactionLogger.logTransactionMessage(message, uuid);
        verify(mockAppender, times(1)).doAppend(argThat(argument -> {
            return argument.getFormattedMessage().contains("Transaction " + uuid + " - " + message);
        }));
    }
}