package j3s.qa.tests.hooks;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestHooks {
    @BeforeAll
    public static void beforeAll() {
        // Placeholder if global setup is needed
    }

    @AfterAll
    public static void afterAll() {
        // Close any lingering ChromeDrivers
        try {
            for (WebDriver d : new WebDriver[] {}) {
                if (d != null) d.quit();
            }
        } catch (Exception ignored) {}
    }
}