package com.equipment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Service class for handling RabbitMQ operations.
 */
public class RabbitMQService {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);
    private static final String QUEUE_NAME = "raw_equipment_events";
    private static final String RABBITMQ_HOST = "localhost";
    private static final int RABBITMQ_PORT = 5672;
    private static final String RABBITMQ_USERNAME = "guest";
    private static final String RABBITMQ_PASSWORD = "guest";
    
    private Connection connection;
    private Channel channel;
    private final ObjectMapper objectMapper;
    
    public RabbitMQService() {
        this.objectMapper = new ObjectMapper();
        // Configure ObjectMapper to handle LocalDateTime serialization
        objectMapper.findAndRegisterModules();
    }
    
    /**
     * Establishes connection to RabbitMQ and creates the queue.
     */
    public void connect() throws IOException, TimeoutException {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(RABBITMQ_HOST);
            factory.setPort(RABBITMQ_PORT);
            factory.setUsername(RABBITMQ_USERNAME);
            factory.setPassword(RABBITMQ_PASSWORD);
            
            logger.info("Attempting to connect to RabbitMQ at {}:{}", RABBITMQ_HOST, RABBITMQ_PORT);
            connection = factory.newConnection();
            channel = connection.createChannel();
            
            // Declare the queue
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            logger.info("Queue '{}' declared successfully", QUEUE_NAME);
            
            // Verify queue exists
            try {
                channel.queueDeclarePassive(QUEUE_NAME);
                logger.info("Queue '{}' verification successful", QUEUE_NAME);
            } catch (IOException e) {
                logger.error("Queue '{}' verification failed", QUEUE_NAME, e);
                throw e;
            }
            
            // Enable publisher confirms
            channel.confirmSelect();
            logger.info("Publisher confirms enabled");
            
            // Verify connection is working
            if (!isConnected()) {
                throw new IOException("Connection verification failed");
            }
            
        } catch (Exception e) {
            logger.error("Failed to connect to RabbitMQ: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Publishes an equipment event to the RabbitMQ queue.
     * @param event the equipment event to publish
     */
    public void publishEvent(EquipmentEvent event) throws Exception {
        if (!isConnected()) {
            logger.error("Cannot publish event - not connected to RabbitMQ");
            return;
        }
        
        try {
            String message = objectMapper.writeValueAsString(event);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            
            // Wait for confirmation
            if (channel.waitForConfirms(5000)) {
                logger.info("Event confirmed published to queue '{}': {}", QUEUE_NAME, event);
            } else {
                logger.error("Event publish confirmation timeout: {}", event);
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize equipment event: {}", event, e);
            throw e;
        } catch (IOException | InterruptedException | TimeoutException e) {
            logger.error("Failed to publish event to RabbitMQ: {}", event, e);
            throw e;
        }
    }
    
    /**
     * Closes the RabbitMQ connection and channel.
     */
    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
            logger.info("RabbitMQ connection closed");
        } catch (IOException | TimeoutException e) {
            logger.error("Error closing RabbitMQ connection", e);
        }
    }
    
    /**
     * Checks if the service is connected to RabbitMQ.
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return connection != null && connection.isOpen() && 
               channel != null && channel.isOpen();
    }
} 