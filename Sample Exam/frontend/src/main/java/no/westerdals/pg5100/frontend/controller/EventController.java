package no.westerdals.pg5100.frontend.controller;

import no.westerdals.pg5100.backend.ejb.EventEJB;

import javax.ejb.EJB;

public class EventController {

    @EJB
    private EventEJB event;

    private String formEventTitle;
    private String formEventCountry;
    private String formEventLocation;
    private String formEventDescription;


    public EventController() {}


    public String getFormEventTitle() { return formEventTitle; }

    public void setFormEventTitle(String formEventTitle) { this.formEventTitle = formEventTitle; }

    public String getFormEventCountry() { return formEventCountry; }

    public void setFormEventCountry(String formEventCountry) { this.formEventCountry = formEventCountry; }

    public String getFormEventLocation() { return formEventLocation; }

    public void setFormEventLocation(String formEventLocation) { this.formEventLocation = formEventLocation; }

    public String getFormEventDescription() { return formEventDescription; }

    public void setFormEventDescription(String formEventDescription) { this.formEventDescription = formEventDescription; }
}

