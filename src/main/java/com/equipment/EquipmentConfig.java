package com.equipment;

/**
 * Configuration class that defines the parameters for each equipment.
 */
public class EquipmentConfig {
    
    private final int equipmentId;
    private final double minTemperature;
    private final double maxTemperature;
    private final int power;
    
    public EquipmentConfig(int equipmentId, double minTemperature, double maxTemperature, int power) {
        this.equipmentId = equipmentId;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.power = power;
    }
    
    public int getEquipmentId() {
        return equipmentId;
    }
    
    public double getMinTemperature() {
        return minTemperature;
    }
    
    public double getMaxTemperature() {
        return maxTemperature;
    }
    
    public int getPower() {
        return power;
    }
    
    /**
     * Generates a random temperature within the configured range.
     * @return random temperature value
     */
    public double generateRandomTemperature() {
        if (minTemperature == maxTemperature) {
            return minTemperature;
        }
        return minTemperature + Math.random() * (maxTemperature - minTemperature);
    }
    
    @Override
    public String toString() {
        return String.format("EquipmentConfig{equipmentId=%d, temperatureRange=[%.1f, %.1f], power=%d}",
                equipmentId, minTemperature, maxTemperature, power);
    }
} 