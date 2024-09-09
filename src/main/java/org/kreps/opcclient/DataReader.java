package org.kreps.opcclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionResponse;
import org.apache.plc4x.java.api.model.PlcSubscriptionHandle;

public class DataReader {
    private String opcServerURI;
    private ArrayList<TagConfig> tagConfigs;
    private IoTDBWriter iotdbWriter;

    public DataReader(String opcServerURI, ArrayList<TagConfig> tagConfigs, IoTDBWriter iotdbWriter) {
        this.opcServerURI = opcServerURI;
        this.tagConfigs = tagConfigs;
        this.iotdbWriter = iotdbWriter;
    }

    public void start() {
        String connectionString = String.format("opcua:tcp://%s",
                opcServerURI);
        try {
            PlcConnection plcConnection = PlcDriverManager.getDefault().getConnectionManager()
                    .getConnection(connectionString);

            System.out.println("Connect to the OPC Server.");

            // Check if this connection support subscribing to data.
            if (!plcConnection.getMetadata().isSubscribeSupported()) {
                System.err.println("This connection doesn't support subscribing.");
                return;
            }

            PlcSubscriptionRequest.Builder builder = plcConnection.subscriptionRequestBuilder();
            for (TagConfig genConfig : this.tagConfigs) {
                builder.addChangeOfStateTagAddress(genConfig.getTagName(), String.format("ns=2;s=%s",
                        genConfig.getTagName()));
            }
            PlcSubscriptionRequest subscriptionRequest = builder.build();

            CompletableFuture<? extends PlcSubscriptionResponse> asyncResponse = subscriptionRequest.execute();
            asyncResponse.whenComplete((response, throwable) -> {
                if (throwable != null) {
                    System.err.println("Subscription error: " + throwable.getMessage());
                    return;
                }

                for (PlcSubscriptionHandle subscriptionHandle : response.getSubscriptionHandles()) {
                    subscriptionHandle.register(plcSubscriptionEvent -> {
                        ArrayList<String> measurements = new ArrayList<>();
                        ArrayList<TSDataType> dataTypes = new ArrayList<>();
                        ArrayList<Object> values = new ArrayList<>();

                        long timestamp = plcSubscriptionEvent.getTimestamp().toEpochMilli();
                        Collection<String> tagNames = plcSubscriptionEvent.getTagNames();

                        System.out
                                .println(String.format("Tag names for %s time: %s", timestamp, tagNames.toString()));

                        for (String tagName : tagNames) {
                            Optional<TagConfig> tagConfig = tagConfigs.stream()
                                    .filter(tagConf -> tagConf.getTagName().equals(tagName)).findFirst();

                            if (tagConfig.isPresent()) {
                                measurements.add(tagName);
                                dataTypes.add(tagConfig.get().getTagIoTDBDataType());
                                values.add((float) plcSubscriptionEvent.getPlcValue(tagName).getDouble());

                                System.out.println(String.format("Measurement %s was added", tagName));
                                System.out.println(
                                        String.format("Value %s was added",
                                                plcSubscriptionEvent.getPlcValue(tagName).toString()));
                            } else {
                                System.err.println(String.format("Tag %s not found in tag confis", tagName));
                            }
                        }

                        if (measurements.size() > 0) {
                            iotdbWriter.writeTags(timestamp, measurements, dataTypes, values);
                        }
                    });
                }
            });

            // Adding a periodic "keep-alive" task to prevent the connection from being
            // closed.
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            }, 0, 10000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error connect to the OPC Server.");
        }
    }
}
