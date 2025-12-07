package tests;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import pages.CartPage;
import pages.CategoryPage;
import pages.HomePage;
import pages.ProductPage;
import utils.ExcelWriter;

public class CartFlowTest extends BaseTest {

    @Test
    public void addThreeItemsAndVerifyCart() throws Exception {
        HomePage home = new HomePage(driver);
        CategoryPage category = new CategoryPage(driver);
        CartPage cart = new CartPage(driver);

        List<Map<String, String>> results = new ArrayList<>();

        // =====================
        // פריט 1: קטגוריה A
        home.openCategoryByHref("/computers");
        category.openFirstProduct();
        ProductPage product1 = new ProductPage(driver);
        product1.waitForProductPageReady();
        product1.setQuantity(1);
        product1.addToCart();
        Thread.sleep(2000);
        String screen1 = "screens/test1_/step1_" + dtf.format(LocalDateTime.now()) + ".png";
        takeScreenshot(screen1);
        String name1 = product1.getName();
        double price1 = Double.parseDouble(product1.getPrice());

        // =====================
        // פריט 2: קטגוריה B
        home.openCategoryByHref("/electronics");
        category.openFirstProduct();
        ProductPage product2 = new ProductPage(driver);
        product2.waitForProductPageReady();
        product2.setQuantity(2);
        product2.addToCart();
        Thread.sleep(2000);
        String screen2 = "screens/test1_/step2_" + dtf.format(LocalDateTime.now()) + ".png";
        takeScreenshot(screen2);
        String name2 = product2.getName();
        double price2 = Double.parseDouble(product2.getPrice());

        // =====================
        // פריט 3: קטגוריה C
        home.openCategoryByHref("/phones");
        category.openFirstProduct();
        ProductPage product3 = new ProductPage(driver);
        product3.waitForProductPageReady();
        product3.setQuantity(1);
        product3.addToCart();
        Thread.sleep(1500);
        String screen3 = "screens/test1_/step3_" + dtf.format(LocalDateTime.now()) + ".png";
        takeScreenshot(screen3);
        String name3 = product3.getName();
        double price3 = Double.parseDouble(product3.getPrice());

        // =====================
        // פתיחת העגלה ובדיקת פריטים
        home.goToCart();
        Thread.sleep(2000);

        double row1Price = price1 * 1;
        double row2Price = price2 * 2;
        double row3Price = price3 * 1;

        double subtotal = cart.getSubtotal();
        int amount = cart.getCartQuantity();
        double calculatedSubtotal = row1Price + row2Price + row3Price;

        assert subtotal > 0 : "Subtotal לא גדול מאפס";
        assert subtotal <= calculatedSubtotal : "Subtotal גבוה מהמחושב - יתכן מבצע";
        assert amount == 3 : "כמות המוצרים לא תואמת";

        // =====================
        // שמירה ל-Excel
        Map<String, String> rowMap1 = new HashMap<>();
        rowMap1.put("ProductName", name1);
        rowMap1.put("UnitPrice", String.valueOf(price1));
        rowMap1.put("Quantity", "1");
        rowMap1.put("RowPrice", String.valueOf(row1Price));
        rowMap1.put("Status", "PASS");
        rowMap1.put("Screenshot", screen1);

        Map<String, String> rowMap2 = new HashMap<>();
        rowMap2.put("ProductName", name2);
        rowMap2.put("UnitPrice", String.valueOf(price2));
        rowMap2.put("Quantity", "2");
        rowMap2.put("RowPrice", String.valueOf(row2Price));
        rowMap2.put("Status", "PASS");
        rowMap2.put("Screenshot", screen2);

        Map<String, String> rowMap3 = new HashMap<>();
        rowMap3.put("ProductName", name3);
        rowMap3.put("UnitPrice", String.valueOf(price3));
        rowMap3.put("Quantity", "1");
        rowMap3.put("RowPrice", String.valueOf(row3Price));
        rowMap3.put("Status", "PASS");
        rowMap3.put("Screenshot", screen3);

        results.add(rowMap1);
        results.add(rowMap2);
        results.add(rowMap3);

        ExcelWriter.writeCartResults("output/cart_results.xlsx", results);
    }

}
