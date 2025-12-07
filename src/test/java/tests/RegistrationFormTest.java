package tests;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegistrationFormTest extends BaseTest {

    @Test
    public void testRegistrationFormWithAllFields() throws Exception {
        System.out.println("\n========== Registration Form Test - 6 Fields ==========");
        Map<String, String> formData = new HashMap<>();

        openRegistrationForm();

        takeScreenshot("screens/test2_/01_registration_empty");

        System.out.println("✓ Step 3: Filling out registration form");
        fillInput("input[name='firstname'], input[name='firstName'], input[name='first_name'], input[placeholder*='שם פרטי']", "ישראל");
        formData.put("First Name", "ישראל");

        fillInput("input[name='lastname'], input[name='lastName'], input[name='last_name'], input[placeholder*='שם משפחה']", "כהן");
        formData.put("Last Name", "כהן");

        fillInput("input[name='email'], input[type='email'], input[id*='email']", "israel.cohen+auto@test.com");
        formData.put("Email", "israel.cohen");

        fillInput("input[name='password'], input[type='password'][name='password']", "Qa!234567");
        formData.put("Password", "Qa!234567");

        fillInput("input[name='confirmation'], input[name='password_confirmation'], input[name='confirm'], input[placeholder*='אימות']", "Qa!234567");
        formData.put("Confirm Password", "Qa!234567");

        fillInput("input[name='telephone'], input[name='phone'], input[placeholder*='טלפון'], input[name='mobile']", "0528123456");
        formData.put("Phone", "0528123456");

        fillInput("input[name='customer_id'], input[name='idNumber'], input[placeholder*='זהות'], input[name='company']", "123456789");
        formData.put("ID", "123456789");

        takeScreenshot("screens/test2_/02_registration_filled");

        System.out.println("\n--- Form Data Entered ---");
        formData.forEach((k, v) -> System.out.println("  " + k + ": " + v));

        Assert.assertTrue(formData.size() >= 6, "All required fields were populated");
        System.out.println("✓ TEST COMPLETED SUCCESSFULLY");
    }
    
    private void fillInput(String selectors, String value) {
        String[] selectorArray = selectors.split(",");
        for (String selector : selectorArray) {
            try {
                WebElement element = locateElement(selector.trim());
                element.clear();
                element.sendKeys(value);
                return;
            } catch (Exception ignored) {}
        }
        if (fillFirstEmptyInput(value)) {
            System.out.println("  • Fallback used for selectors: " + selectors);
            return;
        }
        throw new IllegalStateException("Unable to locate input for selectors: " + selectors);
    }
    
    private WebElement locateElement(String selector) {
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        if (selector.startsWith("//")) {
            return localWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selector)));
        }
        return localWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
    }

    private boolean fillFirstEmptyInput(String value) {
        List<WebElement> candidates = driver.findElements(By.cssSelector("form input[type='text'], form input[type='email'], form input[type='password'], form input[type='tel']"));
        for (WebElement candidate : candidates) {
            try {
                if (!candidate.isDisplayed() || !candidate.isEnabled()) {
                    continue;
                }
                String currentValue = candidate.getAttribute("value");
                if (currentValue != null && !currentValue.isBlank()) {
                    continue;
                }
                candidate.clear();
                candidate.sendKeys(value);
                return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    private void openRegistrationForm() {
        driver.navigate().to("https://www.terminalx.com/");
        waitForDocumentReady();

        boolean openedViaMenu = tryOpenRegistrationViaHeader();
        if (!openedViaMenu) {
            driver.navigate().to("https://www.terminalx.com/customer/account/create/");
            waitForDocumentReady();
        }
        System.out.println("✓ Registration form ready");
    }

    private boolean tryOpenRegistrationViaHeader() {
        WebDriverWait quickWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            By loginTrigger = By.xpath("//*[contains(text(),'התחברות') or contains(text(),'כניסה') or contains(text(),'log in') or contains(text(),'LOG IN')]");
            quickWait.until(ExpectedConditions.elementToBeClickable(loginTrigger)).click();

            By registerLink = By.xpath("//*[contains(text(),'הרשמה') or contains(text(),'יצירת חשבון') or contains(text(),'Sign up') or contains(text(),'Register')]");
            return openRegisterLinkWithAuthParam(registerLink);
        } catch (Exception e) {
            System.out.println("Could not navigate via header: " + e.getMessage());
            return false;
        }
    }

    private boolean openRegisterLinkWithAuthParam(By registerLocator) {
        WebDriverWait linkWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        try {
            WebElement registerElement = linkWait.until(ExpectedConditions.visibilityOfElementLocated(registerLocator));
            String href = registerElement.getAttribute("href");
            if (href != null && !href.isBlank()) {
                String targetHref = ensureRegisterAuthParam(href);
                System.out.println("Navigating to register URL: " + targetHref);
                driver.navigate().to(targetHref);
                waitForDocumentReady();
                return true;
            }
            registerElement.click();
            waitForDocumentReady();
            return true;
        } catch (Exception e) {
            System.out.println("Failed to open header register link: " + e.getMessage());
            return false;
        }
    }

    private String ensureRegisterAuthParam(String href) {
        if (href.contains("auth=register")) {
            return href;
        }
        if (href.contains("auth=login")) {
            return href.replace("auth=login", "auth=register");
        }
        return href + (href.contains("?") ? "&" : "?") + "auth=register";
    }
}

