"# terminalX-automation

תיקיית אוטומציה בדיקות לאתר TERMINALX

## תיאור הפרויקט
פרויקט זה מבצע בדיקות אוטומציה על אתר TERMINALX באמצעות Selenium WebDriver עם TestNG.

## תכניות בדיקה
- **CartFlowTest**: בדיקת זרימת קניה - הוספת 3 מוצרים מקטגוריות שונות לעגלה וביצוע אימות
- **RegistrationFormTest**: בדיקת טופס הרישום - ולידציה של שדות חובה ותקינות כניסה
- **DynamicContentChangeTest**: בדיקת תוכן דינמי - ניווט דרך כפתורים מיוחדים

## ארכיטקטורה
- **BasePage**: מחלקה בסיסית לכל דפי האתר עם פונקציות עזר כמו click, type, waitAndScroll
- **HomePage**: דף הבית
- **CategoryPage**: דף קטגוריה
- **ProductPage**: דף מוצר
- **CartPage**: עגלת קניות
- **ShippingFormPage**: טופס משלוח
- **DynamicContentPage**: דף עם תוכן דינמי

## פרמטרים לבדיקה
הכל מוגדר ב-BaseTest:
- URL: https://www.terminalx.com/
- דרייבר: Chrome ב-Incognito mode
- Timeouts: 15 שניות
" 
