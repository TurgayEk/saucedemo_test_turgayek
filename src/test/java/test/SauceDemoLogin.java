package test;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
public class SauceDemoLogin {
    private static final String BASE_URL      = "https://www.saucedemo.com/";
    private static final String INVENTORY_URL = "https://www.saucedemo.com/inventory.html";

    private static final String VALID_USER  = "standard_user";
    private static final String VALID_PASS  = "secret_sauce";
    private static final String WRONG_USER  = "fel_anvandare";
    private static final String WRONG_PASS  = "fel_losenord";

    private static final String ERROR_SELECTOR = "[data-test='error']";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BASE_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String username, String password) {
        driver.findElement(By.id("user-name")).clear();
        driver.findElement(By.id("user-name")).sendKeys(username);

        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);

        driver.findElement(By.id("login-button")).click();
    }


    @Nested
    @DisplayName("Test 1- Lyckad inloggning")
    class SuccessfulLogin {

        @Test
        @DisplayName("Användaren går till startsidan (inventory)")
        void userIsRedirectedToInventory() {

            login(VALID_USER, VALID_PASS);


            wait.until(ExpectedConditions.urlToBe(INVENTORY_URL));

            assertEquals(INVENTORY_URL, driver.getCurrentUrl(),
                    "Användaren går inte till inventory-sidan.");
        }

        @Test
        @DisplayName("Rubriken 'Products' visas på startsidan")
        void inventoryPageTitleIsVisible() {

            login(VALID_USER, VALID_PASS);


            WebElement heading = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.className("title"))
            );

            assertEquals("Products", heading.getText(),
                    "Förväntad rubrik 'Products' hittades inte.");
        }
    }


    @Nested
    @DisplayName(" Test 2-Fel användarnamn")
    class WrongUsername {

        @Test
        @DisplayName("Felmeddelande visas")
        void errorMessageIsDisplayed() {

            login(WRONG_USER, VALID_PASS);


            WebElement error = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ERROR_SELECTOR))
            );

            assertTrue(error.isDisplayed(), "Felmeddelandet visas inte.");
        }

        @Test
        @DisplayName("Felmeddelandet innehåller rätt text")
        void errorMessageContainsExpectedText() {
            login(WRONG_USER, VALID_PASS);

            WebElement error = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ERROR_SELECTOR))
            );

            assertTrue(error.getText().contains("Username and password do not match"),
                    "Felmeddelandet innehöll inte förväntad text. Faktiskt: " + error.getText());
        }

        @Test
        @DisplayName("Användaren stannar kvar på inloggningssidan")
        void userRemainsOnLoginPage() {
            login(WRONG_USER, VALID_PASS);


            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ERROR_SELECTOR)));

            assertEquals(BASE_URL, driver.getCurrentUrl(),
                    "Användaren omdirigerades trots felaktiga uppgifter.");
        }
    }


    @Nested
    @DisplayName("Test 3- Fel lösenord")
    class WrongPassword {

        @Test
        @DisplayName("Felmeddelande visas")
        void errorMessageIsDisplayed() {
            // GIVET att användaren anger ett felaktigt lösenord
            login(VALID_USER, WRONG_PASS);


            WebElement error = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ERROR_SELECTOR))
            );

            assertTrue(error.isDisplayed(), "Felmeddelandet visas inte.");
        }

        @Test
        @DisplayName("Felmeddelandet innehåller rätt text")
        void errorMessageContainsExpectedText() {
            login(VALID_USER, WRONG_PASS);

            WebElement error = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ERROR_SELECTOR))
            );

            assertTrue(error.getText().contains("Username and password do not match"),
                    "Felmeddelandet innehöll inte förväntad text. Faktiskt: " + error.getText());
        }

        @Test
        @DisplayName("Användaren stannar kvar på inloggningssidan")
        void userRemainsOnLoginPage() {
            login(VALID_USER, WRONG_PASS);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ERROR_SELECTOR)));

            assertEquals(BASE_URL, driver.getCurrentUrl(),
                    "Användaren omdirigerades trots felaktiga uppgifter.");
        }
}
}