package pages;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

//    protected WebElement waitAndScroll(By by){
//        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(by));
//
//        ((JavascriptExecutor) driver).executeScript(
//                "arguments[0].scrollIntoView({block:'center', behavior:'instant'});",
//                el
//        );
//
//        return wait.until(ExpectedConditions.visibilityOf(el));
//    }
//

    protected void click(By by) {
        WebElement element = waitAndScroll(by);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".mask-root_active-17w")));
        } catch (TimeoutException e) {
        }

        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }


//    protected void type(By by, String text){
//        WebElement el = waitAndScroll(by);
//        el.clear();
//        el.sendKeys(text);
//    }

    protected String getText(By by){
        return waitAndScroll(by).getText();
    }

    protected void type(By by, String text) {
        waitForOverlayToDisappear();

        WebElement el = waitAndScroll(by);

        try {
            el.clear();
            el.sendKeys(text);
        } catch (Exception e) {
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].value = arguments[1];", el, text);
        }
    }

    protected WebElement waitAndScroll(By by){
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(by));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', behavior:'instant'});",
                el
        );

        return wait.until(ExpectedConditions.visibilityOf(el));
    }

    protected void waitForOverlayToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector(".overlay, [class*='modal'], [class*='backdrop']")
            ));
        } catch (TimeoutException ignore) {}
    }

}

