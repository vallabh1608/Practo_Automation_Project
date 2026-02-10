package com.practo.utils;
import java.io.*;
import java.util.Properties;


public final class ConfigReader {

    private final Properties props = new Properties();

    public ConfigReader() throws IOException {
        load();
    }

    private void load() throws IOException {
        // 1) Classpath: src/test/resources
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("Config.properties")) {
            if (is != null) {
                props.load(is);
                return;
            }
        }

        // 2) Project root: ./Config.properties
        File rootFile = new File("Config.properties");
        if (rootFile.exists()) {
            try (FileInputStream fis = new FileInputStream(rootFile)) {
                props.load(fis);
                return;
            }
        }

        // 3) Explicit override: -Dconfig.file=/absolute/or/relative/path
        String overridePath = System.getProperty("config.file");
        if (overridePath != null && !overridePath.isBlank()) {
            try (FileInputStream fis = new FileInputStream(overridePath)) {
                props.load(fis);
                return;
            }
        }

        // If nothing was found
        throw new FileNotFoundException(
            "Config.properties not found in classpath, project root, or via -Dconfig.file"
        );
    }

    public String get(String key) {
        return props.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public int getInt(String key) {
        String v = props.getProperty(key);
        if (v == null) {
            throw new IllegalArgumentException("Missing integer config: " + key);
        }
        return Integer.parseInt(v.trim());
    }

    public long getLong(String key, long defaultValue) {
        String v = props.getProperty(key);
        return (v == null || v.isBlank()) ? defaultValue : Long.parseLong(v.trim());
    }

    public boolean getBool(String key, boolean defaultValue) {
        String v = props.getProperty(key);
        return (v == null || v.isBlank()) ? defaultValue : Boolean.parseBoolean(v.trim());
    }

    public Properties asProperties() {
        Properties copy = new Properties();
        copy.putAll(this.props);
        return copy;
    }
}
