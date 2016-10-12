package no.westerdals.pg5100.frontend.po;

import no.westerdals.pg5100.frontend.utils.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class CreateUserPageObject extends PageObject {

    public CreateUserPageObject(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isOnPage() {
        return getDriver().getTitle().contains("PG5100 Exam Example - Create user");
    }

    public HomePageObject createUser(String username, String password,
                                     String confirmedPassword, String firstName,
                                     String middleName, String lastName, String country) {

        WebElement user = getDriver().findElement(By.id("createUserForm:userId"));
        WebElement pwd = getDriver().findElement(By.id("createUserForm:password"));
        WebElement pwdConfirm = getDriver().findElement(By.id("createUserForm:passwordConfirm"));
        WebElement fName = getDriver().findElement(By.id("createUserForm:firstName"));
        WebElement mName = getDriver().findElement(By.id("createUserForm:middleName"));
        WebElement lName = getDriver().findElement(By.id("createUserForm:lastName"));

        user.clear();
        user.sendKeys(username);

        pwd.clear();
        pwd.sendKeys(password);

        pwdConfirm.clear();
        pwdConfirm.sendKeys(confirmedPassword);

        fName.clear();
        fName.sendKeys(firstName);

        mName.clear();
        mName.sendKeys(middleName);

        lName.clear();
        lName.sendKeys(lastName);

        try {
            new Select(getDriver().findElement(By.id("createUserForm:country"))).selectByValue(country);
        } catch (Exception e) {
            return null;
        }

        WebElement create = getDriver().findElement(By.id("createUserForm:createButton"));
        create.click();
        waitForPageToLoad();

        if (isOnPage()){
            return null;
        }

        HomePageObject po = new HomePageObject(getDriver());
        return po;
    }
}
