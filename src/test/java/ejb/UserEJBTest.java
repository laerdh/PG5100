package ejb;

import jpa.Address;
import jpa.Comment;
import jpa.Post;
import jpa.User;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

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
    public void registerNewUser() throws Exception {
        String name = "Test1";
        String surname = "Test";
        String email = "test12@test.com";

        User u = user.registerNewUser(name, surname, email);

        assertNotNull(u);
        assertNotNull(u.getId());
    }

    @Test
    public void isRegistered() throws Exception {
        // Register a user
        String name = "Test";
        String surname =" Tester";
        String email = "test@test.com";

        assertFalse(user.isRegistered(email));

        User u = user.registerNewUser(name, surname, email);

        assertTrue(user.isRegistered(email));
    }

    @Test
    public void getNumberOfUsers() throws Exception {

    }

}