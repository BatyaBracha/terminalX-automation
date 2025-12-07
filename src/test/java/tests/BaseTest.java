package tests;

import java.io.File;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    protected WebDriver driver;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        
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

        driver.get("https://www.terminalx.com/");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterMethod
    public void cleanUp() {
        try {
            driver.manage().deleteAllCookies();
        } catch (Exception e) {
            // Ignore if window is closed
        }
    }

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public void takeScreenshot(String name) {
        try {
            File dir = new File("screens");
            if (!dir.exists()) dir.mkdirs();
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(dir, name + ".png");
            FileUtils.copyFile(scrFile, destFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
