package no.westerdals.pg5100.frontend.po;

import no.westerdals.pg5100.frontend.utils.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPageObject extends PageObject {

    public LoginPageObject(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isOnPage() {
        return getDriver().getTitle().contains("PG5100 Exam Example - Login");
    }

    public HomePageObject createUser(String username, String password) {
        WebElement id = getDriver().findElement(By.id("loginForm:userId"));
        WebElement pw = getDriver().findElement(By.id("loginForm:password"));
        WebElement login = getDriver().findElement(By.id("loginForm:logIn"));

        id.clear();
        id.sendKeys(username);
        pw.clear();
        pw.sendKeys(password);
        login.click();
        waitForPageToLoad();

        HomePageObject po = new HomePageObject(getDriver());
        return po;
    }
}
