import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = Post.GET_ALL_POSTS, query =
                "select p from Post p"),
        @NamedQuery(name = Post.GET_TOTAL_POSTS, query =
                "select count(p) from Post p"),
        @NamedQuery(name = Post.GET_TOTAL_POSTS_PER_COUNTRY, query =
                "select count(p) from Post p where p.author.address.country = :country")
})
public class Post {

    // Constants for named queries names
    public static final String GET_ALL_POSTS = "Post.GET_ALL_POSTS";
    public static final String GET_TOTAL_POSTS = "Post.TOTAL_POSTS";
    public static final String GET_TOTAL_POSTS_PER_COUNTRY = "Post.POSTS_PER_COUNTRY";


    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 2, max = 1024)
    private String text;

    private int upVotes;
    private int downVotes;

    // Many posts can belong to an author
    @NotNull
    @ManyToOne
    private User author;

    // One post can have many comments
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Past
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;


    public Post(){
        createdAt = new Date();
    }


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

    public int getUpVotes() { return upVotes; }

    public void setUpVotes(int upVotes) { this.upVotes = upVotes; }

    public int getDownVotes() { return downVotes; }

    public void setDownVotes(int downVotes) { this.downVotes = downVotes; }
}
