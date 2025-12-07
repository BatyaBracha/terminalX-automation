package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {
    public CartPage(WebDriver d){ super(d); }

    private By subtotal = By.xpath("//span[contains(@class, 'total')] | //div[contains(@class, 'total')] | //*[@class='subtotal']");
    private By cartQuantity = By.xpath("//span[contains(text(), '(')] | //*[contains(@class, 'count')] | //span[contains(@class, 'quantity')]");

    public int getCartQuantity() {
        String text = driver.findElement(cartQuantity).getText();
        // חיפוש המספר בתוך הסוגריים או ישירות
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\((\\d+)\\)|^(\\d+)$").matcher(text);
        if (m.find()) {
            return Integer.parseInt(m.group(1) != null ? m.group(1) : m.group(2));
        } else {
            return 0;
        }
    }

    public double getSubtotal(){
        String s = getText(subtotal)
                .replaceAll("[^0-9.,]", "")
                .replace(",", ".");
        return Double.parseDouble(s);
    }

}

