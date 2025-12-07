package tests;

import java.io.File;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    protected WebDriver driver;

    protected WebDriverWait wait;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        // הוסף proxy אם קיים
        String proxyHost = System.getenv("PROXY_HOST");
        String proxyPort = System.getenv("PROXY_PORT");
        if (proxyHost != null && proxyPort != null) {
            options.addArguments("--proxy-server=http://" + proxyHost + ":" + proxyPort);
        }
        
        // הוסף אפשרויות נוספות לטיפול עם חסימות
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--enable-automation=false");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("https://www.terminalx.com/");
        waitForDocumentReady();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            try {
                driver.manage().deleteAllCookies();
            } catch (Exception ignore) {}
            try {
                driver.quit();
            } catch (Exception e) {
                // Ignore exceptions during quit
            }
            driver = null;
        }
    }

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public String takeScreenshot(String name) {
        try {
            String normalized = name.endsWith(".png") ? name : name + ".png";
            File destFile = new File(normalized);
            File parent = destFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, destFile);
            return destFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    protected void waitForDocumentReady() {
        if (driver == null) {
            return;
        }
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
                try {
                    return "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState"));
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception ignore) {}
    }

    protected void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
