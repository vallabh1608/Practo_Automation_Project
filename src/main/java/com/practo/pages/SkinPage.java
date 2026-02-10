package com.practo.pages;

import com.practo.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class SkinPage {
    private final WebDriver driver;
    private final WaitUtils wu;

    // ---------- PageFactory elements ----------
    @FindBy(xpath = "//a[@aria-label='Lab Tests']")
    private WebElement labTestsLink;

    @FindBy(xpath = "//input[@placeholder='Search for city']")
    private WebElement cityInput;

    @FindBy(xpath = "//div[text()='Skin']")
    private WebElement skinCategory;

    @FindBy(xpath = "//div[text()='Vitamin B12']")
    private WebElement vitaminB12;

    @FindBy(xpath = "//div[text()='Vitamin Profile']")
    private WebElement vitaminProfile;

    @FindBy(xpath = "//div[text()='Add to Cart']")
    private WebElement addToCart;

    private static final By TOTAL_PRICE_ELEMENT =
        By.xpath("//div[contains(@class,'u-font-bold') and contains(@class,'o-font-size--16')]/span");

    public SkinPage(WebDriver driver, int waitSeconds) {
        this.driver = driver;
        this.wu = new WaitUtils(driver, waitSeconds);
        PageFactory.initElements(driver, this);
    }
    public SkinPage(WebDriver driver) {
        this(driver, 15);
    }

    private void jsScrollIntoView(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);
    }
    private By cityOption(String city) {
        return By.xpath("//div[normalize-space(text())='" + city + "']");
    }

    public void clickLabTests() {
        wu.untilClickable(By.xpath("//a[@aria-label='Lab Tests']")).click();
    }

    public void selectCity(String city) {
        WebElement input = wu.untilClickable(By.xpath("//input[@placeholder='Search for city']"));
        input.clear();
        input.sendKeys(city);
        wu.untilClickable(cityOption(city)).click();
    }

    public void selectSkinCategory() {
        jsScrollIntoView(skinCategory);
        skinCategory.click();
    }

    public void addVitaminB12() {
        vitaminB12.click();
        addToCart.click();
        driver.navigate().back();
    }

    public void addVitaminProfile() {
        vitaminProfile.click();
        addToCart.click();
    }

    /**
     * Tries original total locator first.
     * If not found quickly (layout change), scans visible ‘₹’ amounts and returns the largest.
     */
    public String getTotalPrice() {
        try {
            WebElement total = wu.untilVisible(TOTAL_PRICE_ELEMENT);
            String txt = total.getText();
            if (txt != null && txt.contains("₹")) {
                return txt.replace("₹", "").trim();
            }
        } catch (TimeoutException ignored) { /* fallback */ }

        // Fallback: pick the largest visible price on page
        List<WebElement> rupees = driver.findElements(By.xpath("//*[contains(text(),'₹')]"));
        double best = -1;
        String bestText = null;

        for (WebElement el : rupees) {
            try {
                if (!el.isDisplayed()) continue;
                String t = el.getText();
                if (t == null || !t.contains("₹")) continue;

                String digits = t.replaceAll("[^0-9]", "");
                if (digits.isEmpty()) continue;

                double val = Double.parseDouble(digits);
                if (val > best) { best = val; bestText = t; }
            } catch (Exception ignore) { /* keep scanning */ }
        }

        if (bestText == null) {
            throw new TimeoutException("Could not locate cart total via known locators or fallback scan.");
        }
        return bestText.replace("₹", "").trim();
    }
}