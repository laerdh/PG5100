package no.westerdals.pg5100.backend.ejb;

import no.westerdals.pg5100.backend.DeleterEJB;
import no.westerdals.pg5100.backend.entity.Event;
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
import javax.validation.ConstraintViolationException;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class EventEJBTest {

    @Deployment
    public static JavaArchive createDeployment() {

        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, "no.westerdals.pg5100.backend")
                .addPackages(true, "org.apache.commons.codec")
                .addAsResource("META-INF/persistence.xml");
    }

    @EJB
    private EventEJB event;

    @EJB
    private UserEJB user;

    @EJB
    private DeleterEJB deleter;


    @Before
    @After
    public void emptyDatabase() throws Exception {
        deleter.deleteEntities(Event.class);
        deleter.deleteEntities(User.class);
    }

    @Test
    public void testCreateEvent() throws Exception {
        assertNotNull(event.create("Birthday party", new Date(), "Oslo", "Norway", "Description"));
    }

    @Test
    public void testCreateEventWithEmptyDescription() throws Exception {
        String description = "";

        try {
            event.create("Birthday party", new Date(), "Oslo", "Norway", description);
            fail("Should throw ConstraintViolationException");
        } catch (Exception e) {
            assertTrue(hasConstraintViolation(e));
        }
    }

    @Test
    public void testCreateEventWithNullAsDescription() throws Exception {
        String description = null;

        try {
            event.create("Birthday party", new Date(), "Oslo", "Norway", description);
            fail("Should throw ConstraintViolationException");
        } catch (Exception e) {
            assertTrue(hasConstraintViolation(e));
        }
    }

    @Test
    public void testDeleteEvent() throws Exception {
        String description = "Birthday party";
        Long id = event.create("Birthday party", new Date(), "Oslo", "Norway", description);

        assertNotNull(id);

        int n = event.delete(id);

        assertEquals(1, n);
    }

    @Test
    public void testGetAttendants() throws Exception {
        String username = "test@test.com";
        String password = "test1234";
        String firstname = "Test";
        String lastname = "Tester";
        String country = "Norway";

        String title = "Birthday party";
        String location = "Oslo";
        String description = "Description";

        assertTrue(user.createUser(username, password, firstname, null, lastname, country));
        Long id = event.create(title, new Date(), location, country, description);

        assertNotNull(id);
        User u = user.getUser(username);
        assertNotNull(u);

        int attendants = event.getAttendants(id);
        assertEquals(attendants, 0);

        assertTrue(event.attend(id, u));
        attendants = event.getAttendants(id);

        // Assert that attendants has increased by one
        assertEquals(attendants, 1);
    }

    private boolean hasConstraintViolation(Exception e) {
        Throwable t = e.getCause();

        while(t != null && !(t instanceof ConstraintViolationException)) {
            t = t.getCause();
        }

        return t != null;
    }
}