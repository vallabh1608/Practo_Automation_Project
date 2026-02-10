package com.practo.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.practo.base.BaseTest;

public class ExtentReportManager implements ITestListener {

    private ExtentSparkReporter sparkReporter;
    private ExtentReports extent;

    // ✅ Thread-safe ExtentTest for parallel execution
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private String repName;

    @Override
    public void onStart(ITestContext testContext) {

        System.out.println(">>> ExtentReportManager onStart triggered <<<");

        // ✅ Create reports folder if not present
        new File("./reports").mkdirs();

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        repName = "Test-Report-" + timeStamp + ".html";

        sparkReporter = new ExtentSparkReporter("./reports/" + repName);
        sparkReporter.config().setDocumentTitle("Practo Automation Report");
        sparkReporter.config().setReportName("Practo Automation Testing");
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("Application", "Practo");
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", "QA");

        // Optional system info from testng.xml parameters
        String os = testContext.getCurrentXmlTest().getParameter("os");
        String browser = testContext.getCurrentXmlTest().getParameter("browser");

        if (os != null) extent.setSystemInfo("Operating System", os);
        if (browser != null) extent.setSystemInfo("Browser", browser);

        List<String> includedGroups = testContext.getCurrentXmlTest().getIncludedGroups();
        if (includedGroups != null && !includedGroups.isEmpty()) {
            extent.setSystemInfo("Groups", includedGroups.toString());
        }
    }

    // ✅ Create ExtentTest here (correct place)
    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(result.getMethod().getMethodName());
        test.assignCategory(result.getMethod().getGroups());
        extentTest.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().log(Status.PASS, result.getMethod()
        		.getMethodName() + " got successfully executed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        extentTest.get().log(Status.FAIL, result.getMethod().getMethodName() + " got failed");
        extentTest.get().log(Status.INFO, result.getThrowable()); // full stack trace

        // ✅ Get the real test class instance (not new BaseTest())
        Object obj = result.getInstance();

        if (obj instanceof BaseTest) {
            BaseTest base = (BaseTest) obj;

            // ✅ Take screenshot and attach to report
            String imgPath = ScreenshotUtil.takeScreenshot(
                    base.getDriver(),
                    "screenshots",
                    result.getMethod().getMethodName()
            );

            if (imgPath != null) {
                extentTest.get().addScreenCaptureFromPath(imgPath);
            } else {
                extentTest.get().log(Status.WARNING, "Screenshot path is null - screenshot not attached");
            }
        } else {
            extentTest.get().log(Status.WARNING, "Test class is not extending BaseTest, driver not available.");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        extentTest.get().log(Status.SKIP, result.getMethod().getMethodName() + " got skipped");
        if (result.getThrowable() != null) {
            extentTest.get().log(Status.INFO, result.getThrowable());
        }
    }

    @Override
    public void onFinish(ITestContext testContext) {
        if (extent != null) {
            extent.flush();
        }

        String reportPath = System.getProperty("user.dir") + File.separator + "reports" + File.separator + repName;
        File extentReport = new File(reportPath);

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(extentReport.toURI());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}