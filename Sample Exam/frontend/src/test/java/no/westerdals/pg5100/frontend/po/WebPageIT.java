package no.westerdals.pg5100.frontend.po;

import no.westerdals.pg5100.frontend.utils.SeleniumTestBase;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class WebPageIT extends SeleniumTestBase {
    private HomePageObject po;

    @Before
    public void toStartingPage() {
        po = new HomePageObject(getDriver());
        po.toStartingPage();
    }

    @Test
    public void testHomePage() {
        assertTrue(po.isOnPage());
        assertTrue(po.isLoggedOut());
    }

    @Test
    public void testLoginLink() {
        LoginPageObject loginPo = po.doLogIn();
        assertNotNull(loginPo);
        assertTrue(loginPo.isOnPage());
    }

    @Test
    public void testLoginWrongUser() {
        LoginPageObject login = po.doLogIn();
        assertTrue(login.isOnPage());
        assertFalse(po.isOnPage());

        login.createUser("test@test.com", "test1234");
        assertTrue(login.isOnPage());
        assertFalse(po.isOnPage());
    }
}
