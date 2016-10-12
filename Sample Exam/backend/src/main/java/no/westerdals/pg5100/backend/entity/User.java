package no.westerdals.pg5100.backend.entity;

import no.westerdals.pg5100.backend.validation.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class User {

    @Id
    @Pattern(regexp =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @NotEmpty
    private String hash;

    @NotEmpty
    @Size(max = 26)
    private String salt;

    @NotEmpty
    @Size(min = 2, max = 128)
    private String firstName;

    @Size(max = 128)
    private String middleName;

    @NotEmpty
    @Size(min = 2, max = 128)
    private String lastName;

    @Embedded
    private Address address;

    @ManyToMany(mappedBy = "attendants", fetch = FetchType.EAGER)
    private List<Event> eventsToAttend;


    public User() {}


    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getHash() { return hash; }

    public void setHash(String hash) { this.hash = hash; }

    public String getSalt() { return salt; }

    public void setSalt(String salt) { this.salt = salt; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }

    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public Address getAddress() { return address; }

    public void setAddress(Address address) { this.address = address; }

    public List<Event> getEventsToAttend() { return eventsToAttend; }

    public void setEventsToAttend(List<Event> eventsToAttend) { this.eventsToAttend = eventsToAttend; }
}
