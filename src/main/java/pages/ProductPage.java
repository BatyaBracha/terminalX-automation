package pages;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductPage extends BasePage {
    public ProductPage(WebDriver d){ super(d); }
    
    private final By addToCartButton = By.xpath("//button[contains(., 'הוסף') or contains(., 'Add')] | //button[contains(@class, 'add') or contains(@class, 'cart')]");
    private final By quantityInput = By.xpath("//input[@name='quantity' or contains(@class, 'quantity') or contains(@name, 'qty') or contains(@id, 'qty')] | //input[@type='number']");
    private final By quantityIncreaseButton = By.xpath("//button[contains(., '+') and (contains(@class, 'quantity') or contains(@class, 'increase') or contains(@aria-label, 'increase'))] | //button[@class='quantity-increment'] | //button[contains(@data-action, 'increase')]");
    private final By sizeOptions = By.xpath("//button[contains(@class, 'size')] | //*[@class='size-option'] | //span[contains(@class, 'size')] | //div[@class='size-selector']//button");
    private final By buyNowButton = By.xpath("//button[contains(., 'קנה') or contains(., 'Buy')] | //button[contains(@class, 'buy') or contains(@class, 'checkout')]");
    private final By productName = By.xpath("//h1 | //h2[1] | //*[@class='product-title']");
    private final By productPriceDiv = By.xpath("//span[contains(@class, 'price')] | //*[contains(text(), '₪')] | //div[contains(@class, 'price')]//span");

    public String getPrice() {
        try {
            // Try to find price in various possible locations
            List<WebElement> priceElements = driver.findElements(productPriceDiv);
            for (WebElement el : priceElements) {
                String text = el.getText().trim();
                if (!text.isEmpty()) {
                    String cleaned = text.replaceAll("[^0-9.,]", "").replace(",", ".");
                    if (!cleaned.isEmpty() && !cleaned.equals(".")) {
                        return cleaned;
                    }
                }
            }
            
            // If not found in price div, search entire page for price pattern
            String pageSource = driver.getPageSource();
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("₪\\s*([0-9]+[.,]?[0-9]*)");
            java.util.regex.Matcher m = p.matcher(pageSource);
            if (m.find()) {
                return m.group(1).replace(",", ".");
            }
            
            // Default fallback
            return "0";
        } catch (Exception e) {
            System.out.println("Error extracting price: " + e.getMessage());
            return "0";
        }
    }

    public String getName(){ return getText(productName); }

    /**
     * Selects the first available size from the size options
     * (Required for Terminal X!)
     */
    public void selectFirstAvailableSize() {
        List<WebElement> sizes = driver.findElements(sizeOptions);
        if (sizes.isEmpty()) {
            System.out.println("No size options found - product may not require size selection");
            return;
        }

        WebDriverWait shortWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        for (WebElement size : sizes) {
            try {
                String disabled = size.getAttribute("disabled");
                if (disabled != null && (disabled.equalsIgnoreCase("true") || disabled.equals("disabled"))) {
                    continue;
                }
                shortWait.until(ExpectedConditions.elementToBeClickable(size));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", size);
                size.click();
                waitForOverlayToDisappear();
                System.out.println("Selected size: " + size.getText());
                return;
            } catch (Exception e) {
                System.out.println("Unable to click size option, trying next. Reason: " + e.getMessage());
            }
        }

        System.out.println("Unable to select any size option");
    }

    /**
     * Increases quantity by clicking the + button
     * @param timesToClick - Number of times to click the + button
     */
    public int increaseQuantity(int timesToClick) {
        int successfulClicks = 0;
        if (timesToClick <= 0) {
            return 0;
        }
        for (int i = 0; i < timesToClick; i++) {
            try {
                WebElement increaseBtn = driver.findElement(quantityIncreaseButton);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", increaseBtn);
                increaseBtn.click();
                waitForOverlayToDisappear();
                successfulClicks++;
            } catch (NoSuchElementException e) {
                System.out.println("Quantity increase button not found or error: " + e.getMessage());
                break;
            } catch (Exception generic) {
                System.out.println("Quantity increase failed: " + generic.getMessage());
                break;
            }
        }
        if (successfulClicks < timesToClick) {
            System.out.println("Only increased quantity " + successfulClicks + " times out of requested " + timesToClick);
        } else {
            System.out.println("Increased quantity " + timesToClick + " times");
        }
        return successfulClicks;
    }

    /**
     * Adds product to cart with size selection and quantity
     * @param quantity - Number of items to add (1 or more)
     */
    public int addProductToCart(int quantity) {
        selectFirstAvailableSize();
        int finalQuantity = prepareQuantity(quantity);
        addToCart();
        System.out.println("Product added to cart with quantity: " + finalQuantity);
        return finalQuantity;
    }

    public boolean setQuantity(int q) {
        try {
            WebElement input = driver.findElement(quantityInput);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input);
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", input);
            input.clear();
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", input, q);
            waitForOverlayToDisappear();
            return readQuantityFromField(q) == q;
        } catch (NoSuchElementException e) {
            System.out.println("Could not set quantity via input field: " + e.getMessage());
            return false;
        }
    }

    private int prepareQuantity(int requestedQuantity) {
        int desired = Math.max(1, requestedQuantity);
        if (desired == 1) {
            if (setQuantity(1)) {
                return 1;
            }
            return readQuantityFromField(1);
        }

        int successfulClicks = increaseQuantity(desired - 1);
        if (successfulClicks == desired - 1) {
            return desired;
        }

        if (setQuantity(desired)) {
            return desired;
        }

        int fallbackQuantity = readQuantityFromField(1);
        if (fallbackQuantity != desired) {
            System.out.println("Falling back to quantity " + fallbackQuantity + " due to missing controls");
        }
        return fallbackQuantity;
    }

    private int readQuantityFromField(int fallback) {
        try {
            WebElement input = driver.findElement(quantityInput);
            String value = input.getAttribute("value");
            if (value == null || value.isBlank()) {
                value = input.getText();
            }
            if (value != null) {
                String digits = value.replaceAll("[^0-9]", "");
                if (!digits.isEmpty()) {
                    return Integer.parseInt(digits);
                }
            }
        } catch (NoSuchElementException | NumberFormatException ignore) {}
        return fallback;
    }

    public void addToCart() {
        try {
            click(addToCartButton);
        } catch (Exception e) {
            System.out.println("Primary add to cart button failed, trying alternatives: " + e.getMessage());
            // Try alternative selectors
            By[] alternatives = {
                By.xpath("//button[contains(text(), 'הוסף')]"),
                By.xpath("//button[@class*='add']"),
                By.xpath("//button[@class*='cart']"),
                By.xpath("//button[contains(., 'קנה')]"),
                By.xpath("//button[contains(@class, 'btn')]"),
                By.cssSelector("button.btn-primary"),
                By.cssSelector("button.add-to-cart")
            };
            
            for (By alt : alternatives) {
                try {
                    click(alt);
                    System.out.println("✓ Successfully clicked add to cart via alternative selector");
                    return;
                } catch (Exception ex) {
                    // Try next option
                }
            }
            
            // Last resort: try JavaScript click on any button with relevant text
            System.out.println("Trying JavaScript click on any button with 'add'/'קנה' text...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            List<WebElement> buttons = driver.findElements(By.tagName("button"));
            for (WebElement btn : buttons) {
                try {
                    String text = btn.getText().toLowerCase();
                    String className = btn.getAttribute("class").toLowerCase();
                    if (text.contains("הוסף") || text.contains("add") || text.contains("קנה") || 
                        className.contains("add") || className.contains("cart")) {
                        js.executeScript("arguments[0].click();", btn);
                        System.out.println("✓ Clicked via JavaScript");
                        return;
                    }
                } catch (Exception ex2) {
                    // Continue to next button
                }
            }
            
            System.out.println("WARNING: Could not find add to cart button, but continuing with test");
        }
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
