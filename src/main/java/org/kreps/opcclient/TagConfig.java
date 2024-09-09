package org.kreps.opcclient;

import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

public class TagConfig {
    private String tagName;
    private String tagOPCDataType;
    private TSDataType tagIoTDBDataType;
    private double[] tagValueRange;

    public TagConfig(String tagName, String tagOPCDataType, TSDataType tagIoTDBDataType, double[] tagValueRange) {
        this.tagName = tagName;
        this.tagOPCDataType = tagOPCDataType;
        this.tagIoTDBDataType = tagIoTDBDataType;
        this.tagValueRange = tagValueRange;
    }

    public String getTagName() {
        return this.tagName;
    }

    public String getTagOPCDataType() {
        return this.tagOPCDataType;
    }

    public TSDataType getTagIoTDBDataType() {
        return this.tagIoTDBDataType;
    }

    public double[] getTagValueRange() {
        return this.tagValueRange;
    }
}
