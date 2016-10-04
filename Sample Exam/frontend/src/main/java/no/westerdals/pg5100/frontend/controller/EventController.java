package no.westerdals.pg5100.frontend.controller;

import no.westerdals.pg5100.backend.ejb.EventEJB;
import no.westerdals.pg5100.backend.entity.Event;
import no.westerdals.pg5100.backend.entity.User;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Named
@RequestScoped
public class EventController implements Serializable {

    @EJB
    private EventEJB event;

    private List<String> countries = Arrays.asList("Albania", "Belgium", "Denmark", "Italy", "Norway", "Sweden", "United Kingdom", "USA");

    private String formEventTitle;
    private Date formEventDate;
    private String formEventCountry;
    private String formEventLocation;
    private String formEventDescription;


    public EventController() {}


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

    public String attend(long eventId, User user) {
        if (user != null) {
            event.attend(eventId, user);
        }
        return "home.jsf";
    }

    public List<Event> getEvents() {
        return event.getAllEvents();
    }

//    public List<Event> getEventsByCountry() {
//        String country = user.getRegisteredUser().getAddress().getCountry();
//        return event.getEventsByCountry(country);
//    }

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

    public List<String> getCountries() { return countries; }
}

