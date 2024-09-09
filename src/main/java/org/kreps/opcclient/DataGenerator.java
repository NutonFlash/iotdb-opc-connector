package org.kreps.opcclient;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.plc4x.java.spi.values.PlcLREAL;

public class DataGenerator {
    private String opcServerURI;
    private long genInterval;
    private ArrayList<TagConfig> tagConfigs;

    public DataGenerator(String opcServerURI, long genInterval, ArrayList<TagConfig> tagConfigs) {
        this.opcServerURI = opcServerURI;
        this.genInterval = genInterval;
        this.tagConfigs = tagConfigs;
    }

    public void start() {
        String connectionString = String.format("opcua:tcp://%s",
                opcServerURI);
        try (PlcConnection plcConnection = PlcDriverManager.getDefault().getConnectionManager()
                .getConnection(connectionString)) {
            System.out.println("Connect to the OPC Server.");

            // Check if this connection support data writing.
            if (!plcConnection.getMetadata().isWriteSupported()) {
                System.err.println("This connection doesn't support writing.");
                return;
            }

            while (plcConnection.isConnected()) {
                PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();
                for (TagConfig genConfig : this.tagConfigs) {
                    Object tagValue = genRandomValue(genConfig.getTagValueRange());
                    builder.addTagAddress(genConfig.getTagName(), String.format("ns=2;s=%s;%s",
                            genConfig.getTagName(),
                            genConfig.getTagOPCDataType()), tagValue);
                }
                PlcWriteRequest writeRequest = builder.build();

                CompletableFuture<? extends PlcWriteResponse> asyncResponse = writeRequest.execute();
                asyncResponse.whenComplete((response, throwable) -> {
                    if (throwable != null) {
                        System.err.println("Writing error: " + throwable.getMessage());
                        return;
                    }

                    for (String tagName : response.getTagNames()) {
                        if (response.getResponseCode(tagName) == PlcResponseCode.OK) {
                            System.out.println("Value[" + tagName + "]: updated");
                        }
                        // Something went wrong, to output an error message instead.
                        else {
                            System.err.println("Error[" + tagName + "]: " +
                                    response.getResponseCode(tagName).name());
                        }
                    }
                });

                Thread.sleep(genInterval);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error connect to the OPC Server.");
        }
    }

    private Object genRandomValue(double[] valueRange) {
        double min = valueRange[0];
        double max = valueRange[1];
        Random random = new Random();
        double valueD = min + (max - min) * random.nextDouble();
        return new PlcLREAL(valueD);
    }
}
