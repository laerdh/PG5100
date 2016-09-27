package ejb;

import jpa.Address;
import jpa.Comment;
import jpa.Post;
import jpa.User;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.spi.ArquillianProxyException;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.validation.ConstraintViolationException;

import static org.junit.Assert.*;


@RunWith(Arquillian.class)
public class UserEJBTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(User.class, UserEJB.class, Address.class, Post.class, Comment.class)
                .addAsResource("META-INF/persistence.xml");
    }

    @EJB
    private UserEJB user;

    @Test
    public void testRegisterEmptyUser() throws Exception {
        try {
            Long id = user.registerNewUser(null, null, null);
            fail();
        } catch (EJBException e) {

            // Unwrap exception and make sure it is an
            // ConstraintViolationException
            assertTrue(isConstraintViolationException(e));
        }
    }

    @Test
    public void testRegisterNewUser() throws Exception {
        String name = "Test";
        String surname = "Test";
        String email = "test@test.com";

        Long id = user.registerNewUser(name, surname, email);

        assertNotNull(id);
    }

    @Test
    public void isRegistered() throws Exception {
        String name = "Test";
        String surname = "Test";
        String email = "t@t.com";

        assertFalse(user.isRegistered(email));

        Long id = user.registerNewUser(name, surname, email);

        assertTrue(user.isRegistered(email));
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

    private boolean isConstraintViolationException(Exception ex) {
        return ex.getCause() instanceof javax.validation.ConstraintViolationException;
    }

}