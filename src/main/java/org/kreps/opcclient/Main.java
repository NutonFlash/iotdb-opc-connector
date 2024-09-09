package org.kreps.opcclient;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.session.Session;

public class Main {
    public static void main(String[] args) {
        // Initialize configuration and dependencies
        Configuration config = new Configuration();

        DataGenerator dataGenerator = new DataGenerator(config.getOpcServerURI(), config.getGenInterval(),
                config.getTagConfigs());
        startDataGeneration(dataGenerator);

        Session session = createIoTDBSession(config);
        if (session == null)
            return; // Exit if session creation fails

        IoTDBWriter iotdbWriter = new IoTDBWriter(session, config.getIoTDBDevicePath());
        DataReader dataReader = new DataReader(config.getOpcServerURI(), config.getTagConfigs(), iotdbWriter);

        // Start reading data
        dataReader.start();
    }

    // Start data generation in a separate thread
    private static void startDataGeneration(DataGenerator dataGenerator) {
        Thread generatorThread = new Thread(dataGenerator::start);
        generatorThread.start();
    }

    // Create and open IoTDB session
    private static Session createIoTDBSession(Configuration config) {
        Session session = new Session.Builder()
                .host(config.getIotdbHost())
                .port(config.getIotdbPort())
                .username(config.getIotdbUsername())
                .password(config.getIotdbPassword())
                .build();
        try {
            session.open();
            System.out.println("Connected to IoTDB.");
        } catch (IoTDBConnectionException e) {
            System.err.println("Failed to connect to IoTDB: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return session;
    }
}
