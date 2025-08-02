package com.equipment;

import com.fasterxml.jackson.annotation.JsonProperty;
//import java.time.LocalDateTime;

/**
 * Represents an equipment event with temperature, power, and oil level data.
 */
public class EquipmentEvent {
    
    @JsonProperty("equipment_id")
    private int equipmentId;
    
    @JsonProperty("temperature")
    private double temperature;
    
    @JsonProperty("power")
    private int power;
    
    @JsonProperty("oil_level")
    private double oilLevel;
    
    @JsonProperty("timestamp")
    private String timestamp; // Change from LocalDateTime to String
    
    public EquipmentEvent() {
        this.timestamp = java.time.LocalDateTime.now().toString();
    }
    
    public EquipmentEvent(int equipmentId, double temperature, int power, double oilLevel) {
        this.equipmentId = equipmentId;
        this.temperature = temperature;
        this.power = power;
        this.oilLevel = oilLevel;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }
    
    // Getters and Setters
    public int getEquipmentId() {
        return equipmentId;
    }
    
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    public int getPower() {
        return power;
    }
    
    public void setPower(int power) {
        this.power = power;
    }
    
    public double getOilLevel() {
        return oilLevel;
    }
    
    public void setOilLevel(double oilLevel) {
        this.oilLevel = oilLevel;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return String.format("EquipmentEvent{equipmentId=%d, temperature=%.2f, power=%d, oilLevel=%.2f, timestamp=%s}",
                equipmentId, temperature, power, oilLevel, timestamp);
    }
} 