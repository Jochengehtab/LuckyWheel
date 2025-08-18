package com.jochengehtab.luckwheel;


import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.documentfile.provider.DocumentFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JSON {
    private final Gson gson;
    private final Context context;
    private final DocumentFile configFile;

    /**
     * Constructs a JSON manager using a persisted SAF tree URI in SharedPreferences.
     */
    public JSON(Context context, String prefsName, String keyTreeUri, String fileName) {
        this.context = context.getApplicationContext();
        String treeUriString = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                .getString(keyTreeUri, null);
        if (treeUriString == null) {
            throw new RuntimeException("No tree URI in SharedPreferences for key '" + keyTreeUri + "'");
        }
        Uri treeUri = Uri.parse(treeUriString);
        DocumentFile treeRoot = DocumentFile.fromTreeUri(this.context, treeUri);
        if (treeRoot == null || !treeRoot.isDirectory()) {
            throw new RuntimeException("Invalid SAF tree URI: " + treeUriString);
        }

        DocumentFile file = treeRoot.findFile(fileName);
        if (file == null) {
            file = treeRoot.createFile("application/json", fileName);
            if (file == null) {
                throw new RuntimeException("Unable to create file '" + fileName + "'");
            }
        }
        this.configFile = file;

        // Initialize Gson with our custom adapter
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Constructs a JSON manager from a direct DocumentFile reference.
     */
    public JSON(Context context, DocumentFile configFile) {
        this.context = context.getApplicationContext();
        this.configFile = configFile;

        // Initialize Gson with our custom adapter
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Reads the entire JSON file into a Map.
     *
     * @return A map representing the JSON content. Returns an empty map if the file is empty or new.
     */
    private Map<String, JsonElement> readAsMap() throws IOException {
        if (configFile.length() == 0) {
            return new LinkedHashMap<>();
        }

        try (Reader reader = new InputStreamReader(
                context.getContentResolver().openInputStream(configFile.getUri()), StandardCharsets.UTF_8)) {

            Type type = new TypeToken<LinkedHashMap<String, JsonElement>>() {
            }.getType();
            Map<String, JsonElement> map = gson.fromJson(reader, type);
            return (map != null) ? map : new LinkedHashMap<>();
        }
    }

    /**
     * Writes a key-value pair to the JSON file, preserving other content.
     */
    public void write(String key, Object value) {
        try {
            Map<String, JsonElement> root = readAsMap();

            // Convert the new value to a JsonElement using our custom Gson instance
            JsonElement element = gson.toJsonTree(value);
            root.put(key, element);

            try (ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(configFile.getUri(), "wt")) {
                assert pfd != null;
                try (Writer writer = new OutputStreamWriter(
                        new FileOutputStream(pfd.getFileDescriptor()), StandardCharsets.UTF_8)) {
                    gson.toJson(root, writer);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to config file: " + configFile.getName(), e);
        }
    }

    /**
     * Reads a list from the JSON file.
     */
    public <E> List<E> readList(String key, Class<E> elementClass) {
        try {
            Map<String, JsonElement> root = readAsMap();
            JsonElement element = root.get(key);
            if (element == null || element.isJsonNull()) {
                return null;
            }
            // Define the type for a List of our element class
            Type listType = TypeToken.getParameterized(List.class, elementClass).getType();
            // Use our custom Gson instance to deserialize
            return gson.fromJson(element, listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read list from config file: " + configFile.getName(), e);
        }
    }

    /**
     * Reads an array from the JSON file.
     */
    public <T> T[] readArray(String key, Class<T[]> arrayClass) {
        try {
            Map<String, JsonElement> root = readAsMap();
            JsonElement element = root.get(key);
            if (element == null || element.isJsonNull()) {
                return null;
            }
            return gson.fromJson(element, arrayClass);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read array from config file: " + configFile.getName(), e);
        }
    }

    /**
     * Removes a key-value pair from the JSON file.
     */
    public void remove(String key) {
        try {
            Map<String, JsonElement> root = readAsMap();
            if (root.containsKey(key)) {
                root.remove(key);

                try (ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(configFile.getUri(), "wt")) {
                    assert pfd != null;
                    try (Writer writer = new OutputStreamWriter(
                            new FileOutputStream(pfd.getFileDescriptor()), StandardCharsets.UTF_8)) {
                        gson.toJson(root, writer);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to remove key from config file: " + configFile.getName(), e);
        }
    }

    /**
     * Gets all top-level keys from the JSON file.
     *
     * @return A Set of all keys.
     */
    public Set<String> getKeys() {
        try {
            return readAsMap().keySet();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read keys from config file: " + configFile.getName(), e);
        }
    }

    /**
     * Creates a JSON object using the app's internal storage,
     * which requires no user permissions.
     *
     * @param context The application context.
     * @param fileName The name of the JSON file to create or use (e.g., "session.json").
     * @return A new instance of your JSON class.
     */
    public static JSON createInstance(Context context, String fileName) {
        // Get the path to your app's private files directory
        File internalFile = new File(context.getFilesDir(), fileName);

        // If the file doesn't exist, create it.
        // This operation does NOT require SAF permissions.
        if (!internalFile.exists()) {
            try {
                if (!internalFile.createNewFile()) {
                    throw new RuntimeException("Failed to create the JSON instance! Something went wrong with creating the file!");
                }
            } catch (IOException e) {
                // It's good practice to handle this exception
                throw new RuntimeException("Failed to create internal config file", e);
            }
        }

        // Wrap the standard File object in a DocumentFile
        DocumentFile documentFile = DocumentFile.fromFile(internalFile);

        return new JSON(context, documentFile);
    }
}