package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DynamicContentPage extends BasePage {

    private static final String PAGE_URL = "https://www.terminalx.com/";
    private static final Duration ACCESSIBILITY_TIMEOUT = Duration.ofSeconds(40);
        private static final String HEBREW_ACCESSIBILITY = "\u05E0\u05D2\u05D9\u05E9\u05D5\u05EA";
        private static final String HEBREW_CONTRAST = "\u05E0\u05D9\u05D2\u05D5\u05D3\u05D9\u05D5\u05EA";
        private static final List<String> DARK_CONTRAST_JS_SELECTORS = List.of(
            "#blackwhite_label_63_2",
            "[id^='blackwhite_label']",
            "[data-action*='blackwhite']",
            "[data-action*='contrast'][data-action*='dark']",
            "[data-accessibility*='contrast']",
            "[aria-label*='contrast' i]"
        );

        private final List<By> accessibilityTriggers = List.of(
            By.id("imgAcc"),
            By.cssSelector("button[aria-label*='accessibility' i]"),
            By.cssSelector("[data-testid='accessibility-button']"),
            By.cssSelector("[class*='accessibility'][role='button']"),
            By.cssSelector("button[id*='accessibility']"),
            By.xpath("//button[contains(normalize-space(.),'" + HEBREW_ACCESSIBILITY + "')]"),
            By.xpath("//*[contains(normalize-space(.),'" + HEBREW_ACCESSIBILITY + "') and (@role='button' or self::button)]"),
            By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'accessibility')]")
    );

        private final List<By> darkContrastOptions = List.of(
            By.id("blackwhite_label_63_2"),
            By.cssSelector("[data-action*='contrast'][data-action*='dark']"),
            By.cssSelector("[data-accessibility*='contrast'][data-accessibility*='dark']"),
            By.cssSelector("[aria-label*='contrast' i]"),
            By.cssSelector("[class*='contrast'][role='button']"),
            By.xpath("//button[contains(normalize-space(.),'" + HEBREW_CONTRAST + "')]")
    );

            private final List<By> accessibilityFrames = List.of(
                By.cssSelector("iframe[id*='access']"),
                By.cssSelector("iframe[src*='access']"),
                By.cssSelector("iframe[id*='nagish']"),
                By.cssSelector("iframe[class*='nagish']"),
                By.cssSelector("iframe[src*='nagish']"),
                By.tagName("iframe")
            );

    public DynamicContentPage(WebDriver driver) {
        super(driver);
    }

    public void openPage() {
        driver.navigate().to(PAGE_URL);
        waitForPageReady();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("body")));
    }

    public String captureThemeSignature() {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "const html=document.documentElement;" +
                "const body=document.body;" +
                "const toKey=el=>[el?el.className:'',el?el.getAttribute('style'):''].join('|');" +
                "return toKey(html)+'::'+toKey(body);"
        );
    }

    public void openAccessibilityWidget() {
        driver.switchTo().defaultContent();
        WebElement trigger = findCandidate(accessibilityTriggers);
        safeClick(trigger);
        driver.switchTo().defaultContent();
        waitForAccessibilityMenu();
    }

    public void selectDarkContrastOption() {
        waitForAccessibilityMenu();
        scrollAccessibilityPanel();
        boolean clickedViaJs = false;
        for (String selector : DARK_CONTRAST_JS_SELECTORS) {
            if (clickElementAcrossFrames(selector)) {
                clickedViaJs = true;
                break;
            }
        }

        if (!clickedViaJs) {
            WebElement option = findCandidate(darkContrastOptions);
            safeClick(option);
        }
        driver.switchTo().defaultContent();
    }

    public String waitForDarkContrast(String previousSignature) {
        WebDriverWait contrastWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        return contrastWait.until(d -> {
            String current = captureThemeSignature();
            if (!current.equals(previousSignature) || contrastMarkerPresent()) {
                return current;
            }
            return null;
        });
    }

    private boolean contrastMarkerPresent() {
        Object result = ((JavascriptExecutor) driver).executeScript(
                "const body=document.body;" +
                "const html=document.documentElement;" +
                "const marker=elem=>{if(!elem){return false;}" +
                "const cls=(elem.className||'').toLowerCase();" +
                "const style=(elem.getAttribute('style')||'').toLowerCase();" +
                "return cls.includes('contrast')||style.includes('contrast')||style.includes('invert');};" +
                "if(marker(body)||marker(html)){return true;}" +
                "return !!document.querySelector('[class*=\"contrast\" i],[data-contrast],[aria-pressed=\"true\"]');"
        );
        return Boolean.TRUE.equals(result);
    }

    private WebElement findCandidate(List<By> candidates) {
        WebDriverWait candidateWait = new WebDriverWait(driver, ACCESSIBILITY_TIMEOUT);
        return candidateWait.until(d -> {
            d.switchTo().defaultContent();
            WebElement directHit = findInContext(candidates, d);
            if (directHit != null) {
                return directHit;
            }

            for (By frameLocator : accessibilityFrames) {
                List<WebElement> frames = d.findElements(frameLocator);
                for (WebElement frame : frames) {
                    try {
                        d.switchTo().defaultContent();
                        d.switchTo().frame(frame);
                    } catch (Exception ignore) {
                        d.switchTo().defaultContent();
                        continue;
                    }

                    WebElement framedHit = findInContext(candidates, d);
                    if (framedHit != null) {
                        return framedHit;
                    }

                    d.switchTo().defaultContent();
                }
            }

            d.switchTo().defaultContent();
            return null;
        });
    }

    private WebElement findInContext(List<By> candidates, WebDriver contextDriver) {
        for (By locator : candidates) {
            List<WebElement> matches = contextDriver.findElements(locator);
            for (WebElement element : matches) {
                if (!element.isEnabled()) {
                    continue;
                }
                try {
                    ((JavascriptExecutor) contextDriver).executeScript(
                            "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                            element
                    );
                } catch (Exception ignore) {}
                return element;
            }
        }
        return null;
    }

    private void scrollAccessibilityPanel() {
        driver.switchTo().defaultContent();
        scrollContainersInCurrentContext();

        for (By frameLocator : accessibilityFrames) {
            List<WebElement> frames = driver.findElements(frameLocator);
            for (WebElement frame : frames) {
                try {
                    driver.switchTo().frame(frame);
                    scrollContainersInCurrentContext();
                } catch (Exception ignore) {
                } finally {
                    driver.switchTo().defaultContent();
                }
            }
        }
    }

    private void scrollContainersInCurrentContext() {
        ((JavascriptExecutor) driver).executeScript(
                "const selectors=[\"[class*='access' i]\",\"[id*='access' i]\",\"[class*='nagish' i]\",\"[id*='nagish' i]\"];" +
                        "selectors.forEach(sel=>{document.querySelectorAll(sel).forEach(el=>{try{if(el.scrollHeight>el.clientHeight){el.scrollTop=el.scrollHeight;}}catch(e){}});});"
        );
    }

    private void waitForAccessibilityMenu() {
        WebDriverWait widgetWait = new WebDriverWait(driver, ACCESSIBILITY_TIMEOUT);
        widgetWait.until(d -> {
            for (String selector : DARK_CONTRAST_JS_SELECTORS) {
            if (elementExistsInAnyFrame(d, selector)) {
                return true;
            }
            }
            return false;
        });
        driver.switchTo().defaultContent();
    }

        private boolean elementExistsInAnyFrame(WebDriver contextDriver, String cssSelector) {
        Object exists = ((JavascriptExecutor) contextDriver).executeScript(
            "const selector=arguments[0];" +
                        "const visited=new Set();" +
                "const search=win=>{if(!win||visited.has(win)){return false;}visited.add(win);" +
                "try{const doc=win.document;if(doc.querySelector(selector)){return true;}" +
                        "for(let i=0;i<win.frames.length;i++){if(search(win.frames[i])){return true;}}}" +
                        "catch(e){}return false;};" +
                        "return search(window);",
            cssSelector
        );
        return Boolean.TRUE.equals(exists);
    }

        private boolean clickElementAcrossFrames(String cssSelector) {
        Object clicked = ((JavascriptExecutor) driver).executeScript(
            "const selector=arguments[0];" +
                        "const visited=new Set();" +
                        "const click=win=>{if(!win||visited.has(win)){return false;}visited.add(win);" +
                "try{const doc=win.document;const el=doc.querySelector(selector);" +
                        "if(el){el.scrollIntoView({block:'center'});try{el.click();}catch(e){el.dispatchEvent(new MouseEvent('click',{bubbles:true}));}return true;}" +
                        "for(let i=0;i<win.frames.length;i++){if(click(win.frames[i])){return true;}}}" +
                        "catch(e){}return false;};" +
                        "return click(window);",
            cssSelector
        );
        return Boolean.TRUE.equals(clicked);
    }

    private void safeClick(WebElement element) {
        waitForOverlayToDisappear();
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void waitForPageReady() {
        WebDriverWait readyWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        readyWait.until(d -> {
            try {
                Object state = ((JavascriptExecutor) d).executeScript("return document.readyState");
                return "complete".equals(state);
            } catch (Exception e) {
                return false;
            }
        });
    }

    public static String getPageUrl() {
        return PAGE_URL;
    }
}
