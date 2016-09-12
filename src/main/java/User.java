import javax.persistence.*;
import java.util.List;


@Entity
@NamedQueries({
        @NamedQuery(name = User.GET_BY_COUNTRY, query = "select distinct u from User u where u.address.country = :country"),
        @NamedQuery(name = User.PER_COUNTRY, query = "select count(u) from User u where u.address.country = :country"),
        @NamedQuery(name = User.TOP_POSTERS, query = "select u from User u order by u.posts.size desc")
})
public class User {

    // Named queries names
    public static final String GET_BY_COUNTRY = "GET_BY_COUNTRY";
    public static final String PER_COUNTRY = "PER_COUNTRY";
    public static final String TOP_POSTERS = "TOP_POSTERS";

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String surname;
    private String email;

    // User only have one address
    @OneToOne
    private Address address;

    // User can have many posts
    @OneToMany
    private List<Post> posts;

    // User can have many comments
    @OneToMany
    private List<Comment> comments;


    public User() {}


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public Address getAddress() { return address; }

    public void setAddress(Address address) { this.address = address; }

    public List<Post> getPosts() { return posts; }

    public void setPosts(List<Post> posts) { this.posts = posts; }

    public List<Comment> getComments() { return comments; }

    public void setComments(List<Comment> comments) { this.comments = comments; }

    public int getTotalPosts() { return posts.size(); }
}
