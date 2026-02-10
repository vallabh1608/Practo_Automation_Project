package com.practo.pages;

import com.practo.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class DoctorsPage {
    private final WebDriver driver;
    private final WaitUtils wu;

    // ---------- PageFactory elements ----------
    @FindBy(xpath = "//span[@data-qa-id='sort_by_selected']")
    private WebElement sortSelected;

    @FindBy(xpath = "//*[normalize-space(text())='Experience - High to Low']")
    private WebElement expHighToLow;

    @FindBy(css = "[data-qa-id='doctor_name']")
    private List<WebElement> doctorNames;

    @FindBy(css = "[data-qa-id='consultation_fee']")
    private List<WebElement> fees;

    // For waits
    private static final By DOCTOR_NAMES = By.cssSelector("[data-qa-id='doctor_name']");

    public DoctorsPage(WebDriver driver, int waitSeconds) {
        this.driver = driver;
        this.wu = new WaitUtils(driver, waitSeconds);
        PageFactory.initElements(driver, this);
    }
    public DoctorsPage(WebDriver driver) {
        this(driver, 12);
    }

    private void safeClick(WebElement el) {
        try { el.click(); }
        catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    public void sortByExperienceHighToLow() {
        wu.untilClickable(DOCTOR_NAMES); // ensure results visible before opening dropdown
        safeClick(sortSelected);
        safeClick(expHighToLow);
        wu.untilVisible(DOCTOR_NAMES);   // wait for refreshed list
    }

    public void printTop5Doctors() {
        wu.untilVisible(DOCTOR_NAMES);
        int maxToPrint = Math.min(doctorNames.size(), 5);
        for (int i = 0; i < maxToPrint; i++) {
            String name = doctorNames.get(i).getText().trim();
            String fee  = (i < fees.size()) ? fees.get(i).getText().trim() : "N/A";
            System.out.println(name + " | " + fee);
        }
    }
}
