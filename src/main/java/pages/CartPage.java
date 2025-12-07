package pages;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends BasePage {
    public CartPage(WebDriver d){ super(d); }

    private final By subtotal = By.xpath("//span[contains(@class, 'total')] | //div[contains(@class, 'total')] | //*[@class='subtotal']");
    private final By cartQuantity = By.xpath("//span[contains(text(), '(')] | //*[contains(@class, 'count')] | //span[contains(@class, 'quantity')]");
    private final By cartItems = By.xpath("//div[contains(@class, 'cart-item')] | //tr[@class='cart-item'] | //li[contains(@class, 'item')] | //div[@class='product-row']");
    private final By itemName = By.xpath(".//span[@class='item-name'] | .//a[@class='product-name'] | .//h3 | .//a[contains(@class, 'name')]");
    private final By itemQuantity = By.xpath(".//input[@name='quantity'] | .//span[@class='quantity'] | .//span[contains(@class, 'qty')]");
    private final By itemPrice = By.xpath(".//span[@class='item-price'] | .//span[@class='price'] | .//span[contains(@class, 'price')]");

    /**
     * Gets all cart items as a list of WebElements
     * @return List of cart item elements
     */
    public List<WebElement> getCartItems() {
        return driver.findElements(cartItems);
    }

    /**
     * Gets the name of a cart item
     * @param product - WebElement representing the product row
     * @return Product name
     */
    public String getItemName(WebElement product) {
        try {
            WebElement nameElement = product.findElement(itemName);
            return nameElement.getText().trim();
        } catch (Exception e) {
            System.out.println("Error getting item name: " + e.getMessage());
            return "";
        }
    }

    /**
     * Gets the quantity of a cart item
     * @param product - WebElement representing the product row
     * @return Quantity as integer
     */
    public int getItemQuantity(WebElement product) {
        try {
            WebElement quantityElement = product.findElement(itemQuantity);
            String quantityText = quantityElement.getAttribute("value");
            if (quantityText == null || quantityText.isEmpty()) {
                quantityText = quantityElement.getText();
            }
            return Integer.parseInt(quantityText.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            System.out.println("Error getting item quantity: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Gets the price of a cart item
     * @param product - WebElement representing the product row
     * @return Price as double
     */
    public double getItemPrice(WebElement product) {
        try {
            WebElement priceElement = product.findElement(itemPrice);
            String priceText = priceElement.getText();
            String cleaned = priceText.replaceAll("[^0-9.,]", "").replace(",", ".");
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            System.out.println("Error getting item price: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Verifies that an item with a specific name exists in the cart
     * @param name - Product name to search for
     * @return true if item exists, false otherwise
     */
    public boolean verifyItemExists(String name) {
        List<WebElement> items = getCartItems();
        for (WebElement item : items) {
            String itemName = getItemName(item);
            if (itemName.contains(name) || name.contains(itemName)) {
                System.out.println("✓ Item found in cart: " + itemName);
                return true;
            }
        }
        System.out.println("✗ Item NOT found in cart: " + name);
        return false;
    }

    /**
     * Verifies that an item has the expected quantity
     * @param name - Product name to search for
     * @param expectedQuantity - Expected quantity value
     * @return true if quantity matches, false otherwise
     */
    public boolean verifyQuantity(String name, int expectedQuantity) {
        List<WebElement> items = getCartItems();
        for (WebElement item : items) {
            String itemName = getItemName(item);
            if (itemName.contains(name) || name.contains(itemName)) {
                int actualQuantity = getItemQuantity(item);
                if (actualQuantity == expectedQuantity) {
                    System.out.println("✓ Quantity verified for " + itemName + ": " + actualQuantity);
                    return true;
                } else {
                    System.out.println("✗ Quantity mismatch for " + itemName + ". Expected: " + expectedQuantity + ", Actual: " + actualQuantity);
                    return false;
                }
            }
        }
        System.out.println("✗ Item not found for quantity verification: " + name);
        return false;
    }

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

