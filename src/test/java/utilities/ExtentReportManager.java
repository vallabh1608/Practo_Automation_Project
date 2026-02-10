package utilities;

import org.testng.ITestContext;
import org.testng.ITestListener;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Minimal TestNG listener shell.
 * If you use ExtentReports, keep your existing onTestStart/onTestSuccess/onTestFailure/etc.
 * Only onFinish() is shown here with CI-safe behavior.
 */
public class ExtentReportManager implements ITestListener {

    // If you already build the report elsewhere, point to it here.
    // For demo, we generate a timestamped file name under ./reports
    private static final String REPORTS_DIR = "reports";
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
    private static Path reportFile;

    @Override
    public void onStart(ITestContext context) {
        try {
            Files.createDirectories(Path.of(REPORTS_DIR));
            // If your Extent code creates the file, set reportFile to that exact file instead
            String ts = LocalDateTime.now().format(TS);
            reportFile = Path.of(REPORTS_DIR, "Test-Report-" + ts + ".html");

            // If you use ExtentReports, initialize it here and write to 'reportFile'
            // e.g., ExtentReports extent = new ExtentReports();
            // ExtentSparkReporter spark = new ExtentSparkReporter(reportFile.toFile());
            // extent.attachReporter(spark);
            // Store 'extent' in a static holder and flush in onFinish()

        } catch (Exception e) {
            System.err.println("[Extent] Could not prepare report file: " + e.getMessage());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        try {
            // If using ExtentReports, be sure to flush here:
            // ExtentHolder.get().flush();

            if (reportFile == null) {
                System.out.println("[Extent] Report path not initialized.");
                return;
            }

            boolean isCI = System.getenv("JENKINS_URL") != null || Boolean.getBoolean("ci");
            if (!isCI && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(reportFile.toUri()); // local dev only
            } else {
                System.out.println("[Extent] CI mode: report at " + reportFile.toAbsolutePath());
            }
        } catch (Exception e) {
            // Never fail build because a desktop browser isn't available on CI
            System.err.println("[Extent] Skipping opening report: " + e.getMessage());
        }
    }

    // Implement other ITestListener callbacks if you need them for Extent logging
    // onTestStart, onTestSuccess, onTestFailure, etc.
}
