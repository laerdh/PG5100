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
        LoginPageObject login = po.clickLogIn();
        assertTrue(login.isOnPage());

        CreateUserPageObject create = login.clickCreateUser();
        assertTrue(create.isOnPage());

        HomePageObject home = create.createUser("eric@eric.com", "test1234", "test1234", "Eric", "", "Ericsson", "Norway");
        assertTrue(home.isOnPage());
        assertTrue(home.isLoggedIn("Eric"));

        home.clickLogOut();
        assertTrue(home.isLoggedOut());

        login = home.clickLogIn();
        home = login.loginUser("eric@eric.com", "test1234");

        assertTrue(home.isOnPage());
        assertTrue(home.isLoggedIn());
    }
}
