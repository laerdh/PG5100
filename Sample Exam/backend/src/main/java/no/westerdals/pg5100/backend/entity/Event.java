package no.westerdals.pg5100.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NamedQueries({
    @NamedQuery(name = Event.GET_ALL_EVENTS, query =
        "select e from Event e"),
    @NamedQuery(name = Event.GET_ATTENDANTS, query =
        "select e.attendants.size from Event e where e.id = :id"),
    @NamedQuery(name = Event.GET_EVENTS_BY_COUNTRY, query =
        "select e from Event e where e.country = :country")
})
public class Event {

    public static final String GET_ALL_EVENTS = "Event.GET_ALL_EVENTS";
    public static final String GET_ATTENDANTS = "Event.GET_ATTENDANTS";
    public static final String GET_EVENTS_BY_COUNTRY = "Event.GET_EVENTS_BY_COUNTRY";

    @Id @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 2, max = 128)
    private String title;

    @NotNull
    private Date eventDate;

    @NotNull
    private String location;

    @NotNull
    @Size(max = 128)
    private String country;

    @NotNull
    @Size(min = 2, max = 1024)
    private String description;

    @OneToMany
    private List<User> attendants;


    public Event() {}


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public Date getEventDate() { return eventDate; }

    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public List<User> getAttendants() {
        if (attendants == null) {
            return new ArrayList<>();
        }
        return attendants;
    }

    public void setAttendants(List<User> attendants) { this.attendants = attendants; }
}
