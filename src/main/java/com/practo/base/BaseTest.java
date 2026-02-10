//package com.practo.base;
//
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.testng.annotations.*;
//
//import java.io.*;
//import java.time.Duration;
//import java.util.Properties;
//
//public class BaseTest {
//
//    protected WebDriver driver;
//    protected Properties config;
//
//    @BeforeClass(alwaysRun = true)
//    public void setUpClass() throws IOException {
//        config = new Properties();
//
//        
//        try (InputStream is = Thread.currentThread()
//                .getContextClassLoader()
//                .getResourceAsStream("Config.properties")) {
//            if (is != null) {
//                config.load(is);
//            } else {
//                // 2) Fallback: explicit file path (project root) if you still keep the file there
//                File file = new File("Config.properties");
//                if (file.exists()) {
//                    try (FileInputStream fis = new FileInputStream(file)) {
//                        config.load(fis);
//                    }
//                } else {
//                    // 3) Optional: allow override via -Dconfig.file=/path/to/Config.properties
//                    String override = System.getProperty("config.file");
//                    if (override != null && !override.isBlank()) {
//                        try (FileInputStream fis = new FileInputStream(override)) {
//                            config.load(fis);
//                        }
//                    } else {
//                        throw new FileNotFoundException(
//                            "Config.properties not found in classpath, project root, or -Dconfig.file");
//                    }
//                }
//            }
//        }
//        
//        // ---- WebDriver: single instance per class ----
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--start-maximized");
//        // options.addArguments("--headless=new"); // uncomment for CI
//        driver = new ChromeDriver(options);
//
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
//        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
//        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
//
//        // Open base URL once here
//        String baseUrl = config.getProperty("baseUrl");
//        if (baseUrl != null && !baseUrl.isBlank()) {
//            driver.get(baseUrl);
//        }
//    }
//
//    @BeforeMethod(alwaysRun = true)
//    public void navigateToHomeIfConfigured() {
//        String baseUrl = config.getProperty("baseUrl");
//        if (baseUrl != null && !baseUrl.isBlank()) {
//            driver.navigate().to(baseUrl);
//        }
//    }
//
//    @AfterClass(alwaysRun = true)
//    public void tearDownClass() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
//
//    protected String get(String key) { 
//    	return config.getProperty(key); }
//    protected int getInt(String key) { return Integer.parseInt(config.getProperty(key)); }
//    
//
//    public WebDriver getDriver() {
//       return driver;
//    }
//
//}
//
package com.practo.base;

import com.practo.utils.ConfigReader;
import com.practo.utils.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class BaseTest {

    protected WebDriver driver;
    protected Properties config;

    @BeforeClass(alwaysRun = true)
    public void setUpClass() throws IOException {

        // ✅ Use ConfigReader (now it is USED)
        ConfigReader reader = new ConfigReader();
        config = reader.asProperties(); // or reader.getProperties()

        // ✅ Read browser from config
        String browser = config.getProperty("browser", "chrome");

        // ✅ Options (only meaningful for Chrome; DriverFactory ignores for others)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        // optional headless from config
        String headless = config.getProperty("headless", "false");
        if ("true".equalsIgnoreCase(headless)) {
            options.addArguments("--headless=new");
        }

        // ✅ Use DriverFactory (now it is USED)
        driver = DriverFactory.createDriver(browser, options);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        // Open base URL once
        String baseUrl = config.getProperty("baseUrl");
        if (baseUrl != null && !baseUrl.isBlank()) {
            driver.get(baseUrl);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void navigateToHomeIfConfigured() {
        String baseUrl = config.getProperty("baseUrl");
        if (baseUrl != null && !baseUrl.isBlank()) {
            driver.navigate().to(baseUrl);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        DriverFactory.closeDriver();
    }

    protected String get(String key) {
        return config.getProperty(key);
    }

    protected int getInt(String key) {
        return Integer.parseInt(config.getProperty(key));
    }

    public WebDriver getDriver() {
        return driver;
    }
}