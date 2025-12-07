package tests;

import java.time.LocalDateTime;

import org.testng.Assert;
import org.testng.annotations.Test;

import pages.DynamicContentPage;

public class DynamicContentChangeTest extends BaseTest {

    @Test
    public void testDynamicContentLoading() {
        DynamicContentPage page = new DynamicContentPage(driver);
        page.openPage();

        String beforePath = "screens/test3_/before_" + dtf.format(LocalDateTime.now());
        takeScreenshot(beforePath);
        String baselineSignature = page.captureThemeSignature();

        page.openAccessibilityWidget();
        String accessibilityPanelPath = "screens/test3_/accessibility_" + dtf.format(LocalDateTime.now());
        takeScreenshot(accessibilityPanelPath);
        page.selectDarkContrastOption();
        String updatedSignature = page.waitForDarkContrast(baselineSignature);

        String afterPath = "screens/test3_/after_" + dtf.format(LocalDateTime.now());
        takeScreenshot(afterPath);

        Assert.assertNotEquals(updatedSignature, baselineSignature,
                "Dark contrast toggle did not update the page theme");
    }

}
