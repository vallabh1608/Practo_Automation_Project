package com.practo;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.practo.base.BaseTest;
import com.practo.pages.DenistSearchResults;
import com.practo.pages.DoctorsPage;
import com.practo.pages.HomePage;
import com.practo.pages.SkinPage;
import com.practo.pages.SurgeriesPage;
import com.practo.utils.ScreenshotUtil;
import org.apache.logging.log4j.LogManager; 
import org.apache.logging.log4j.Logger;

public class PractoTests extends BaseTest { 
    
    private static final Logger logger = LogManager.getLogger(PractoTests.class);
    
    @AfterMethod(alwaysRun = true)
    public void takeFailureScreenshot(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            ScreenshotUtil.takeScreenshot(driver, "screenshots",
                    "FAILED_" + result.getMethod().getMethodName());
            logger.error("Test {} FAILED. Screenshot captured.", result.getMethod().getMethodName());
        }
    }

    @Test(description = "Smoke: open home, select city, search specialization, verify results header",
          groups = {"smoke"}, priority = 0)
    public void smoke_search_only() {
        final String city = get("city");
        final String specialization = get("specialization");

        logger.info("Starting smoke test: searching for specialization {} in city {}", specialization, city);

        HomePage home = new HomePage(driver);
        logger.info("Opening home page and selecting city {}", city);
        home.selectLocation(city);

        logger.info("Searching for specialization {}", specialization);
        home.searchSpecialization(specialization);

        DenistSearchResults results = new DenistSearchResults(driver);
        logger.info("Fetching results count.");
        int count = results.getResultsCount(); 
        Assert.assertTrue(count > 0,
                "Expected at least 1 result in city=" + city + " for specialization=" + specialization);

        logger.info("Smoke test completed successfully. Found {} results.", count);
        ScreenshotUtil.takeScreenshot(driver, "screenshots", "TC1_smoke_test");
        logger.info("Screenshot captured for Smoke Test Build Path Test.");

    }

    @Test(description = "Search specialization in city, apply filters, and open booking for configured card index",
          groups = {"smoke", "regression"}, priority = 1)
    public void search_applyFilters_and_bookConfiguredCard() throws InterruptedException {
        final String city = get("city");
        final String specialization = get("specialization");
        final int cardIndex = getInt("bookCardIndex");

        logger.info("Starting doctor booking test in city {} for specialization {}.", city, specialization);

        HomePage home = new HomePage(driver);
        logger.info("Selecting city {} and searching specialization {}", city, specialization);
        home.selectLocation(city).searchSpecialization(specialization);

        DenistSearchResults results = new DenistSearchResults(driver);
        logger.info("Applying Patient Stories filter.");
        results.applyPatientStoriesFirstFilter();

        logger.info("Applying Experience 20+ filter.");
        results.applyExperience20PlusFilter();

        logger.info("Fetching results count after filters.");
        int count = results.getResultsCount();
        Assert.assertTrue(count > 0, "Expected some results after filters for city=" + city);

        logger.info("Booking doctor at card index {}", cardIndex);
        results.bookDoctorByCardIndex(cardIndex);

        logger.info("Displaying doctor appointment details.");
        results.DisplayDocAppointmentDetails();

        logger.info("Doctor booking scenario executed successfully.");
        ScreenshotUtil.takeScreenshot(driver, "screenshots", "TC2_Doctorpage");
        logger.info("Screenshot captured for Booking Doctors Details.");
    }

    @Test(description = "Search specialization then sort by Experience (High->Low) and print top 5",
          groups = {"regression"}, priority = 2)
    public void sortDoctorsByExperience_and_printTop5() {
        final String city = get("city");
        final String specialization = get("specialization");

        logger.info("Starting doctor sorting test in city {} for specialization {}.", city, specialization);

        HomePage home = new HomePage(driver);
        logger.info("Selecting city {} and searching specialization {}", city, specialization);
        home.selectLocation(city).searchSpecialization(specialization);

        DoctorsPage dp = new DoctorsPage(driver);
        logger.info("Sorting doctors by experience (High to Low).");
        dp.sortByExperienceHighToLow();

        logger.info("Printing top 5 doctors.");
        dp.printTop5Doctors();

        logger.info("Top 5 doctors printed successfully.");
        ScreenshotUtil.takeScreenshot(driver, "screenshots", "TC1_Doctors_High_To_Low");
        logger.info("Screenshot captured for Sort Doctors Results");
    }

    @Test(description = "Lab Tests -> Skin -> add Vitamin B12 + Vitamin Profile -> verify cart total (>0)",
          groups = {"smoke", "regression"}, priority = 3)
    public void addSkinTestsAndValidateTotal() {
        final String labCity = get("labCity");

        logger.info("Starting skin test flow for lab city {}", labCity);

        SkinPage skin = new SkinPage(driver);

        logger.info("Clicking on Lab Tests link.");
        skin.clickLabTests();

        logger.info("Selecting city {}", labCity);
        skin.selectCity(labCity);

        logger.info("Navigating to Skin category.");
        skin.selectSkinCategory();

        logger.info("Adding Vitamin B12 test to cart.");
        skin.addVitaminB12();

        logger.info("Adding Vitamin Profile test to cart.");
        skin.addVitaminProfile();

        logger.info("Fetching total price from cart.");
        String priceText = skin.getTotalPrice(); 
        String numeric = priceText.replaceAll("[^0-9]", "").trim();
        Assert.assertFalse(numeric.isEmpty(), "Total price should not be empty");

        double amount = Double.parseDouble(numeric);
        Assert.assertTrue(amount > 0.0, "Expected total price > 0 but was: " + amount);

        System.out.println("Displayed total amount for all selected skin tests:");
        System.out.println(priceText);
        logger.info("Displayed total amount for all selected skin tests: {}", priceText);

        ScreenshotUtil.takeScreenshot(driver, "screenshots", "TC3_Skin_Page");
        logger.info("Screenshot captured for Skin test flow.");
    }
    @Test(description = "Surgeries -> Cataract lead form -> choose surgeryCity -> book",
            groups = {"regression"}, priority = 4)
      public void fillCataractLeadForm_and_book() {
          final String surgeryCity = get("surgeryCity");
          final String patientName = get("patientName");
          final String patientPhone = get("patientPhone");
          logger.info("Starting cataract surgery booking test for patient {} in city {}", patientName, surgeryCity);
          SurgeriesPage page = new SurgeriesPage(driver);
          logger.info("Navigating to Surgeries page.");
          page.goToSurgeries();
          logger.info("Printing all available ailments.");
          page.printAllAilments();
          logger.info("Selecting Cataract option.");
          page.selectCataract();
          logger.info("Checking if lead form is visible.");
          boolean leadFormVisible =
                  driver.findElements(By.cssSelector("#Name-AIlment-Lead-Form")).size() > 0;
          Assert.assertTrue(leadFormVisible, "Lead form name input should be visible after selecting Cataract");
          logger.info("Filling lead form with patient name {} and phone {}", patientName, patientPhone);
          page.fillLeadForm(patientName, patientPhone);
          logger.info("Choosing city {}", surgeryCity);
          page.chooseCity(surgeryCity);
         
          page.bookAppointment();
          logger.info("clicking on booking appointment");
          page.otp();
          logger.info("Screenshot captured for Cataract booking flow.");
          ScreenshotUtil.takeScreenshot(driver, "screenshots", "Otp successful");
   
          Assert.assertTrue(driver.getTitle() != null && !driver.getTitle().trim().isEmpty(),
                  "Page title should be present after booking action");
          logger.info("Cataract surgery booking scenario executed successfully.");
      }
  }
   
   