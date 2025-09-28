package j3s.qa.tests.steps;

import io.cucumber.java.en.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.beans.factory.annotation.Value;

public class UiStepDefs {

    private static HttpServer server;

    @Value("${tests.backendBaseUrl:http://localhost:5173}")
    private String BASE_URL;

    private static int port = 5173;

    private static final String BACKEND = System.getProperty("tests.backendBaseUrl", "http://localhost:5173");
    private WebDriver driver;
    private final List<String> createdUserIds = new ArrayList<>();
    private final Deque<String> uiCreatedUserIds = new ArrayDeque<>();

    private WebElement findFirst(By... locators) {
        for (By by : locators) {
            var els = getDriver().findElements(by);
            if (!els.isEmpty()) return els.get(0);
        }
        throw new NoSuchElementException("None of the selectors matched.");
    }

    private String waitForNonEmptyText(By locator, Duration timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return wait.until(d -> {
            String t = d.findElement(locator).getText();
            return (t != null && !t.trim().isEmpty()) ? t : null;
        });
    }

    private Optional<String> extractIdFromJson(String jsonish) {
        // Matches "id":"..."  or  "id":123
        Pattern p = Pattern.compile("\"id\"\\s*:\\s*\"?([^\"]+?)\"?(?:[,}\\]])");
        Matcher m = p.matcher(jsonish);
        return m.find() ? Optional.of(m.group(1)) : Optional.empty();
    }

    private By userName()     {
        return By.cssSelector("[data-testid='user-name']");
    }
    private By userEmail()    { return By.cssSelector("[data-testid='user-email']"); }
    private By userType()     { return By.cssSelector("[data-testid='user-accountType']"); }
    private By userRegister() { return By.cssSelector("[data-testid='btn-create-user']"); }
    private By userResult()   { return By.cssSelector("[data-testid='user-result']"); }

    private By txUserId()     { return By.cssSelector("[data-testid='tx-userId']"); }
    private By txRecipientId(){ return By.cssSelector("[data-testid='tx-recipientId']"); }
    private By txAmount()     { return By.cssSelector("[data-testid='tx-amount']"); }
    private By txSend()       { return By.cssSelector("[data-testid='btn-create-tx']"); }
    private By txResult()     { return By.cssSelector("[data-testid='tx-result']"); }

    // Fallbacks if your page uses element IDs instead of data-testid
    private By userNameFallback()     { return By.id("name"); }
    private By userEmailFallback()    { return By.id("email"); }
    private By userTypeFallback()     { return By.id("accountType"); }
    private By userRegisterFallback() { return By.cssSelector("button[type='submit']"); }
    private By userResultFallback()   { return By.id("userResult"); }

    private By txUserIdFallback()     { return By.id("userId"); }
    private By txRecipientIdFallback(){ return By.id("recipientId"); }
    private By txAmountFallback()     { return By.id("amount"); }
    private By txSendFallback()       { return By.cssSelector("button[type='submit']"); }
    private By txResultFallback()     { return By.id("txResult"); }

    @Given("the mock UI server is running")
    public void mock_ui_running() throws IOException {
        if (server == null) {
            server = HttpServer.create(new InetSocketAddress(URI.create(BASE_URL).getPort()), 0);
            var exec = Executors.newCachedThreadPool();
            server.setExecutor(exec);

            // Serve / and /index.html from src/test/resources/static
            Path root = Path.of("src/test/resources/static").toAbsolutePath();
            server.createContext("/", (HttpExchange ex) -> {
                try {
                    String path = ex.getRequestURI().getPath();
                    if (path.equals("/")) path = "/index.html";
                    Path file = root.resolve(path.substring(1)).normalize();
                    if (!file.startsWith(root) || !Files.exists(file)) {
                        ex.sendResponseHeaders(404, -1);
                        return;
                    }
                    byte[] bytes = Files.readAllBytes(file);
                    ex.getResponseHeaders().add("Content-Type", path.endsWith(".html") ? "text/html" : "text/plain");
                    ex.sendResponseHeaders(200, bytes.length);
                    ex.getResponseBody().write(bytes);
                } finally {
                    ex.close();
                }
            });
            server.start();
        }
    }

    @Given("the backend base URL is set for UI")
    public void backend_url_set() {
        RestAssured.baseURI = BACKEND;
    }

    private WebDriver getDriver() {
        if (driver == null) {
            System.out.println("************getting driver....");
            WebDriverManager.chromedriver().setup();
            ChromeOptions opts = new ChromeOptions();
            opts.addArguments("--headless=new", "--no-sandbox", "--disable-gpu");
            driver = new ChromeDriver(opts);
        }
        return driver;
    }

    @When("I open the mock UI")
    public void open_mock_ui() {
        getDriver().get(BASE_URL + "/index.html");
    }

