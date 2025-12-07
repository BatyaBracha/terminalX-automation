package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DynamicContentPage extends BasePage {

    public DynamicContentPage(WebDriver driver) {
        super(driver);
    }

    private final By specialButton = By.xpath("//button[contains(@class, 'promo') or contains(@class, 'banner') or contains(@class, 'special')] | //button[contains(., 'מבצע')]");
    private final By pageTitle = By.xpath("//h1 | //h2");

    public void clickSpecialButton() {
        click(specialButton);
    }

    public void waitForPageTitle() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
    }

    public String getPageTitleText() {
        return driver.findElement(pageTitle).getText().trim();
    }
}
