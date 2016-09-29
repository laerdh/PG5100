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
import javax.validation.ConstraintViolationException;

import java.util.List;

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
    private PostEJB post;

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
            User u = user.create(null, null, null);
            fail("Should throw ConstraintViolationException");
        } catch (EJBException e) {
            // Unwrap exception and make sure it is an
            // ConstraintViolationException
            assertTrue(isConstraintViolation(e));
        }
    }

    @Test
    public void testRegisterNewUser() throws Exception {
        User u = user.create(USER_NAME, USER_SURNAME, USER_EMAIL);

        assertNotNull(u.getId());
    }

    @Test
    public void testIsRegistered() throws Exception {
        assertFalse(user.isRegistered(USER_EMAIL));

        User u = user.create(USER_NAME, USER_SURNAME, USER_EMAIL);
        assertNotNull(u.getId());

        assertTrue(user.isRegistered(USER_EMAIL));
    }

    @Test
    public void testDeleteUser() throws Exception {
        User u = user.create(USER_NAME, USER_SURNAME, USER_EMAIL);
        assertNotNull(u.getId());

        long expected = 1;
        long actual = user.delete(u.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void testGetNumberOfUsers() throws Exception {
        int nbOfUsers = 5;
        long currentUsers = user.getNumberOfUsers();

        for (int i = 1; i <= nbOfUsers; i++) {
            String name = "Test" + i;
            String surname = "Test" + i;
            String email = "test" + i + "@test.com";

            user.create(name, surname, email);
        }

        int expected = nbOfUsers + (int) currentUsers;

        assertEquals(expected, user.getNumberOfUsers());
    }

    @Test
    public void testGetTotalPosts() throws Exception {
        User u = user.create(USER_NAME, USER_SURNAME, USER_EMAIL);
        assertNotNull(u.getId());

        long nbOfPosts = user.getTotalPosts(u.getId());

        String text = "This is a test-message";
        Post p = post.create(u, text);

        long expected = nbOfPosts + 1;
        long actual = user.getTotalPosts(u.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTotalPostsWhenNone() throws Exception {
        User u = user.create(USER_NAME, USER_SURNAME, USER_EMAIL);
        assertNotNull(u.getId());

        long nbOfPosts = user.getTotalPosts(u.getId());

        assertEquals(0, nbOfPosts);
    }

    @Test
    public void testGetTotalUsers() throws Exception {
        User u1 = user.create(USER_NAME, USER_SURNAME, USER_EMAIL);

        long nbOfUsers = user.getNumberOfUsers();

        User u2 = user.create(USER_NAME, USER_SURNAME, "unique@test.com");

        assertEquals(nbOfUsers + 1, user.getNumberOfUsers());
    }

    @Test
    public void testGetTotalUsersWhenNone() throws Exception {
        assertEquals(0, user.getNumberOfUsers());
    }

    @Test
    public void testGetTopPosters() throws Exception {
        User u1 = user.create("U1", "U1", "U1@test.com");
        User u2 = user.create("U2", "U2", "U2@test.com");
        User u3 = user.create("U3", "U3", "U3@test.com");
        User u4 = user.create("U4", "U4", "U4@test.com");

        String text = "Testpost";
        Post p1 = post.create(u1, text);
        Post p2 = post.create(u2, text);
        Post p3 = post.create(u2, text);
        Post p4 = post.create(u3, text);
        Post p5 = post.create(u3, text);
        Post p6 = post.create(u3, text);

        List<User> topPosters = user.getTopPosters(4);
        assertNotNull(topPosters);

        long first = topPosters.get(0).getId();
        long last = topPosters.get(topPosters.size() - 1).getId();

        assertEquals((long) u3.getId(), first);
        assertEquals((long) u4.getId(), last);
    }

    @Test
    public void testGetTopCommenters() throws Exception {
        User u1 = user.create("U1", "U1", "U1@test.com");
        User u2 = user.create("U2", "U2", "U2@test.com");
        User u3 = user.create("U3", "U3", "U3@test.com");

        String text = "Testcomment";
    }

    private boolean isConstraintViolation(Exception ex) {
        Throwable t = ex.getCause();

        while((t != null) && !(t instanceof ConstraintViolationException)) {
            t = t.getCause();
        }
        return t instanceof ConstraintViolationException;
    }
}
