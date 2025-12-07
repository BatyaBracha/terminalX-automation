package tests;

import java.time.LocalDateTime;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import pages.CategoryPage;
import pages.HomePage;
import pages.ProductPage;
import pages.ShippingFormPage;

public class RegistrationFormTest extends BaseTest {

    // --------------------------------------------------------
    // פונקציית עזר: ניווט לטופס המשלוח
    // --------------------------------------------------------
    private ShippingFormPage navigateToForm() {
        HomePage home = new HomePage(driver);
        CategoryPage category = new CategoryPage(driver);

        home.openCategoryByHref("/computers");

        category.openFirstProduct();

        ProductPage product = new ProductPage(driver);
        product.waitForProductPageReady();
        product.buyNow();

        return new ShippingFormPage(driver);
    }

    // --------------------------------------------------------
    // בדיקה: שם פרטי חובה
    // --------------------------------------------------------
    @Test
    public void testRequiredFieldFirstName() {
        ShippingFormPage form = navigateToForm();

        form.fillLastName("Doe");
        form.fillEmail("john@doe.com");
        form.fillPhone("0501234567");
        form.fillStreet("ירמיהו");
        form.fillCity("ירושלים");
        form.fillHouseNumber("12");

        form.submitForm();

        String screen1 = "screens/test3_/step1_" + dtf.format(LocalDateTime.now()) + ".png";
        takeScreenshot(screen1);

        Assert.assertTrue(form.hasErrorForField("firstname"), "שדה שם פרטי לא הציג שגיאת חובה");
    }

    // --------------------------------------------------------
    // בדיקה: אימייל לא תקין
    // --------------------------------------------------------
    @Test
    public void testInvalidEmail() {
        ShippingFormPage form = navigateToForm();

        form.fillFirstName("John");
        form.fillLastName("Doe");
        form.fillEmail("wrong_email");
        form.fillPhone("0501234567");
        form.fillStreet("ירמיהו");
        form.fillCity("Tel Aviv");
        form.fillFloor("5");
        form.fillHouseNumber("12");
        form.fillApartment("17");

        form.submitForm();

        String screen2 = "screens/test3_/step2_" + dtf.format(LocalDateTime.now()) + ".png";
        takeScreenshot(screen2);

        Assert.assertTrue(form.hasErrorForField("email"), "לא הופיעה שגיאה על אימייל לא תקין");
    }

    // --------------------------------------------------------
    // בדיקה: טלפון — ספרות בלבד
    // --------------------------------------------------------
    @Test
    public void testTelephoneDigitsOnly() {
        ShippingFormPage form = navigateToForm();

        form.fillPhone("abc123!!");

        String screen3 = "screens/test3_/step3_" + dtf.format(LocalDateTime.now()) + ".png";
        takeScreenshot(screen3);

        String value = driver.findElement(By.cssSelector("input#telephone")).getAttribute("value");
        Assert.assertTrue(value.matches("\\d+"), "שדה טלפון לא מסנן תווים שאינם ספרות");
    }

    // --------------------------------------------------------
    // בדיקה: כפתור שליחה חסום כשהשדות ריקים
    // --------------------------------------------------------
    @Test
    public void testSubmitDisabledWhenRequiredEmpty() {
        navigateToForm();

        WebElement submit = driver.findElement(By.cssSelector("button[type='submit']"));
        boolean disabled = !submit.isEnabled();

        String screen4 = "screens/test3_/step4_" + dtf.format(LocalDateTime.now()) + ".png";
        takeScreenshot(screen4);

        Assert.assertTrue(disabled, "כפתור שליחה לא חסום כשהשדות ריקים");
    }

}
