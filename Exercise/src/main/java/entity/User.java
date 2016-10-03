package entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@NamedQueries({
        @NamedQuery(name = User.GET_ALL_USERS, query =
                "select u from User u"),
        @NamedQuery(name = User.GET_TOTAL_USERS, query =
                "select count(u) from User u"),
        @NamedQuery(name = User.GET_USER_COUNTRIES, query =
                "select distinct u.address.country from User u"),
        @NamedQuery(name = User.GET_TOTAL_USERS_PER_COUNTRY, query =
                "select count(u) from User u where u.address.country = :country"),
        @NamedQuery(name = User.GET_TOTAL_POSTS, query =
                "select u.posts.size from User u where u.id = :userId"),
        @NamedQuery(name = User.GET_TOTAL_COMMENTS, query =
                "select u.comments.size from User u where u.id = :userId"),
        @NamedQuery(name = User.TOP_X_POSTERS, query =
                "select u from User u order by u.posts.size desc"),
        @NamedQuery(name = User.TOP_X_COMMENTERS, query =
                "select u from User u order by u.comments.size desc")
})
public class User {

    // Constants for named queries names
    public static final String GET_ALL_USERS = "entity.User.GET_ALL_USERS";
    public static final String GET_TOTAL_USERS = "entity.User.GET_TOTAL_USERS";
    public static final String GET_USER_COUNTRIES = "entity.User.GET_USER_COUNTRIES";
    public static final String GET_TOTAL_USERS_PER_COUNTRY = "entity.User.POSTS_PER_COUNTRY";
    public static final String GET_TOTAL_POSTS = "entity.User.TOTAL_POSTS";
    public static final String GET_TOTAL_COMMENTS = "entity.User.TOTAL_COMMENTS";
    public static final String TOP_X_POSTERS = "entity.User.TOP_POSTERS";
    public static final String TOP_X_COMMENTERS = "entity.User.TOP_COMMENTERS";


    @Id @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 2, max = 128)
    private String name;

    @NotNull
    @Size(min = 2, max = 128)
    private String surname;

    @NotNull
    @Pattern(regexp =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @Past
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date dateOfRegistration;

    // entity.User only have one address
    @OneToOne(orphanRemoval = true)
    private Address address;

    // entity.User can have many posts
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Post> posts;

    // entity.User can have many comments
    @OneToMany(cascade = CascadeType.ALL)
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

    public Date getDateOfRegistration() { return dateOfRegistration; }

    public void setDateOfRegistration(Date dateOfRegistration) { this.dateOfRegistration = dateOfRegistration; }

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
