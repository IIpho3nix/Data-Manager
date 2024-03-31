package me.iipho3nix.datamanager;

import java.util.List;

public class Data {
    private final String key;
    private Object data;
    private types type;
    private List<Data> dataList;

    public enum types {
        BOOLEAN, STRING, NUMBER, LIST
    }

    public Data(String key, boolean dataBoolean) {
        this.key = key;
        this.data = dataBoolean;
        this.type = types.BOOLEAN;
    }

    public Data(String key, String dataString) {
        this.key = key;
        this.data = dataString;
        this.type = types.STRING;
    }

    public Data(String key, Number dataNumber) {
        this.key = key;
        this.data = dataNumber;
        this.type = types.NUMBER;
    }

    public Data(String key, List<Data> dataList) {
        this.key = key;
        this.dataList = dataList;
        this.type = types.LIST;
    }

    public String getKey() {
        return key;
    }

    public types getType() {
        return type;
    }

    public Object getData() {
        if (type == types.LIST) {
            return dataList;
        }
        return data;
    }

    public Data getData(String key) {
        if (type == types.LIST) {
            return dataList.stream().filter(d -> d.getKey().equals(key)).findFirst().orElse(null);
        }
        return null;
    }

    public void setData(boolean dataBoolean) {
        this.data = dataBoolean;
        this.type = types.BOOLEAN;
    }

    public void setData(String dataString) {
        this.data = dataString;
        this.type = types.STRING;
    }

    public void setData(Number dataNumber) {
        this.data = dataNumber;
        this.type = types.NUMBER;
    }

    public void setData(List<Data> dataList) {
        this.dataList = dataList;
        this.type = types.LIST;
    }
}
