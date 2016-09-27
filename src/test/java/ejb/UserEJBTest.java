package ejb;

import entity.*;
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

import static org.junit.Assert.*;


@RunWith(Arquillian.class)
public class UserEJBTest {

    private static final String USER_NAME = "Test";
    private static final String USER_SURNAME = "Test";
    private static final String USER_EMAIL = "test@test.com";

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, UserEJB.class.getPackage())
                .addPackages(true, User.class.getPackage())
                .addPackages(true, UserEJBTest.class.getPackage())
                .addPackages(true, UserTest.class.getPackage())
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
        deleter.deleteEntities(Post.class);
    }

    @Test
    public void testRegisterEmptyUser() throws Exception {
        try {
            Long id = user.registerNewUser(null, null, null);
            fail();
        } catch (EJBException e) {
            // Unwrap exception and make sure it is an
            // ConstraintViolationException
            assertTrue(isConstraintViolation(e));
        }
    }

    @Test
    public void testRegisterNewUser() throws Exception {
        Long id = user.registerNewUser(USER_NAME, USER_SURNAME, USER_EMAIL);

        assertNotNull(id);
    }

    @Test
    public void isRegistered() throws Exception {
        assertFalse(user.isRegistered(USER_EMAIL));

        Long id = user.registerNewUser(USER_NAME, USER_SURNAME, USER_EMAIL);
        assertNotNull(id);

        assertTrue(user.isRegistered(USER_EMAIL));
    }

    @Test
    public void testUserCreatesEmptyPost() throws Exception {
        Long id = user.registerNewUser(USER_NAME, USER_SURNAME, USER_EMAIL);
        assertNotNull(id);

        String text = "";
        try {
            user.createPost(id, text);
            fail();
        } catch (Exception e) {
            // Find exceptiontype
        }
    }

    @Test
    public void testUserCreatesPost() throws Exception {
        Long id = user.registerNewUser(USER_NAME, USER_SURNAME, USER_EMAIL);
        assertNotNull(id);

        String text = "This is a test-message";

        assertTrue(user.createPost(id, text));
    }

    @Test
    public void getNumberOfUsers() throws Exception {
        int nbOfUsers = 5;
        long currentUsers = user.getNumberOfUsers();

        for (int i = 1; i <= nbOfUsers; i++) {
            String name = "Test" + i;
            String surname = "Test" + i;
            String email = "test" + i + "@test.com";

            user.registerNewUser(name, surname, email);
        }

        int expected = nbOfUsers + (int) currentUsers;

        assertEquals(expected, user.getNumberOfUsers());
    }

    @Test
    public void getTotalPosts() throws Exception {
        Long id = user.registerNewUser(USER_NAME, USER_SURNAME, USER_EMAIL);
        assertNotNull(id);

        long nbOfPosts = user.getTotalPosts(id);

        String text = "This is a test-message";
        assertTrue(user.createPost(id, text));

        assertEquals(nbOfPosts + 1, user.getTotalPosts(id));
    }

    private boolean isConstraintViolation(Exception ex) {
        return ex.getCause() instanceof javax.validation.ConstraintViolationException;
    }
}