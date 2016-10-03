package no.westerdals.pg5100.backend.ejb;

import no.westerdals.pg5100.backend.entity.Event;
import no.westerdals.pg5100.backend.entity.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class EventEJB {

    @PersistenceContext
    private EntityManager em;


    public EventEJB() {}


    public Long create(Date eventDate, String description) {
        if (eventDate == null || (description == null || description.isEmpty())) {
            return null;
        }

        Event event = new Event();
        event.setEventDate(eventDate);
        event.setDescription(description);

        em.persist(event);

        return event.getId();
    }

    public int delete(long id) {
        Query query = em.createQuery("delete from Event e where e.id = :id");
        query.setParameter("id", id);

        return query.executeUpdate();
    }

    public boolean attend(User user, long eventId) {
        Event event = em.find(Event.class, eventId);

        if (event == null) {
            return false;
        }

        event.getAttendants().add(user);
        em.merge(event);
        return true;
    }

    public int getAttendants(long eventId) {
        Query query = em.createNamedQuery(Event.GET_ATTENDANTS);
        query.setParameter("id", eventId);

        return (int) query.getSingleResult();
    }
}
