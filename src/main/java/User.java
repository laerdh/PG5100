import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@NamedQueries({
        @NamedQuery(name = User.GET_ALL_USERS, query =
                "select u from User u"),
        @NamedQuery(name = User.GET_TOTAL_USERS, query =
                "select count(u) from User u"),
        @NamedQuery(name = User.GET_TOTAL_USERS_PER_COUNTRY, query =
                "select count(u) from User u where u.address.country = :country"),
        @NamedQuery(name = User.TOP_X_POSTERS, query =
                "select u from User u order by u.posts.size desc"),
        @NamedQuery(name = User.TOP_X_COMMENTERS, query =
                "select u from User u order by u.comments.size desc")
})
public class User {

    // Constants for named queries names
    public static final String GET_ALL_USERS = "Customer.GET_ALL_USERS";
    public static final String GET_TOTAL_USERS = "Customer.GET_TOTAL_USERS";
    public static final String GET_TOTAL_USERS_PER_COUNTRY = "Customer.POSTS_PER_COUNTRY";
    public static final String TOP_X_POSTERS = "Customer.TOP_POSTERS";
    public static final String TOP_X_COMMENTERS = "Customer.TOP_COMMENTERS";


    @Id @GeneratedValue
    private Long id;

    private String name;
    private String surname;
    private String email;

    // User only have one address
    @OneToOne
    private Address address;

    // User can have many posts
    @OneToMany(cascade = CascadeType.ALL)
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

    public List<Post> getPosts() {
        if (posts == null) {
            return new ArrayList<>();
        }
        return posts;
    }

    public void setPosts(List<Post> posts) { this.posts = posts; }

    public List<Comment> getComments() {
        if (comments == null) {
            return new ArrayList<>();
        }
        return comments;
    }

    public void setComments(List<Comment> comments) { this.comments = comments; }

    public int getTotalPosts() { return posts.size(); }
}
