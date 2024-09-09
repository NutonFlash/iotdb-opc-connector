package org.kreps.opcclient;

import java.util.ArrayList;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

public class IoTDBWriter {
    private Session iotdbSession;
    private String devicePath;

    public IoTDBWriter(Session iotdbSession, String devicePath) {
        this.iotdbSession = iotdbSession;
        this.devicePath = devicePath;
    }

    public void writeTags(long timestamp, ArrayList<String> measurements,
            ArrayList<TSDataType> types, ArrayList<Object> values) {
        if (iotdbSession == null) {
            System.err.println("No IoTDB session found!");
            return;
        }
        try {
            iotdbSession.insertRecord(devicePath, timestamp, measurements, types, values);
            System.out.println(String.format("Inserted %s tags into IoTDB", measurements.size()));
        } catch (IoTDBConnectionException | StatementExecutionException exception) {
            exception.printStackTrace();
        }
    }
}
