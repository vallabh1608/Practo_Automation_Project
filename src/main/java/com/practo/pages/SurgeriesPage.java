package com.practo.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.practo.utils.WaitUtils;

public class SurgeriesPage {
    private final WebDriver driver;
    private final WaitUtils wu;

    private static final int TIMEOUT_SECONDS = 20;

    // ---------- PageFactory elements ----------
    @FindBy(id = "resendOtp")
    WebElement resendOtpBtn;
 
    @FindBy(xpath = "//div[@class='mobile-text-preview']")
    WebElement mobilePreviewText;
 
    @FindBy(id = "otpSentMsg")
    WebElement otpMessageText;
    @FindBy(linkText = "Surgeries")
    private WebElement surgeriesLink;

    @FindBy(className = "AilmentItem-module_itemText__XvCHL")
    private List<WebElement> ailmentElements;

    @FindBy(xpath = "//p[@class='mt-12px AilmentItem-module_itemText__XvCHL']")
    private WebElement cataractItem;

    @FindBy(id = "Name-AIlment-Lead-Form")
    private WebElement nameInput;

    @FindBy(id = "Phone-AIlment-Lead-Form")
    private WebElement phoneInput;

    @FindBy(xpath = "//span[@class='ailmentLeadForm-module_text__BkrHT']")
    private WebElement cityDropdownTrigger;

    @FindBy(xpath = "(//button[normalize-space(.)='Book Appointment'])[2]")
    private WebElement bookAppointmentBtn;

    public SurgeriesPage(WebDriver driver) {
        this.driver = driver;
        this.wu = new WaitUtils(driver, TIMEOUT_SECONDS);
        PageFactory.initElements(driver, this);
    }

    public void goToSurgeries() {
        wu.untilClickable(By.linkText("Surgeries")).click();
    }

    public void printAllAilments() {
        wu.untilVisible(By.className("AilmentItem-module_itemText__XvCHL"));
        for (WebElement element : ailmentElements) {
            System.out.println(element.getText());
        }
    }

    public void selectCataract() {
        cataractItem.click();
    }

    public void fillLeadForm(String name, String phone) {
        nameInput.sendKeys(name);
        phoneInput.sendKeys(phone);
    }

    public void chooseCity(String city) {
        cityDropdownTrigger.click();
        By dynamicCity = By.xpath("(//h1[normalize-space(.)='" + city + "'])[2]");
        wu.untilClickable(dynamicCity).click();
    }

    
    public void bookAppointment() {
        bookAppointmentBtn.click();
    }
        public void otp() {
        	WebDriverWait wait=new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        	By otpIframe = By.cssSelector("iframe[data-qa-id='otp-modal-iframe']");
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(otpIframe));
     
            // Inside iframe: click "Resend OTP" and read message + mobile preview
            
     
            String num = wait.until(ExpectedConditions.visibilityOf(mobilePreviewText)).getText();
            String msg = wait.until(ExpectedConditions.visibilityOf(otpMessageText)).getText();
     
            System.out.println("OTP message: " + msg + " " + num);
     
            // Switch back to default content
            driver.switchTo().defaultContent();
        
    
}
}
    
 