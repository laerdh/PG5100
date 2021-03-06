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
import java.util.List;

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
    private DeleterEJB deleter;


    @Before
    @After
    public void emptyDatabase() throws Exception {
        deleter.deleteEntities(Event.class);
    }

    @Test
    public void testCreateEvent() throws Exception {
        assertNotNull(event.create("Birthday party", new Date(), "Oslo", "Norway", "Description"));
    }

    @Test
    public void testCreateEventWithEmptyDescription() throws Exception {
        String description = "";

        assertNull(event.create("Birthday party", new Date(), "Oslo", "Norway", description));
    }

    @Test
    public void testCreateEventWithNullAsDescription() throws Exception {
        String description = null;

        assertNull(event.create("Birthday party", new Date(), "Oslo", "Norway", description));
    }

    @Test
    public void testDeleteEvent() throws Exception {
        String description = "Birthday party";
        Long id = event.create("Birthday party", new Date(), "Oslo", "Norway", description);

        assertNotNull(id);

        int n = event.delete(id);

        assertEquals(1, n);
    }
}