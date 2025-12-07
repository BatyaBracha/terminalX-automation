package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    public HomePage(WebDriver d){ super(d); }

    By cartLink = By.xpath("//a[contains(@href, 'cart')] | //*[contains(@class, 'cart')]//a");

    public void openCategoryByHref(String hrefFragment){
        By catLink = By.xpath("//a[contains(@href, '" + hrefFragment + "')]");
        click(catLink);
    }

    public void goToCart() {
        click(cartLink);
    }

}
