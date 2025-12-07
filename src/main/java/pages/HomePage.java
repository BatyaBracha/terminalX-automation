package pages;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage extends BasePage {

    public HomePage(WebDriver d){ super(d); }

    private final By cartLink = By.xpath("//a[contains(@href, 'cart')] | //*[contains(@class, 'cart')]//a | //button[contains(@class, 'cart')]");
    private final By navCategories = By.xpath("//nav//a | //header//a[@href] | //ul//li//a[@href]");

    /**
     * Opens a category from the top navigation menu
     * @param categoryName - Category name (e.g., "WOMEN", "MEN", "SPORT")
     */
    public void openCategory(String categoryName){
        By categoryLink = By.xpath("//nav//a[contains(translate(., 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), '" + categoryName.toUpperCase() + "')] | //header//a[contains(text(), '" + categoryName + "')]");
        click(categoryLink);
    }

    /**
     * Opens the first available navigation category
     */
    public void openFirstCategory(){
        List<WebElement> categories = driver.findElements(navCategories);
        for(WebElement cat : categories){
            String text = cat.getText().trim();
            String href = cat.getAttribute("href");
            // Skip empty, cart, and non-product links
            if(!text.isEmpty() && !text.toLowerCase().contains("cart") && !text.toLowerCase().contains("עגלה") && href != null && !href.isEmpty()){
                cat.click();
                return;
            }
        }
    }

    public void openCategoryByHref(String hrefFragment){
        By catLink = By.xpath("//a[contains(@href, '" + hrefFragment + "')]");
        click(catLink);
    }

    public void goToCart() {
        click(cartLink);
    }

}
