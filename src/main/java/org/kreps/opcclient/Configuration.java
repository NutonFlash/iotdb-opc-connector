package org.kreps.opcclient;

import java.util.ArrayList;

import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

class Configuration {
    private String opcServerURI = "localhost:62640/IntegrationObjects/ServerSimulator";
    private long genInterval = 1000;
    private ArrayList<TagConfig> tagConfigs = new ArrayList<>() {
        {
            add(new TagConfig("light", "LREAL", TSDataType.FLOAT, new double[] { 0d, 1d }));
            add(new TagConfig("temperature", "LREAL", TSDataType.FLOAT, new double[] { 1d, 2d }));
            add(new TagConfig("humidity", "LREAL", TSDataType.FLOAT, new double[] { 2d, 3d }));
            add(new TagConfig("pressure", "LREAL", TSDataType.FLOAT, new double[] { 3d, 4d }));
            add(new TagConfig("frequency", "LREAL", TSDataType.FLOAT, new double[] { 4d, 5d }));
        }
    };

    private String iotdbHost = "192.168.0.202";
    private int iotdbPort = 6667;
    private String iotdbUsername = "root";
    private String iotdbPassword = "root";
    private String iotdbDevicePath = "root.kreps.djn01";

    public String getOpcServerURI() {
        return opcServerURI;
    }

    public long getGenInterval() {
        return genInterval;
    }

    public ArrayList<TagConfig> getTagConfigs() {
        return tagConfigs;
    }

    public String getIotdbHost() {
        return iotdbHost;
    }

    public int getIotdbPort() {
        return iotdbPort;
    }

    public String getIotdbUsername() {
        return iotdbUsername;
    }

    public String getIotdbPassword() {
        return iotdbPassword;
    }

    public String getIoTDBDevicePath() {
        return iotdbDevicePath;
    }
}
