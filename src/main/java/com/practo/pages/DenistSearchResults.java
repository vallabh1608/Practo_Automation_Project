package com.practo.pages;

import com.practo.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class DenistSearchResults {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

   
    @FindBy(xpath = "//span[text()='Patient Stories']")
    private WebElement patientStoriesTab;

    @FindBy(xpath = "//span[normalize-space(.)='Experience']")
    private WebElement experienceTab;

    @FindBy(xpath = "//h1[@class='u-xx-large-font u-bold']")
    private WebElement resultsCountHeader;

   
    @FindBy(css = "[data-qa-id='doctor_card']")
    private List<WebElement> doctorCards;

    @FindBy(css = "[data-qa-id='slot_time']")
    private List<WebElement> slotTimes;

    @FindBy(xpath = "//*[@data-qa-id='doctor_review_count_list']/li[1]")
    private WebElement patientStoryOption;

    @FindBy(xpath = "//span[normalize-space(.)='20+ Years of experience']")
    private WebElement exp20Plus;

  
    private final By bookButtonInCard = By.xpath(".//div[contains(text(),'No Booking Fee')]");

  
    @FindBy(xpath = "//div[@data-qa-id='doctor_name']")
    private WebElement doctorName;

    @FindBy(xpath = "//div[@data-qa-id='doctor_qualifications']")
    private WebElement doctorQualifications;

    @FindBy(xpath = "//div[@data-qa-id='practice_name']")
    private WebElement practiceArea;

    @FindBy(xpath = "//div[@class='u-cushion c-appointment-info__row']//span[2]")
    private List<WebElement> appointmentDateTimeSpans;

    public DenistSearchResults(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver, 12);
        PageFactory.initElements(driver, this);
    }

  

    private void scrollIntoViewCenter(WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el);
    }

    private void click(WebElement el) {
        waitUtils.untilClickable(el);
        scrollIntoViewCenter(el);
        try {
            el.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    private void waitForResultsRefresh() {
        // Snapshot old list; wait until staleness or re-population
        if (doctorCards != null && !doctorCards.isEmpty()) {
            waitUtils.untilStaleness(doctorCards.get(0));
        }
        waitUtils.untilListHasElements(() -> doctorCards);
    }


    public DenistSearchResults waitForResults() {
        waitUtils.untilListHasElements(() -> doctorCards);
        waitUtils.untilAllVisible(doctorCards);
        return this;
    }

   
    public DenistSearchResults waitForFirstSlot() {
        waitUtils.untilListHasElements(() -> slotTimes);
        waitUtils.untilAllVisible(slotTimes);
        return this;
    }

   

    public void applyPatientStoriesFirstFilter() {
        waitForResults();
        waitUtils.untilClickable(patientStoriesTab);
        scrollIntoViewCenter(patientStoriesTab);
        patientStoriesTab.click();

        click(patientStoryOption);
        waitForResultsRefresh();
    }

    public void applyExperience20PlusFilter() {
        waitForResults();
        waitUtils.untilClickable(experienceTab);
        scrollIntoViewCenter(experienceTab);
        experienceTab.click();

        click(exp20Plus);
        waitForResultsRefresh();
    }


    public int getResultsCount() {
        waitForResults();
        String text = waitUtils.untilVisible(resultsCountHeader).getText();
        String leading = text.split(" ")[0].replaceAll("[^0-9]", "");
        return leading.isEmpty() ? 0 : Integer.parseInt(leading);
    }


    public void bookDoctorByCardIndex(int cardIndex1Based) throws InterruptedException {
        waitForResults();

        int idx = Math.max(1, cardIndex1Based);
        if (idx > doctorCards.size()) {
            throw new IllegalArgumentException(
                    "Requested card index " + idx + " but only " + doctorCards.size() + " cards visible.");
        }

        WebElement card = doctorCards.get(idx - 1);
        scrollIntoViewCenter(card);

        WebElement bookBtn = card.findElement(bookButtonInCard); // relative to the card
        scrollIntoViewCenter(bookBtn);

       
        Thread.sleep(500);

        try {
            waitUtils.untilClickable(bookBtn).click(); // using By overload via relative element isn't possible; we already have WebElement
        } catch (Exception e) {
           
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bookBtn);
        }

       
        waitForFirstSlot();
        WebElement firstSlot = slotTimes.get(0);
        click(firstSlot);
    }

    public void DisplayDocAppointmentDetails() {
        String docName = waitUtils.untilVisible(doctorName).getText();
        String docQuals = waitUtils.untilVisible(doctorQualifications).getText();
        String prac = waitUtils.untilVisible(practiceArea).getText();

        waitUtils.untilAllVisible(appointmentDateTimeSpans);
        String date = appointmentDateTimeSpans.get(0).getText();
        String time = appointmentDateTimeSpans.get(1).getText();

        System.out.println("Your Appointment Details :- ");
        System.out.println("Doctor Name : " + docName);
        System.out.println("Doctor Qualifications : " + docQuals);
        System.out.println("Practice Area : " + prac);
        System.out.println("Slot Timings : " + date + " " + time);
    }

    private void click(By locator) {
        WebElement el = waitUtils.untilClickable(locator);
        scrollIntoViewCenter(el);
        try {
            el.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }
}