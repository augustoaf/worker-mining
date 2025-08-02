package com.equipment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Main application class that publishes equipment events to RabbitMQ.
 */
public class EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    private static final int EVENT_INTERVAL_SECONDS = 1;
    private static final int OIL_LEVEL_UPDATE_INTERVAL_SECONDS = 10;
    private static final double INITIAL_OIL_LEVEL = 250.0;
    private static final double MIN_OIL_DECREASE = 1.0;
    private static final double MAX_OIL_DECREASE = 3.0;
    
    private final RabbitMQService rabbitMQService;
    private final List<EquipmentConfig> equipmentConfigs;
    private final Map<Integer, Double> equipmentOilLevels;
    private final ScheduledExecutorService scheduler;
    
    public EventPublisher() {
        this.rabbitMQService = new RabbitMQService();
        this.equipmentConfigs = initializeEquipmentConfigs();
        this.equipmentOilLevels = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(2);
        
        // Initialize oil levels for all equipment
        equipmentConfigs.forEach(config -> 
            equipmentOilLevels.put(config.getEquipmentId(), INITIAL_OIL_LEVEL));
    }
    
    /**
     * Initializes the equipment configurations based on the requirements.
     */
    private List<EquipmentConfig> initializeEquipmentConfigs() {
        return Arrays.asList(
            new EquipmentConfig(1, 50.0, 70.0, 1),  // Equipment 1: temp 50-70, power 1
            new EquipmentConfig(2, 60.0, 100.0, 1), // Equipment 2: temp 60-100, power 1
            new EquipmentConfig(3, 20.0, 50.0, 1),  // Equipment 3: temp 20-50, power 1
            new EquipmentConfig(4, 0.0, 0.0, 0)     // Equipment 4: temp 0, power 0
        );
    }
    
    /**
     * Starts the event publishing process.
     */
    public void start() {
        try {
            // Connect to RabbitMQ
            rabbitMQService.connect();
            logger.info("Connected to RabbitMQ successfully");
            
            // Schedule event publishing every 1 second
            scheduler.scheduleAtFixedRate(
                this::publishEventsForAllEquipment,
                0,
                EVENT_INTERVAL_SECONDS,
                TimeUnit.SECONDS
            );
            
            // Schedule oil level updates every 10 seconds
            scheduler.scheduleAtFixedRate(
                this::updateOilLevels,
                OIL_LEVEL_UPDATE_INTERVAL_SECONDS,
                OIL_LEVEL_UPDATE_INTERVAL_SECONDS,
                TimeUnit.SECONDS
            );
            
            logger.info("Event publishing started. Press Ctrl+C to stop.");
            
            // Keep the application running
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
            
        } catch (IOException | TimeoutException e) {
            logger.error("Failed to connect to RabbitMQ", e);
            System.exit(1);
        }
    }
    
    /**
     * Publishes events for all equipment.
     */
    private void publishEventsForAllEquipment() {
        for (EquipmentConfig config : equipmentConfigs) {
            try {
                EquipmentEvent event = generateEvent(config);
                rabbitMQService.publishEvent(event);
                logger.info("Published event: {}", event);
            } catch (Exception e) {
                logger.error("Failed to publish event for equipment {}", config.getEquipmentId(), e);
            }
        }
    }
    
    /**
     * Generates an event for the given equipment configuration.
     */
    private EquipmentEvent generateEvent(EquipmentConfig config) {
        double temperature = config.generateRandomTemperature();
        int power = config.getPower();
        double oilLevel = equipmentOilLevels.get(config.getEquipmentId());
        
        return new EquipmentEvent(config.getEquipmentId(), temperature, power, oilLevel);
    }
    
    /**
     * Updates oil levels for all equipment by decreasing them randomly between 1-3.
     */
    private void updateOilLevels() {
        for (EquipmentConfig config : equipmentConfigs) {
            int equipmentId = config.getEquipmentId();
            double currentOilLevel = equipmentOilLevels.get(equipmentId);
            
            // Generate random decrease between 1 and 3
            double decrease = MIN_OIL_DECREASE + Math.random() * (MAX_OIL_DECREASE - MIN_OIL_DECREASE);
            double newOilLevel = Math.max(0, currentOilLevel - decrease);
            
            equipmentOilLevels.put(equipmentId, newOilLevel);
            logger.info("Updated oil level for equipment {}: {:.2f} -> {:.2f} (decrease: {:.2f})", 
                       equipmentId, currentOilLevel, newOilLevel, decrease);
        }
    }
    
    /**
     * Shuts down the application gracefully.
     */
    private void shutdown() {
        logger.info("Shutting down EventPublisher...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        rabbitMQService.close();
        logger.info("EventPublisher shutdown complete");
    }

    public void testConnection() throws Exception {
        try {
            rabbitMQService.connect();
            logger.info("Connection test successful");
            
            // Test publishing a simple event
            EquipmentEvent testEvent = new EquipmentEvent(999, 25.0, 1, 100.0);
            rabbitMQService.publishEvent(testEvent);
            logger.info("Test event published successfully");
            
            rabbitMQService.close();
        } catch (Exception e) {
            logger.error("Connection test failed", e);
            throw e;
        }
    }
    
    /**
     * Main method to start the application.
     */
    public static void main(String[] args) {
        EventPublisher publisher = new EventPublisher();
        
        // Test connection first
        try {
            publisher.testConnection();
        } catch (Exception e) {
            logger.error("Failed to test connection", e);
            System.exit(1);
        }
        
        // Then start the main application

        publisher.start();
        // Keep the main thread alive
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.info("Main thread interrupted");
        }
        
        
    }
} 