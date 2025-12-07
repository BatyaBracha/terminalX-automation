package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductPage extends BasePage {
    public ProductPage(WebDriver d){ super(d); }
    private By addToCartButton = By.xpath("//button[contains(., 'הוסף') or contains(., 'Add')] | //button[contains(@class, 'add') or contains(@class, 'cart')]");
    private By quantityInput = By.xpath("//input[@name='quantity' or contains(@class, 'quantity')] | //input[@type='number']");
    private By buyNowButton = By.xpath("//button[contains(., 'קנה') or contains(., 'Buy')] | //button[contains(@class, 'buy') or contains(@class, 'checkout')]");
    private By productName = By.xpath("//h1 | //h2[1] | //*[@class='product-title']");
    private By productPriceDiv = By.xpath("//span[contains(@class, 'price')] | //*[contains(text(), '₪')] | //div[contains(@class, 'price')]//span");

    public String getPrice() {
        String raw = getText(productPriceDiv);
        String cleaned = raw.replaceAll("[^0-9.,]", "").replace(",", ".");
        return cleaned;
    }

    public String getName(){ return getText(productName); }

    public void setQuantity(int q) {
        WebElement input = driver.findElement(quantityInput);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", input, q);
    }

    public void addToCart() {
        click(addToCartButton);
    }

    public void buyNow() {
        click(buyNowButton);
    }

    public void waitForProductPageReady() {
        WebElement name = wait.until(
                ExpectedConditions.visibilityOfElementLocated(productName)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                name
        );
    }
}
