package org.jboss.as.quickstarts.kitchensink.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MemberRegistrationE2ETest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:8080/jboss-kitchensink/");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testSuccessfulRegistration() {
        fillRegistrationForm("John Doe", "john@example.com", "1234567890");
        submitForm();

        WebElement successMessage = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("formMsgs")));
        assertTrue(successMessage.getText().contains("Registered!"));

        WebElement memberList = driver.findElement(By.id("members"));
        assertTrue(memberList.getText().contains("John Doe"));
    }

    @Test
    public void testRegistrationWithInvalidEmail() {
        fillRegistrationForm("Jane Doe", "invalid-email", "9876543210");
        submitForm();

        WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("invalid")));
        assertTrue(errorMessage.getText().contains("Email address is not valid"));
    }

    @Test
    public void testRegistrationWithExistingEmail() {
        // First, register a member
        fillRegistrationForm("Alice Smith", "alice@example.com", "1112223333");
        submitForm();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("formMsgs")));

        // Try to register another member with the same email
        fillRegistrationForm("Bob Smith", "alice@example.com", "4445556666");
        submitForm();

        WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("invalid")));
        assertTrue(errorMessage.getText().contains("Email already exists"));
    }

    private void fillRegistrationForm(String name, String email, String phone) {
        driver.findElement(By.id("reg:name")).sendKeys(name);
        driver.findElement(By.id("reg:email")).sendKeys(email);
        driver.findElement(By.id("reg:phoneNumber")).sendKeys(phone);
    }

    private void submitForm() {
        driver.findElement(By.id("reg:register")).click();
    }
}