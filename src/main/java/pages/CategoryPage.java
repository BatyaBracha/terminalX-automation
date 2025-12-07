package pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CategoryPage extends BasePage  {

    public CategoryPage(WebDriver d){ super(d); }

    private final By productLinks = By.xpath("//*[contains(@href, '/product') or contains(@data-product-url, '/product') or contains(@data-url, '/product')] | //a[contains(@class, 'product')]");
    private final By nestedProductAnchor = By.xpath(".//a[contains(@href, '/product')]");
    private final By firstProduct = By.xpath("(//a[contains(@href, '/product')] | //a[contains(@class, 'product')])[1]");

    /**
     * Selects a random product from the category page
     * @return ProductPage object
     */
    public ProductPage selectRandomProduct() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        shortWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(productLinks, 0));

        List<String> productUrls = collectProductUrls();
        System.out.println("Found " + productUrls.size() + " product detail links");

        if (productUrls.isEmpty()) {
            throw new IllegalStateException("No products found on category page");
        }

        Collections.shuffle(productUrls, new Random());

        for (String productUrl : productUrls) {
            try {
                ProductPage productPage = navigateToProductPage(productUrl);
                if (isLikelyProductDestination(driver.getCurrentUrl())) {
                    return productPage;
                }
                System.out.println("URL did not look like a product page, trying next option");
            } catch (Exception e) {
                System.out.println("Navigation failed, trying next product: " + e.getMessage());
            }
        }

        throw new IllegalStateException("Unable to open any product page");
    }

    /**
     * Selects a product by index (0-based)
     * @param index - Product index (0 = first product)
     * @return ProductPage object
     */
    public ProductPage selectProductByIndex(int index) {
        return openProductAtPosition(index);
    }

    public ProductPage openProductAtPosition(int index) {
        List<String> productUrls = collectProductUrls();
        if (productUrls.isEmpty()) {
            throw new IllegalStateException("No products found on category page");
        }
        if (index < 0) {
            index = 0;
        }
        if (index >= productUrls.size()) {
            System.out.println("Requested product index " + index + " exceeds available products. Using last product instead.");
            index = productUrls.size() - 1;
        }
        String targetUrl = productUrls.get(index);
        System.out.println("Opening product at index " + index + ": " + targetUrl);
        return navigateToProductPage(targetUrl);
    }

    public void openFirstProduct() {
        click(firstProduct);
    }

    private List<String> collectProductUrls() {
        List<String> urls = collectProductUrlsFromDom();
        if (!urls.isEmpty()) {
            return urls;
        }
        urls = collectProductUrlsFromInitialState();
        return urls;
    }

    private List<String> collectProductUrlsFromDom() {
        List<WebElement> rawElements = driver.findElements(productLinks);
        List<String> urls = new ArrayList<>();
        for (WebElement element : rawElements) {
            String url = extractProductUrl(element);
            if (isValidProductUrl(url)) {
                urls.add(normalizeUrl(url));
            }
        }
        return new ArrayList<>(new LinkedHashSet<>(urls));
    }

    @SuppressWarnings("unchecked")
    private List<String> collectProductUrlsFromInitialState() {
        List<String> urls = new ArrayList<>();
        try {
            Object raw = ((JavascriptExecutor) driver).executeScript(
                    "try {\n" +
                    "  var state = window.__INITIAL_STATE__;\n" +
                    "  if (!state || !state.listingAndSearchStoreData || !state.listingAndSearchStoreData.listing) { return []; }\n" +
                    "  var products = state.listingAndSearchStoreData.listing.products;\n" +
                    "  if (products && products.items) { return products.items; }\n" +
                    "  return [];\n" +
                    "} catch (e) { return []; }"
            );

            System.out.println("INITIAL_STATE raw type: " + (raw == null ? "null" : raw.getClass()));

            if (raw instanceof List<?>) {
                for (Object obj : (List<?>) raw) {
                    if (!(obj instanceof Map)) {
                        continue;
                    }
                    Map<String, Object> productMap = (Map<String, Object>) obj;
                    String path = valueAsString(productMap.get("url_path"));
                    if (!path.isEmpty()) {
                        urls.add(absolutizeProductPath(path));
                        continue;
                    }
                    String key = valueAsString(productMap.get("url_key"));
                    if (!key.isEmpty()) {
                        urls.add(absolutizeProductPath(key));
                        continue;
                    }
                    String directUrl = valueAsString(productMap.get("url"));
                    if (isValidProductUrl(directUrl)) {
                        urls.add(normalizeUrl(directUrl));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to parse INITIAL_STATE products: " + e.getMessage());
        }

        return new ArrayList<>(new LinkedHashSet<>(urls));
    }

    private String extractProductUrl(WebElement element) {
        if (element == null) {
            return null;
        }
        String[] attributes = {"href", "data-product-url", "data-url", "data-href", "data-link"};
        for (String attr : attributes) {
            try {
                String value = element.getAttribute(attr);
                if (isValidProductUrl(value)) {
                    return value;
                }
            } catch (Exception ignore) {}
        }

        try {
            WebElement nestedAnchor = element.findElement(nestedProductAnchor);
            String nestedHref = nestedAnchor.getAttribute("href");
            if (isValidProductUrl(nestedHref)) {
                return nestedHref;
            }
        } catch (Exception ignore) {}

        return null;
    }

    private ProductPage navigateToProductPage(String productUrl) {
        waitForOverlayToDisappear();
        System.out.println("Navigating directly to product URL: " + productUrl);
        driver.navigate().to(productUrl);
        ProductPage productPage = new ProductPage(driver);
        productPage.waitForProductPageReady();
        return productPage;
    }

    private boolean isValidProductUrl(String href) {
        if (href == null || href.isBlank()) {
            return false;
        }
        String lower = href.toLowerCase();
        if (lower.contains("/products/")) {
            return false;
        }
        return lower.contains("/product/") || lower.contains("/catalog/product");
    }

    private String normalizeUrl(String url) {
        if (url == null) {
            return null;
        }
        int hashIndex = url.indexOf('#');
        return hashIndex >= 0 ? url.substring(0, hashIndex) : url;
    }

    private boolean isLikelyProductDestination(String currentUrl) {
        if (currentUrl == null) {
            return false;
        }
        String lower = currentUrl.toLowerCase();
        return lower.contains("/product/") || lower.contains("/catalog/product");
    }

    private String valueAsString(Object value) {
        if (value instanceof String str) {
            return str.trim();
        }
        return "";
    }

    private String absolutizeProductPath(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        String cleaned = path.startsWith("/") ? path : "/" + path;
        String base = "https://www.terminalx.com";
        return normalizeUrl(base + cleaned);
    }

}