    @When("I register user {string} with {string} as {string}")
    public void register_user(String name, String email, String type) {
        WebDriver d = getDriver();
        d.findElement(By.cssSelector("[data-testid='user-name']")).clear();
        d.findElement(By.cssSelector("[data-testid='user-name']")).sendKeys(name);
        d.findElement(By.cssSelector("[data-testid='user-email']")).clear();
        d.findElement(By.cssSelector("[data-testid='user-email']")).sendKeys(email);
        d.findElement(By.cssSelector("[data-testid='user-accountType']")).sendKeys(type);
        d.findElement(By.cssSelector("[data-testid='btn-create-user']")).click();
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    @Then("I should see a success message containing {string}")
    public void see_success_msg(String text) {
        WebElement el = getDriver().findElement(By.cssSelector("[data-testid='user-result']"));
        Assertions.assertTrue(el.getText().contains(text), "Expected success text not found");
    }

    @When("I try to register a user {string} with {string} as {string}")
    public void i_try_to_register_a_user_with_as(String name, String email, String type) {
        WebDriver d = getDriver();
        d.findElement(By.cssSelector("[data-testid='user-name']")).clear();
        d.findElement(By.cssSelector("[data-testid='user-name']")).sendKeys(name);
        d.findElement(By.cssSelector("[data-testid='user-email']")).clear();
        d.findElement(By.cssSelector("[data-testid='user-email']")).sendKeys(email);
        d.findElement(By.cssSelector("[data-testid='user-accountType']")).sendKeys(type);
        d.findElement(By.cssSelector("[data-testid='btn-create-user']")).click();
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    @Then("I should see a status message containing {string}")
    public void i_should_see_a_status_message_containing(String msgExpected) {
        verifyStatusMessage(msgExpected);
    }

    @Given("I ensure users exist for a transfer")
    public void i_ensure_users_exist_for_a_transfer() {
        int needed = Math.max(0, 2 - uiCreatedUserIds.size());
        for (int i = 0; i < needed; i++) {
            String name = (i == 0) ? "Alice" : "Bob";
            String email = (i == 0) ? ("alice+" + System.currentTimeMillis() + "@example.com")
                    : ("bob+"   + System.currentTimeMillis() + "@example.com");
            String type = "standard";
            register_user(name, email, type);
            String newUserId = verifyUserExistsAndGetId(email);
            System.out.println("****** newUserId: " + newUserId);
            uiCreatedUserIds.add(newUserId);
        }

        ///html/body/div[1]/div/section[2]/div[1]/table/tbody/tr[2]/td[2]
        ///html/body/div[1]/div/section[2]/div[1]/table
        //Just ensure we have created two users
        Assertions.assertTrue(uiCreatedUserIds.size() >= 2, "Failed to provision two users via UI.");
    }

    @When("I submit a transfer of {double} between the last two created users")
    public void i_submit_a_transfer_of_between_the_last_two_created_users(Double amount) {
        Assertions.assertTrue(uiCreatedUserIds.size() >= 2,
                "Need at least two users created via UI before transferring.");
        String from = uiCreatedUserIds.pop();
        String to   = uiCreatedUserIds.pop();
        transferAmount(from, to, amount);
    }

    @Then("I submit a transfer to a invalid recipient")
    public void i_submit_a_transfer_to_a_invalid_recipient(){
        transferAmount("invalidUser123", "101", 1.0);
    }

    @Then("I should see the respective transaction rows")
    public void i_should_see_the_respective_transaction_rows() {
        //TODO: Identify the actual transaction rows and check - Jai
        List<WebElement> rows = getDriver().findElements(
                By.xpath("/html/body/div[1]/div/section[2]/div[2]/table/tbody/tr")
        );

        System.out.println("*******Rows count: " + rows.size());
        Assertions.assertTrue(rows.size() >= 2, "Failed to provision two users via UI.");
    }

    private String verifyUserExistsAndGetId(String userName){
        List<WebElement> rows = driver.findElements(
                By.xpath("/html/body/div[1]/div/section[2]/div[1]/table/tbody/tr")
        );

        boolean found = false;
        String userId = null;
        for (WebElement row : rows) {
            System.out.println("****user '" + userName + "' in row: " + row.getText());
            if (row.getText().contains(userName)) {
                System.out.println("Found user '" + userName + "' in row: " + row.getText());
                found = true;
                userId = row.getText().split("\\s+")[0];
                break;
            }
        }

        if (!found) {
            System.out.println("User '" + userName + "' not found in table.");
            Assertions.fail("User '" + userName + "' not found in table.");
        }
        return userId;
    }

    private void transferAmount(String sender, String recipient, Double amount){
        WebDriver d = getDriver();
        // Fill transfer form (with fallbacks)
        findFirst(txUserId(), txUserIdFallback()).clear();
        findFirst(txUserId(), txUserIdFallback()).sendKeys(sender);

        findFirst(txRecipientId(), txRecipientIdFallback()).clear();
        findFirst(txRecipientId(), txRecipientIdFallback()).sendKeys(recipient);

        findFirst(txAmount(), txAmountFallback()).clear();
        findFirst(txAmount(), txAmountFallback()).sendKeys(String.valueOf(amount));

        // Type “transfer” if your UI requires it; otherwise omit
        if (!getDriver().findElements(By.cssSelector("[data-testid='tx-type']")).isEmpty()) {
            WebElement typeEl = getDriver().findElement(By.cssSelector("[data-testid='tx-type']"));
            typeEl.clear();
            typeEl.sendKeys("transfer");
        }

        findFirst(txSend(), txSendFallback()).click();
    }

    private void verifyStatusMessage(String msgExpected) {
        WebElement el = getDriver().findElement(By.cssSelector("[data-testid='status-message']"));
        String msg = el.getText();
        //if its controlled in ui, then ignore by msg
        Assertions.assertTrue(msg.toLowerCase().contains(msgExpected.toLowerCase()),
                "Expected status to contain '" + msgExpected + "', but got: " + msg);
    }
}
