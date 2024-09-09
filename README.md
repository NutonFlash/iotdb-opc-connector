# IoTDB - OPC Server Connector

This repository contains a project that simulates the flow of data from a PLC to an OPC UA server and then stores the data in IoTDB. It consists of two main components: `DataGenerator` to simulate PLC data generation, and `DataReader` to subscribe to OPC server updates and write the data to IoTDB. Both components are executed together and are managed from the `Main.java` class.

## Table of Contents
- [Project Structure](#project-structure)
- [Setup and Usage](#setup-and-usage)
  - [Configuration Setup](#configuration-setup)
  - [Build and Run the Application](#build-and-run-the-application)
- [Dependencies](#dependencies)

## Project Structure

```bash
opcclient
 ┣ Configuration.java        # Handles configuration for the OPC UA server and IoTDB
 ┣ DataGenerator.java        # Simulates PLC and writes generated data to OPC UA server
 ┣ DataReader.java           # Subscribes to OPC server updates and writes them to IoTDB
 ┣ IoTDBWriter.java          # Handles writing data into IoTDB
 ┣ Main.java                 # Main entry point for launching DataGenerator and DataReader
 ┗ TagConfig.java            # Configuration for tags being written to OPC and IoTDB
```

## Setup and Usage

### Maven Project Setup

### Configuration Setup

To run the project, you must properly configure the **`Configuration.java`** file, which contains the OPC UA server and IoTDB setup parameters. The `Configuration` class allows you to configure:
- The **OPC UA server URI**.
- **IoTDB connection details** (hostname, port, username, and password).
- The **tag configuration** for the generated and subscribed data.


### Build and Run the Application

1. Build the project from teh root folder:
   ```bash
   mvn clean package

1. Once the project is built, run the application:
   ```bash
   java -jar target/iotdb-opc-connector.jar

2. The application will start both the `DataGenerator` (which simulates data and writes to the OPC server) and the `DataReader` (which subscribes to changes and writes them to IoTDB) simultaneously.

## Dependencies

The following dependencies are required and should be included in the `pom.xml` of the project:

- **Apache PLC4X**: For communication with the OPC UA server.
- **Apache IoTDB**: Time-series database used for storing sensor data.
- **Maven**: For building and managing the Java projects.