package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CategoryPage extends BasePage  {

    public CategoryPage(WebDriver d){ super(d); }

    private By firstProduct = By.xpath("(//a[contains(@href, '/product')] | //a[contains(@class, 'product')])[1]");

    public void openFirstProduct() {
        click(firstProduct);
    }

}