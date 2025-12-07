package tests;

import java.time.LocalDateTime;

import org.testng.Assert;
import org.testng.annotations.Test;

import pages.DynamicContentPage;

public class DynamicContentChangeTest extends BaseTest {

    @Test
    public void testSpecialOfferNavigation() {
        DynamicContentPage page = new DynamicContentPage(driver);

        String screen1 = "screens/test2_/step1_" + dtf.format(LocalDateTime.now()) + ".png";
        takeScreenshot(screen1);

        page.clickSpecialButton();

        page.waitForPageTitle();

        String pageTitle = page.getPageTitleText();
        Assert.assertFalse(pageTitle.isEmpty(), "העמוד לא הציג כותרת לאחר לחיצה על הכפתור");

        String screen2 = "screens/test2_/step2_" + dtf.format(LocalDateTime.now()) + ".png";
        takeScreenshot(screen2);
    }

}
