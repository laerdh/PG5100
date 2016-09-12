import javax.persistence.*;

@Entity
public class Address {
    @Id
    @GeneratedValue
    private Long id;

    private String country;
    private String city;
    private Long postcode;

    @OneToOne(mappedBy = "address")
    private User user;


    public Address(){}


    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public Long getPostcode() { return postcode; }

    public void setPostcode(Long postcode) { this.postcode = postcode; }
}
