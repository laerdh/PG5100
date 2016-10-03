package no.westerdals.pg5100.backend.ejb;

import no.westerdals.pg5100.backend.DeleterEJB;
import no.westerdals.pg5100.backend.entity.Address;
import no.westerdals.pg5100.backend.entity.User;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.validation.ConstraintViolationException;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class UserEJBTest {

    private static final String USER_EMAIL = "test@test.com";
    private static final String USER_PASSWORD = "test1234";

    @Deployment
    public static JavaArchive createDeployment() {

        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, "no.westerdals.pg5100.backend")
                .addPackages(true, "org.apache.commons.codec")
                .addAsResource("META-INF/persistence.xml");
    }

    @EJB
    private UserEJB user;

    @EJB
    private DeleterEJB deleter;


    @Before
    @After
    public void emptyDatabase() throws Exception {
        deleter.deleteEntities(User.class);
    }

    @Test
    public void testCanCreateUser() throws Exception {
        assertTrue(user.createUser(USER_EMAIL, USER_PASSWORD));
    }

    @Test
    public void testCreateUserWithInvalidEmail() throws Exception {
        String email = "t@1";
        String password = "123";

        try {
            user.createUser(email, password);
            fail("Should throw exception");
        } catch (Exception ex) {
            assertTrue(hasConstraintViolations(ex));
        }
    }

    @Test
    public void testGetUser() throws Exception {
        assertTrue(user.createUser(USER_EMAIL, USER_PASSWORD));

        User u = user.getUser(USER_EMAIL);
        assertNotNull(u);
        assertEquals(u.getEmail(), USER_EMAIL);
    }

    @Test
    public void testLoginUser() throws Exception {
        assertTrue(user.createUser(USER_EMAIL, USER_PASSWORD));

        assertTrue(user.login(USER_EMAIL, USER_PASSWORD));
    }

    private boolean hasConstraintViolations(Exception e) {
        Throwable t = e.getCause();

        while (t != null && !(t instanceof ConstraintViolationException)) {
            t = t.getCause();
        }

        return t != null;
    }
}