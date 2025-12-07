package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ShippingFormPage extends BasePage {

    public ShippingFormPage(WebDriver driver) {
        super(driver);
    }

    private final By firstName = By.xpath("//input[@name='firstName' or @name='first_name' or contains(@placeholder, 'שם')]");
    private final By lastName = By.xpath("//input[@name='lastName' or @name='last_name' or contains(@placeholder, 'משפחה')]");
    private final By email = By.xpath("//input[@name='email' or @type='email']");
    private final By phone = By.xpath("//input[@name='phone' or @name='telephone' or contains(@placeholder, 'טלפון')]");
    private final By street = By.xpath("//input[@name='street' or @name='address' or contains(@placeholder, 'רחוב')]");
    private final By city = By.xpath("//input[@name='city'] | //select[@name='city'] | //input[contains(@placeholder, 'עיר')]");
    private final By houseNumber = By.xpath("//input[@name='houseNumber' or @name='house_number' or contains(@placeholder, 'בית')]");
    private final By floorNumber = By.xpath("//input[@name='floor' or @name='floor_number' or contains(@placeholder, 'קומה')]");
    private final By apartmentNumber = By.xpath("//input[@name='apartment' or @name='apartment_number' or contains(@placeholder, 'דירה')]");
    private final By submitButton = By.xpath("//button[@type='submit'] | //button[contains(., 'שלח')] | //button[contains(., 'Submit')]");
    private final By nextPageTitle = By.xpath("//h1 | //h2 | //h3");

    public void fillFirstName(String txt) { type(firstName, txt); }
    public void fillLastName(String txt) { type(lastName, txt); }
    public void fillEmail(String txt) { type(email, txt); }
    public void fillPhone(String txt) { type(phone, txt); }
    public void fillStreet(String txt) { type(street, txt); }
    public void fillHouseNumber(String txt) { type(houseNumber, txt); }
    public void fillFloor(String txt) { type(floorNumber, txt); }
    public void fillApartment(String txt) { type(apartmentNumber, txt); }

    // עיר – כי זה AutoComplete
    public void fillCity(String txt) {
        type(city, txt);
    }

    public void submitForm() {
        click(submitButton);
    }

    public boolean isNextPageDisplayed() {
        try {
            return driver.findElement(nextPageTitle).isDisplayed();
        } catch (Exception e){
            return false;
        }
    }

    // ========================
    // Error validation
    // Error format: element + sibling with class "error"
    // ========================

    public boolean hasErrorForField(String fieldName) {
        try {
            By selector = By.cssSelector(
                    "[class*='" + fieldName + "'] p[class*='error'], " +
                    "[name='" + fieldName + "'] + [class*='error']"
            );

            WebElement error = driver.findElement(selector);

            String text = error.getText().trim();
            System.out.println("ERROR for " + fieldName + ": '" + text + "'");

            return !text.isEmpty();
        }
        catch (Exception e) {
            System.out.println("ERROR element for " + fieldName + " NOT FOUND");
            return false;
        }
    }


}
