//package com.practo.utils;
//
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.edge.EdgeDriver;
//
//public class DriverFactory {
//
//	public static WebDriver driver;
//	public static WebDriver createDriver(String browser)
//	{
//		switch (browser.toLowerCase()) {
//		case "chrome":
//			WebDriverManager.chromedriver().setup();
//			driver = new ChromeDriver();
//			break;
//
//		case "firefox":
//			WebDriverManager.firefoxdriver().setup();
//			driver = new FirefoxDriver();
//			break;
//
//		case "edge":
//			driver = new EdgeDriver(); 
//			break;
//
//		default:
//			throw new IllegalArgumentException("Unsupported browser: " + browser);
//		}
//		
//		driver.manage().window().maximize();
//		return driver;
//	}
//
//	public static void closeDriver() {
//		if(driver != null) {			
//			driver.quit();
//		}
//	}
//
//	
//}
package com.practo.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class DriverFactory {

    private static WebDriver driver;

    // Keep your original method for simplicity
    public static WebDriver createDriver(String browser) {
        return createDriver(browser, null);
    }

    // Overload: allow ChromeOptions from BaseTest
    public static WebDriver createDriver(String browser, ChromeOptions chromeOptions) {

        String b = (browser == null) ? "chrome" : browser.trim().toLowerCase();

        switch (b) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = (chromeOptions == null) ? new ChromeDriver() : new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;

            case "edge":
               // WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        driver.manage().window().maximize();
        return driver;
    }

    public static WebDriver getDriver() {
        return driver;
    }

    public static void closeDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
