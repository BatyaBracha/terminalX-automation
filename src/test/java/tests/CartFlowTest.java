package tests;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import pages.CartPage;
import pages.CategoryPage;
import pages.ProductPage;
import utils.ExcelWriter;

public class CartFlowTest extends BaseTest {

    private static final String[] CATEGORY_URLS = {
        "https://www.terminalx.com/women",
        "https://www.terminalx.com/men",
        "https://www.terminalx.com/sports"
    };

    private static final int WOMEN_CATEGORY_INDEX = 0;
    private static final int MEN_CATEGORY_INDEX = 1;
    private static final int SPORTS_CATEGORY_INDEX = 2;
    private static final int[] CATEGORY_QUANTITIES = {1, 1, 2};
    private static final int WOMEN_PRODUCT_POSITION = 0;
    private static final int MEN_PRODUCT_POSITION = 2;
    private static final int SPORTS_PRODUCT_POSITION = 4;
    private static final String CART_URL = "https://www.terminalx.com/cart";
    private static final String EXCEL_OUTPUT = "output/cart_results.xlsx";

    @Test
    public void cartFlowWithThreeCategories() throws Exception {
        System.out.println("✓ Step 1: Homepage opened - URL: " + driver.getCurrentUrl());

        List<Map<String, String>> cartResults = new ArrayList<>();
        double totalCalculatedPrice = 0;
        totalCalculatedPrice += addWomenProductToCart(cartResults);
        totalCalculatedPrice += addMenProductToCart(cartResults);
        totalCalculatedPrice += addSportsProductToCart(cartResults);

        driver.navigate().to(CART_URL);
        waitForDocumentReady();
        CartPage cartPage = new CartPage(driver);
        takeScreenshot("screens/test1_/cart_" + dtf.format(LocalDateTime.now()));

        boolean cartValidationPassed = true;
        for (Map<String, String> row : cartResults) {
            String name = row.get("ProductName");
            int quantity = Integer.parseInt(row.get("Quantity"));
            boolean exists = cartPage.verifyItemExists(name);
            boolean qtyMatch = cartPage.verifyQuantity(name, quantity);
            if (!(exists && qtyMatch)) {
                cartValidationPassed = false;
                row.put("Status", "MISMATCH");
            }
        }

        ExcelWriter.writeCartResults(EXCEL_OUTPUT, cartResults);
        System.out.println("Excel written to: " + EXCEL_OUTPUT);

        Assert.assertEquals(cartResults.size(), CATEGORY_URLS.length, "All categories were processed");
        Assert.assertTrue(cartValidationPassed, "Cart validation succeeded for all items");
        Assert.assertTrue(totalCalculatedPrice > 0, "Total price should be greater than zero");
        System.out.println("\n✓ TEST COMPLETED SUCCESSFULLY. Total expected cart value ₪" + formatPrice(totalCalculatedPrice));
    }

    private double addWomenProductToCart(List<Map<String, String>> cartResults) throws Exception {
        return addCategoryProduct(
                "Women",
                CATEGORY_URLS[WOMEN_CATEGORY_INDEX],
                WOMEN_PRODUCT_POSITION,
                CATEGORY_QUANTITIES[WOMEN_CATEGORY_INDEX],
                "screens/test1_/women_",
                cartResults
        );
    }

    private double addMenProductToCart(List<Map<String, String>> cartResults) throws Exception {
        return addCategoryProduct(
                "Men",
                CATEGORY_URLS[MEN_CATEGORY_INDEX],
                MEN_PRODUCT_POSITION,
                CATEGORY_QUANTITIES[MEN_CATEGORY_INDEX],
                "screens/test1_/men_",
                cartResults
        );
    }

    private double addSportsProductToCart(List<Map<String, String>> cartResults) throws Exception {
        return addCategoryProduct(
                "Sports",
                CATEGORY_URLS[SPORTS_CATEGORY_INDEX],
                SPORTS_PRODUCT_POSITION,
                CATEGORY_QUANTITIES[SPORTS_CATEGORY_INDEX],
                "screens/test1_/sports_",
                cartResults
        );
    }

    private double addCategoryProduct(String label,
                                       String categoryUrl,
                                       int productPosition,
                                       int quantity,
                                       String screenshotPrefix,
                                       List<Map<String, String>> cartResults) throws Exception {
        System.out.println("\n--- Processing " + label.toUpperCase() + " product ---");
        driver.navigate().to(categoryUrl);
        waitForDocumentReady();
        String fragment = categoryUrl.replace("https://www.terminalx.com", "");
        waitForUrlContains(fragment.isEmpty() ? categoryUrl : fragment);

        CategoryPage categoryPage = new CategoryPage(driver);
        ProductPage product = categoryPage.openProductAtPosition(productPosition);

        String productName = product.getName();
        String priceStr = product.getPrice();
        double productPrice = parsePrice(priceStr);

        System.out.println("  Product Name: " + productName);
        System.out.println("  Product Price (raw): " + priceStr + " | Parsed: " + productPrice);
        System.out.println("  Quantity requested: " + quantity);

        int actualQuantity = product.addProductToCart(quantity);
        System.out.println("  Final quantity applied: " + actualQuantity);
        waitForDocumentReady();

        double rowPrice = productPrice * actualQuantity;
        String screenshotPath = screenshotPrefix + dtf.format(LocalDateTime.now());
        String storedShot = takeScreenshot(screenshotPath);

        Map<String, String> result = new HashMap<>();
        result.put("ProductName", productName);
        result.put("UnitPrice", formatPrice(productPrice));
        result.put("Quantity", String.valueOf(actualQuantity));
        result.put("RowPrice", formatPrice(rowPrice));
        result.put("Status", "ADDED");
        result.put("Screenshot", storedShot);
        cartResults.add(result);

        System.out.println("✓ " + label + " product added successfully | Row Total ₪" + formatPrice(rowPrice));
        return rowPrice;
    }

    private void waitForUrlContains(String fragment) {
        if (fragment == null || fragment.isEmpty()) {
            return;
        }
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains(fragment));
        } catch (TimeoutException ignore) {}
    }

    private double parsePrice(String price) {
        try {
            String cleaned = price.replaceAll("[^0-9.,]", "").replace(",", ".");
            if (cleaned.isEmpty()) {
                return 0;
            }
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            System.out.println("  WARNING: Could not parse price '" + price + "', defaulting to 0");
            return 0;
        }
    }

    private String formatPrice(double value) {
        return String.format("%.2f", value);
    }
}
