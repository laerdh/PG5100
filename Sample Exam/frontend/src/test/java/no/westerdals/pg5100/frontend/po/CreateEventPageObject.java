package no.westerdals.pg5100.frontend.po;

import no.westerdals.pg5100.frontend.utils.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.Date;

public class CreateEventPageObject extends PageObject {

    public CreateEventPageObject(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isOnPage() {
        return getDriver().getTitle().contains("PG5100 Exam Example - New Event");
    }

    public HomePageObject createEvent(String title, String country,
                                      String location, String description) {

        WebElement eTitle = getDriver().findElement(By.id("newEventForm:eventTitle"));
        WebElement eDate = getDriver().findElement(By.id("newEventForm:eventDate"));
        WebElement eLocation = getDriver().findElement(By.id("newEventForm:eventLocation"));
        WebElement eDescription = getDriver().findElement(By.id("newEventForm:eventDescription"));
        WebElement eCreate = getDriver().findElement(By.id("newEventForm:createEvent"));

        eTitle.clear();
        eTitle.sendKeys(title);

        eDate.clear();
        eDate.sendKeys("2016-11-11");

        try {
            new Select(getDriver().findElement(By.id("newEventForm:eventCountry"))).selectByValue(country);
        } catch (Exception e) {
            return null;
        }

        eLocation.clear();
        eLocation.sendKeys(location);

        eDescription.clear();
        eDescription.sendKeys(description);

        eCreate.click();
        waitForPageToLoad();

        if (isOnPage()) {
            return null;
        }

        HomePageObject po = new HomePageObject(getDriver());
        return po;
    }
}
