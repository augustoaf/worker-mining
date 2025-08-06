# Equipment Event Publisher

A Java Maven application that publishes equipment events to RabbitMQ. The application generates random equipment data and publishes it to a RabbitMQ exchange queue (fanout type) named `raw_equipment_events_exchange`. Also it creates a durable queue named `raw_equipment_events` linked to the exchange.

## Features

- Publishes equipment events every 1 second to an exchange queue. 
- Supports 4 different equipment types with specific configurations:
  - **Equipment 1**: Temperature 50-70°C, Power 1
  - **Equipment 2**: Temperature 60-100°C, Power 1
  - **Equipment 3**: Temperature 20-50°C, Power 1
  - **Equipment 4**: Temperature 0°C, Power 0
- Oil level management: starts at 250 and decreases by 1-3 units every 10 seconds
- JSON formatted messages with timestamp
- Comprehensive logging

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- RabbitMQ server running on localhost:5672 (default configuration)

## Installation

1. **Clone or download the project**
2. **Install dependencies:**
   ```bash
   mvn clean install
   ```

## Running the Application

### Option 1: Using Maven
```bash
mvn exec:java -Dexec.mainClass="com.equipment.EventPublisher"
```

### Option 2: Using JAR file
```bash
# Build the JAR
mvn clean package

# Run the JAR
java -jar target/worker-mining-1.0.0.jar
```

## RabbitMQ Setup

Make sure RabbitMQ is running on your system:

### Windows
```bash
# Start RabbitMQ service
net start RabbitMQ
```

### Linux/Mac
```bash
# Start RabbitMQ service
sudo systemctl start rabbitmq-server
# or
brew services start rabbitmq
```

### Docker
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

## Configuration

The application uses the following default RabbitMQ configuration:
- **Host**: localhost
- **Port**: 5672
- **Username**: guest
- **Password**: guest
- **Exchange**: raw_equipment_events_exchange
- **Queue**: raw_equipment_events

To modify these settings, edit the constants in `RabbitMQService.java`.

## Event Format

Each event is published as a JSON message with the following structure:

```json
{
  "equipment_id": 1,
  "temperature": 65.5,
  "power": 1,
  "oil_level": 245.2,
  "timestamp": "2024-01-15T10:30:45.123"
}
```

## Logging

The application logs to both console and file:
- Console: Real-time event publishing information
- File: `logs/equipment-events.log` (with daily rotation)

## Stopping the Application

Press `Ctrl+C` to gracefully stop the application. The shutdown hook will:
1. Stop the event publishing scheduler
2. Close the RabbitMQ connection
3. Clean up resources

## Troubleshooting

### Connection Issues
- Ensure RabbitMQ is running and accessible
- Check firewall settings
- Verify RabbitMQ credentials

### Build Issues
- Ensure Java 11+ is installed
- Verify Maven is properly configured
- Check network connectivity for dependency downloads

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── equipment/
│   │           ├── EventPublisher.java      # Main application class
│   │           ├── EquipmentEvent.java      # Event data model
│   │           ├── EquipmentConfig.java     # Equipment configuration
│   │           └── RabbitMQService.java     # RabbitMQ operations
│   └── resources/
│       └── logback.xml                      # Logging configuration
├── pom.xml                                  # Maven configuration
└── README.md                               # This file
```

## Dependencies

- **RabbitMQ Java Client**: For RabbitMQ communication
- **Jackson**: For JSON serialization
- **SLF4J + Logback**: For logging
- **Maven Shade Plugin**: For creating executable JAR

## License

This project is provided as-is for educational and development purposes. 