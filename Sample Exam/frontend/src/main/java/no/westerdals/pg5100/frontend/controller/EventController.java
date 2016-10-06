package no.westerdals.pg5100.frontend.controller;

import no.westerdals.pg5100.backend.ejb.EventEJB;
import no.westerdals.pg5100.backend.ejb.UserEJB;
import no.westerdals.pg5100.backend.entity.Event;
import no.westerdals.pg5100.backend.entity.User;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Named
@SessionScoped
public class EventController implements Serializable {

    @EJB
    private EventEJB event;

    @Inject
    private LoginController loginController;

    private String formEventTitle;
    private Date formEventDate;
    private String formEventCountry;
    private String formEventLocation;
    private String formEventDescription;


    private boolean eventsMyCountry;
    private Map<Long, Boolean> attendMap;


    public EventController() {}

    @PostConstruct
    public void init() {
        eventsMyCountry = true;
        attendMap = new ConcurrentHashMap<>();
    }

    public String registerNew() {
        Long id = event.create(formEventTitle, formEventDate, formEventLocation, formEventCountry,
                formEventDescription);

        if (id != null) {
            return "home.jsf";
        }
        return "newEvent.jsf";
    }

    public String delete(long eventId) {
        if (event.delete(eventId) > 0) {
            return "home.jsf";
        }
        return "home.jsf";
    }

    public String setAttendance(long eventId, Boolean attend) {
        if (attend != null && attend) {
            event.attend(eventId, loginController.getRegisteredUser().getEmail());
        } else {
            event.unAttend(eventId, loginController.getRegisteredUser().getEmail());
        }
        return "home.jsf";
    }

    public List<Event> getEvents() {
        List<Event> events;

        if (!loginController.isLoggedIn() || !eventsMyCountry) {
            events = event.getAllEvents();
        } else {
            events = event.getEventsByCountry(loginController.getFormCountry());
        }

        User user = loginController.getRegisteredUser();
        if (user != null) {
            events.stream().map(Event::getId)
                    .forEach(id -> {
                        if (event.isAttending(id, user.getEmail())) {
                            attendMap.put(id, true);
                        } else {
                            attendMap.put(id, false);
                        }
                    });
        }

        return events;
    }

    public String getFormEventTitle() { return formEventTitle; }

    public void setFormEventTitle(String formEventTitle) { this.formEventTitle = formEventTitle; }

    public Date getFormEventDate() { return formEventDate; }

    public void setFormEventDate(Date formEventDate) { this.formEventDate = formEventDate; }

    public String getFormEventCountry() { return formEventCountry; }

    public void setFormEventCountry(String formEventCountry) { this.formEventCountry = formEventCountry; }

    public String getFormEventLocation() { return formEventLocation; }

    public void setFormEventLocation(String formEventLocation) { this.formEventLocation = formEventLocation; }

    public String getFormEventDescription() { return formEventDescription; }

    public void setFormEventDescription(String formEventDescription) { this.formEventDescription = formEventDescription; }

    public boolean getEventsMyCountry() { return eventsMyCountry; }

    public void setEventsMyCountry(boolean eventsMyCountry) { this.eventsMyCountry = eventsMyCountry; }

    public Map<Long, Boolean> getAttendMap() { return attendMap; }

}

