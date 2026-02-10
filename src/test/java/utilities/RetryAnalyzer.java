package utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempt = 0;

    // You can also read this from Config.properties later
    private static final int MAX_RETRY = 2;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < MAX_RETRY) {
            attempt++;
            return true; // tells TestNG to rerun the failed test
        }
        return false;
    }
}