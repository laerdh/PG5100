package no.westerdals.pg5100.frontend.po;

import no.westerdals.pg5100.frontend.utils.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HomePageObject extends PageObject {

    public HomePageObject(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isOnPage() {
        return getDriver().getTitle().contains("PG5100 Exam Example - Home");
    }

    public void toStartingPage() {
        getDriver().get(getBaseUrl() + "/home.jsf");
        waitForPageToLoad();
        clickLogOut();
    }

    public boolean isLoggedOut() {
        List<WebElement> elements = getDriver().findElements(By.id("loginButton"));

        return !elements.isEmpty();
    }

    public LoginPageObject clickLogIn() {
        if (isLoggedIn()) {
            return null;
        }

        WebElement login = getDriver().findElement(By.id("loginButton"));
        login.click();
        waitForPageToLoad();

        LoginPageObject po = new LoginPageObject(getDriver());
        return po;
    }

    public void clickLogOut() {
        if (isLoggedIn()) {
            WebElement logout = getDriver().findElement(By.id("logoutFormTop:logoutButton"));
            logout.click();
            waitForPageToLoad();
        }
    }
}
