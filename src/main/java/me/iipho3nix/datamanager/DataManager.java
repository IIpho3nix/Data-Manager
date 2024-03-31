package me.iipho3nix.datamanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataManager {
    private final File dataFile;
    private final Gson gson;
    CopyOnWriteArrayList<Data> dataList = new CopyOnWriteArrayList<>();

    public DataManager(File dataFile) {
        this.dataFile = dataFile;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public DataManager addData(Data... data) {
        Collections.addAll(dataList, data);
        return this;
    }

    public DataManager removeData(Data... data) {
        for (Data d : data) {
            dataList.remove(d);
        }
        return this;
    }

    public DataManager removeAllData() {
        dataList.clear();
        return this;
    }

    public Data getData(String key) {
        for (Data d : dataList) {
            if (d.getKey().equals(key)) {
                return d;
            }
        }
        return null;
    }

    public CopyOnWriteArrayList<Data> getDataList() {
        return dataList;
    }

    public DataManager saveData() {
        JsonObject jsonObject = new JsonObject();
        for (Data d : dataList) {
            saveDataRecursively(jsonObject, d);
        }
        FileUtils.writeToFile(gson.toJson(jsonObject), dataFile);
        return this;
    }

    private void saveDataRecursively(JsonObject jsonObject, Data data) {
        switch (data.getType()) {
            case BOOLEAN:
                jsonObject.addProperty(data.getKey() + "$BOOLEAN", (Boolean) data.getData());
                break;
            case STRING:
                jsonObject.addProperty(data.getKey() + "$STRING", (String) data.getData());
                break;
            case NUMBER:
                jsonObject.addProperty(data.getKey() + "$NUMBER", (Number) data.getData());
                break;
            case LIST:
                JsonArray jsonArray = new JsonArray();
                List<Data> subDataList = (List<Data>) data.getData();
                for (Data subData : subDataList) {
                    JsonObject subJsonObject = new JsonObject();
                    saveDataRecursively(subJsonObject, subData);
                    jsonArray.add(subJsonObject);
                }
                jsonObject.add(data.getKey() + "$LIST", jsonArray);
                break;
        }
    }

    public DataManager loadData() {
        String fileContent = FileUtils.readFromFile(dataFile);
        if (!fileContent.isEmpty()) {
            JsonObject jsonObject = gson.fromJson(fileContent, JsonObject.class);
            loadJsonData(jsonObject);
        }
        return this;
    }

    private void loadJsonData(JsonObject jsonObject) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            String[] split = key.split("\\$");
            String dataKey = split[0];
            String dataType = split[1];
            switch (dataType) {
                case "BOOLEAN":
                    addData(new Data(dataKey, value.getAsBoolean()));
                    break;
                case "STRING":
                    addData(new Data(dataKey, value.getAsString()));
                    break;
                case "NUMBER":
                    addData(new Data(dataKey, value.getAsNumber()));
                    break;
                case "LIST":
                    JsonArray jsonArray = value.getAsJsonArray();
                    ArrayList<Data> dataList = new ArrayList<>();
                    for (JsonElement element : jsonArray) {
                        JsonObject subJsonObject = element.getAsJsonObject();
                        Data subData = createDataFromJsonObject(subJsonObject);
                        dataList.add(subData);
                    }
                    addData(new Data(dataKey, dataList));
                    break;
            }
        }
    }

    private Data createDataFromJsonObject(JsonObject jsonObject) {
        String key = jsonObject.entrySet().iterator().next().getKey();
        JsonElement value = jsonObject.get(key);
        String[] split = key.split("\\$");
        String dataKey = split[0];
        String dataType = split[1];
        switch (dataType) {
            case "BOOLEAN":
                return new Data(dataKey, value.getAsBoolean());
            case "STRING":
                return new Data(dataKey, value.getAsString());
            case "NUMBER":
                return new Data(dataKey, value.getAsNumber());
            case "LIST":
                JsonArray jsonArray = value.getAsJsonArray();
                ArrayList<Data> dataList = new ArrayList<>();
                for (JsonElement element : jsonArray) {
                    JsonObject subJsonObject = element.getAsJsonObject();
                    Data subData = createDataFromJsonObject(subJsonObject);
                    dataList.add(subData);
                }
                return new Data(dataKey, dataList);
            default:
                return null;
        }
    }

    private class FileUtils {
        public static void writeToFile(String content, File file) {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String readFromFile(File file) {
            StringBuilder builder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }
    }
}
