package com.jochengehtab.luckwheel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class Manager {

    private final JSONArray arrayResult = new JSONArray();
    private JSONObject jsonObject;
    private JSONObject holder = null;

    /**
     * Set's a value in a JSON-File
     *
     * @param fileName The name of the File
     * @param id       The id
     * @param object   The value who gets put
     */
    public static void set(String fileName, String id, Object object) {
        JSONObject jsonObject = getHoleFile(fileName);
        jsonObject.put(id, object);

        try (FileWriter fileWriter = new FileWriter(fileName, false)) {
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set's an array in a JSON-File
     *
     * @param fileName     The name of the File
     * @param id           The id
     * @param innerObjects The {@link JSONObject} that get put into an array
     */
    public static void set(String fileName, String id, JSONObject[] innerObjects) {
        JSONObject jsonObject = getHoleFile(fileName);

        ArrayList<Object> jsonArray = new JSONArray();

        jsonArray.add(innerObjects);

        jsonArray = removeDuplicates(jsonArray);

        jsonObject.put(id, jsonArray);

        try (FileWriter fileWriter = new FileWriter(fileName, false)) {
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set's an array in a JSON-File
     *
     * @param fileName The name of the File
     * @param id       The id
     * @param object   The value who gets put
     */
    public static void set(String fileName, String id, Object[] object) {
        JSONObject jsonObject = getHoleFile(fileName);

        ArrayList<Object> jsonArray = new JSONArray();

        int index = 0;

        for (Object value : object) {
            jsonArray.add(index, value);
        }
        jsonArray = removeDuplicates(jsonArray);

        jsonObject.put(id, jsonArray);

        try (FileWriter fileWriter = new FileWriter(fileName, false)) {
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a value from a file
     *
     * @param fileName The name of the File
     * @return Returns a {@link JSONObject} as result of the hole file
     */
    public static JSONObject get(String fileName) {

        Manager manager = new Manager();

        File file = new File(fileName);

        if (!file.exists()) {
            throw new RuntimeException("The File: '" + file.getName() + "' does not exists!");
        }

        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(fileName)) {
            Object object = jsonParser.parse(fileReader);
            manager.jsonObject = (JSONObject) object;

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        return manager.jsonObject;
    }

    /**
     * Gets an Array from a JSON - file
     *
     * @param fileName  The Name of the file
     * @param arrayName The Name of the array
     * @return Returns a {@link JSONArray}
     */
    public static JSONArray getArray(String fileName, String arrayName) {

        Manager manager = new Manager();
        int index = -1;

        try (FileReader reader = new FileReader(fileName)) {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONArray jsonArray;
            if (!(jsonObject.get(arrayName) == null)) {
                jsonArray = (JSONArray) jsonObject.get(arrayName);
            } else {
                return null;
            }
            for (Object object : Objects.requireNonNull(jsonArray)) {
                index++;
                manager.arrayResult.add(index, object);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        return manager.arrayResult;
    }

    /**
     * Removes a value from a file
     *
     * @param fileName The name of the file
     * @param id       The id / key for the value
     */
    public static void remove(String fileName, String id) {
        JSONObject jsonObject = get(fileName);
        jsonObject.remove(id);
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes objects from an array
     *
     * @param fileName  The name of the file
     * @param arrayName The name of the array
     */
    public static void removeFromArray(String fileName, String arrayName, Object object) {
        JSONArray jsonArray = getArray(fileName, arrayName);
        Objects.requireNonNull(jsonArray).remove(object);
        Manager.set(fileName, arrayName, jsonArray);
    }

    /**
     * Check if the file exists
     *
     * @param fileName The Name of the File
     * @return Returns true when the file exists
     */
    public static boolean exists(String fileName) {
        return new File(fileName).exists();
    }

    /**
     * Removes duplicates from a {@link ArrayList}
     *
     * @param list The list that get check
     * @param <T>  The static Type
     * @return Returns a {@link ArrayList} without duplicates
     */
    private static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        ArrayList<T> newList = new ArrayList<>();

        for (T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }

        return newList;
    }

    /**
     * Returns the hole file
     *
     * @param fileName The name of the file
     * @return Returns the hole File in a {@link JSONObject}
     */
    public static JSONObject getHoleFile(String fileName) {
        Manager manager = new Manager();

        JSONParser parser = new JSONParser();
        try {
            manager.holder = (JSONObject) parser.parse(new FileReader(fileName));
        } catch (ParseException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return manager.holder == null ? new JSONObject() : manager.holder;
    }
}