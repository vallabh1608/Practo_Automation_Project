//
//package com.practo.utils;
//
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import org.openqa.selenium.WebDriver;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class ScreenshotUtil {
//
//	public static void takeScreenshot(WebDriver driver, String folderPath, String baseName) {
//		try {
//			if (driver == null) {
//				System.out.println("ScreenshotUtil: driver is null");
//				return;
//			}
//			if (!(driver instanceof TakesScreenshot)) {
//				System.out.println("ScreenshotUtil: driver does not support screenshots");
//				return;
//			}
//			
//			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//			String safeBase = (baseName == null || baseName.isBlank()) ? "screenshot"
//					: baseName.trim().replaceAll("\\s+", "_");
//			String fileName = safeBase + "_" + timestamp + ".png";
//
//			File folder = new File((folderPath == null || folderPath.isBlank()) ? "screenshots" : folderPath.trim());
//			if (!folder.exists()) {
//				folder.mkdirs();
//			}
//
//			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//			File dest = new File(folder, fileName);
//			Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
//			System.out.println("Screenshot saved at: " + dest.getAbsolutePath());
//		} catch (IOException e) {
//			System.out.println("Failed to save screenshot: " + e.getMessage());
//		} catch (Exception e) {
//			System.out.println("Error taking screenshot: " + e.getMessage());
//		}
//	}
//}
package com.practo.utils;

import org.openqa.selenium.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {

    public static String takeScreenshot(WebDriver driver, String folderPath, String baseName) {
        try {
            if (driver == null) {
                System.out.println("ScreenshotUtil: driver is null");
                return null;
            }
            if (!(driver instanceof TakesScreenshot)) {
                System.out.println("ScreenshotUtil: driver does not support screenshots");
                return null;
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String safeBase = (baseName == null || baseName.isBlank())
                    ? "screenshot"
                    : baseName.trim().replaceAll("\\s+", "_");

            String fileName = safeBase + "_" + timestamp + ".png";

            File folder = new File((folderPath == null || folderPath.isBlank()) ? "screenshots" : folderPath.trim());
            if (!folder.exists()) folder.mkdirs();

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(folder, fileName);

            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return dest.getAbsolutePath();
        } catch (IOException e) {
            System.out.println("Failed to save screenshot: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("Error taking screenshot: " + e.getMessage());
            return null;
        }
    }
}