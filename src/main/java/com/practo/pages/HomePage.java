package com.practo.pages;
import com.practo.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

    private final WebDriver driver;
    private final WaitUtils wu;

    // ---------- PageFactory elements ----------
    @FindBy(xpath = "//input[@placeholder='Search location']")
    private WebElement locationSearch;

    @FindBy(xpath = "//input[@placeholder='Search doctors, clinics, hospitals, etc.']")
    private WebElement doctorSearch;

    // ---------- Ctors ----------
    public HomePage(WebDriver driver, int waitSeconds) {
        this.driver = driver;
        this.wu = new WaitUtils(driver, waitSeconds);
        PageFactory.initElements(driver, this);
    }

    public HomePage(WebDriver driver) {
        this(driver, 12);
    }

    // ---------- private helpers ----------
    private void waitForPageReady() {
        wu.untilTitleNotEmpty(); // quick pre-check
        // document.readyState wait via JS
        new WaitUtils(driver, 12).untilTitleNotEmpty(); // optional redundancy if you want more robustness
        ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }

    private void scrollCenter(WebElement el) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el);
    }

    private void jsClick(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    private By citySuggestion(String city) {
        // Narrow target to common suggestion item title container
        String xp = String.format(
                "//div[contains(@class,'c-omni-suggestion-item__content__title') and contains(normalize-space(.),'%s')]",
                city
        );
        return By.xpath(xp);
    }

    // ---------- navigation ----------
    public HomePage open() {
        return open("https://www.practo.com/");
    }

    public HomePage open(String baseUrl) {
        driver.get(baseUrl);
        driver.manage().window().maximize();
        waitForPageReady();
        return this;
    }

    
    public HomePage selectLocation(String city) {
        WebElement loc = wu.untilClickable(locationSearch);
        scrollCenter(loc);

        loc.click();
        loc.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        loc.sendKeys(Keys.DELETE);
        loc.sendKeys(city);
        loc.sendKeys(Keys.BACK_SPACE);

        try {
            WebElement suggestion = wu.untilClickable(citySuggestion(city));
            scrollCenter(suggestion);
            suggestion.click();
        } catch (TimeoutException | ElementClickInterceptedException e) {
            // Fallback to ENTER to accept the typed city
            loc.sendKeys(Keys.ENTER);
        }

        waitForPageReady();
        return this;
    }

    /**
     * Searches for a specialization and submits via ENTER.
     */
    public void searchSpecialization(String specialization) {
        WebElement input = wu.untilClickable(doctorSearch);
        scrollCenter(input);
        input.click();
        input.clear();
        input.sendKeys(specialization);
        input.sendKeys(Keys.ENTER);
        waitForPageReady();
    }
}
