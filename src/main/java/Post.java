import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = Post.GET_ALL, query = "select p from Post p")
})
public class Post {

    public static final String GET_ALL = "GET_ALL";

    @Id
    @GeneratedValue
    private Long id;


    // Many posts can belong to an author
    @ManyToOne
    private User author;


    // One post can have many comments
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    private String text;


    public Post(){}


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public User getAuthor() { return author; }

    public void setAuthor(User author) { this.author = author; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public Date getCreatedAt() { return createdAt; }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public List<Comment> getComments() { return comments; }

    public void setComments(List<Comment> comments) { this.comments = comments; }
}
