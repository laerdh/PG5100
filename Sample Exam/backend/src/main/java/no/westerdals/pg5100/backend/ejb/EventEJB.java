package no.westerdals.pg5100.backend.ejb;

import no.westerdals.pg5100.backend.entity.Event;
import no.westerdals.pg5100.backend.entity.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class EventEJB {

    @PersistenceContext
    private EntityManager em;


    public EventEJB() {}


    public Long create(String title, Date eventDate, String location, String country, String description) {
        boolean valid = validate(title, location, country, description);

        if (valid) {
            Event event = new Event();
            event.setTitle(title);
            event.setEventDate(eventDate);
            event.setLocation(location);
            event.setDescription(description);
            event.setCountry(country);

            em.persist(event);

            return event.getId();
        }

        return null;
    }

    public int delete(long id) {
        Query query = em.createQuery("delete from Event e where e.id = :id");
        query.setParameter("id", id);

        return query.executeUpdate();
    }

    public boolean attend(long eventId, User user) {
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

    public List<Event> getAllEvents() {
        Query query = em.createNamedQuery(Event.GET_ALL_EVENTS);

        return query.getResultList();
    }

    public List<Event> getEventsByCountry(String country) {
        Query query = em.createNamedQuery(Event.GET_EVENTS_BY_COUNTRY);
        query.setParameter("country", country);

        return query.getResultList();
    }

    private boolean validate(String... strings) {
        for (String s : strings) {
            if (s == null || s.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
