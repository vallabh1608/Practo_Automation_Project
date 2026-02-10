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
