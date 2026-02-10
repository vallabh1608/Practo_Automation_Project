package com.practo.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

public class WaitUtils {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver, int seconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    // ---------- By-based ----------
    public WebElement untilClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement untilVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // ---------- WebElement-based ----------
    public WebElement untilClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public WebElement untilVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public List<WebElement> untilAllVisible(List<WebElement> elements) {
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    public boolean untilStaleness(WebElement element) {
        return wait.until(ExpectedConditions.stalenessOf(element));
    }

    // ---------- List helper ----------
    public List<WebElement> untilListHasElements(Supplier<List<WebElement>> supplier) {
        return wait.until(d -> {
            List<WebElement> list = supplier.get();
            return (list != null && !list.isEmpty()) ? list : null;
        });
    }

    // ---------- URL / Title ----------
    public boolean untilUrlContains(String fragment) {
        return wait.until(ExpectedConditions.urlContains(fragment));
    }

    public boolean untilTitleContains(String titleFragment) {
        return wait.until(ExpectedConditions.titleContains(titleFragment));
    }

    public boolean untilTitleNotEmpty() {
        return wait.until(d -> {
            String t = d.getTitle();
            return t != null && !t.trim().isEmpty();
        }) != null;
    }
}