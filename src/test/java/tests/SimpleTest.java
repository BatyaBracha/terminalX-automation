package tests;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class SimpleTest extends BaseTest {
    @Test
    public void simpleScreenshot() throws Exception {
        System.out.println("✓ Test started");
        System.out.println("✓ Browser is: " + driver.getClass().getSimpleName());
        System.out.println("✓ Current URL: " + driver.getCurrentUrl());
        
        // Navigate to homepage
        driver.navigate().to("https://www.terminalx.com/");
        Thread.sleep(3000);
        System.out.println("✓ Navigated to homepage");
        System.out.println("✓ Current URL: " + driver.getCurrentUrl());
        
        takeScreenshot("screens/test1_/homepage.png");
        System.out.println("✓ Screenshot taken");
        
        // List all links on the page
        System.out.println("\n=== Links on page ===");
        List<String> linkData = new ArrayList<>();
        try {
            var links = driver.findElements(org.openqa.selenium.By.tagName("a"));
            for (var link : links) {
                try {
                    String text = link.getText();
                    String href = link.getAttribute("href");
                    if (!text.isEmpty() && !text.toLowerCase().contains("cart") && href != null && !href.isEmpty()) {
                        linkData.add(text + " -> " + href);
                        if (linkData.size() >= 10) break;
                    }
                } catch (Exception e) {
                    // Skip this link if it fails
                }
            }
            
            for (String link : linkData) {
                System.out.println("  " + link);
            }
        } catch (Exception e) {
            System.out.println("  (Could not retrieve links: " + e.getMessage() + ")");
        }
        
        System.out.println("✓ Test passed");
    }
}


