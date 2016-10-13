package no.westerdals.pg5100.frontend.po;

import no.westerdals.pg5100.frontend.utils.SeleniumTestBase;
import org.junit.Before;
import org.junit.Test;

import javax.ejb.AfterCompletion;

import static org.junit.Assert.*;

public class WebPageIT extends SeleniumTestBase {

    private HomePageObject po;

    @Before
    public void toStartingPage() throws Exception {
        po = new HomePageObject(getDriver());
        po.toStartingPage();
    }

    @Test
    public void testHomePage() throws Exception {
        assertTrue(po.isOnPage());
        assertTrue(po.isLoggedOut());
    }

    @Test
    public void testLoginLink() throws Exception {
        LoginPageObject loginPo = po.clickLogIn();
        assertNotNull(loginPo);
        assertTrue(loginPo.isOnPage());
    }

    @Test
    public void testLoginWrongUser() throws Exception {
        LoginPageObject login = po.clickLogIn();
        assertTrue(login.isOnPage());
        assertFalse(po.isOnPage());

        login.loginUser("test@test.com", "test");
        assertTrue(login.isOnPage());
        assertFalse(po.isOnPage());
    }

    @Test
    public void testCreateUserFailsDueToPasswordMismatch() throws Exception {
        String pw1 = "test123";
        String pw2 = "test1234";

        LoginPageObject login = po.clickLogIn();
        assertTrue(login.isOnPage());

        CreateUserPageObject create = login.clickCreateUser();
        assertTrue(create.isOnPage());

        po = create.createUser("john@johnson.com", pw1, pw2, "John", "", "Johnson", "United Kingdom");
        assertNull(po);
        assertTrue(create.isOnPage());
    }

    @Test
    public void testCreateValidUser() throws Exception {
        LoginPageObject login = po.clickLogIn();
        assertTrue(login.isOnPage());

        CreateUserPageObject create = login.clickCreateUser();
        assertTrue(create.isOnPage());

        HomePageObject home = create.createUser("jack@jackson.no", "test1234", "test1234", "Jack", "", "Jackson", "USA");
        assertTrue(home.isOnPage());
        assertTrue(home.isLoggedIn("Jack"));
    }

    @Test
    public void testLogin() throws Exception {
        createAndLoginUser("foo@bar.com", "test1234", "Foo", "Bar", "Italy");
        assertTrue(po.isOnPage());

        po.clickLogOut();
        assertTrue(po.isLoggedOut());

        LoginPageObject login = po.clickLogIn();
        login.loginUser("foo@bar.com", "test1234");
        assertTrue(po.isOnPage());
        assertTrue(po.isLoggedIn());
    }

    @Test
    public void testCreateOneEvent() throws Exception {
        createAndLoginUser("foobar@foobar.com", "test1234", "Foobar", "Foobar", "Sweden");
        assertTrue(po.isOnPage());

        CreateEventPageObject event = po.clickCreateEvent();
        assertTrue(event.isOnPage());

        po = event.createEvent("GardenParty", "Sweden", "Stockholm", "Fun");
        assertTrue(po.isOnPage());

        int expected = 1;
        int actual = po.getNumberOfDisplayedEvents();
        assertEquals("Should have created one event", expected, actual);
    }

    private void createAndLoginUser(String username, String password, String firstname, String lastname, String country) throws Exception {
        po.clickLogOut();
        LoginPageObject login = po.clickLogIn();
        CreateUserPageObject create = login.clickCreateUser();

        po = create.createUser(username, password, password, firstname, "", lastname, country);
        assertTrue(po.isLoggedIn());
    }

    private void loginExistingUser(String username, String password) {
        po.clickLogOut();
        LoginPageObject login = po.clickLogIn();
        po = login.loginUser(username, password);
        assertTrue(po.isOnPage());
    }
}
