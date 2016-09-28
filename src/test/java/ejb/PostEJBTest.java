package ejb;

import entity.Post;
import entity.User;
import entity.UserTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.validation.ConstraintViolationException;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PostEJBTest {

    private static final String USER_NAME = "Test";
    private static final String USER_SURNAME = "Test";
    private static final String USER_EMAIL = "test@test.com";
    private static final String POST_TEXT = "This is a test";

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
    private PostEJB post;

    @EJB
    private UserEJB user;

    @EJB
    private DeleterEJB deleter;


    @Before
    @After
    public void emptyDatabase() throws Exception {
        deleter.deleteEntities(Post.class);
        deleter.deleteEntities(User.class);
    }

    @Test
    public void createValidPost() throws Exception {
        User u = user.create(USER_NAME, USER_SURNAME, USER_EMAIL);
        Post p = post.create(u, POST_TEXT);

        assertNotNull(p.getId());
    }

    @Test
    public void createNotValidPost() throws Exception {
        User u = user.create(USER_NAME, USER_SURNAME, USER_EMAIL);

        try {
            Post p = post.create(u, null);
            fail("Should throw ConstraintViolationException");
        } catch (EJBTransactionRolledbackException e) {
            assertTrue(isConstraintViolation(e));
        }
    }

    @Test
    public void delete() throws Exception {
        User u = user.create(USER_NAME, USER_SURNAME, USER_EMAIL);
        Post p = post.create(u, POST_TEXT);

    }

    @Test
    public void getAllPosts() throws Exception {

    }

    @Test
    public void getTotalPosts() throws Exception {

    }

    private boolean isConstraintViolation(Exception ex) {
        Throwable t = ex.getCause();

        while ((t != null) && !(t instanceof ConstraintViolationException)) {
            t = t.getCause();
        }
        return t instanceof ConstraintViolationException;
    }

}