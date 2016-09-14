import javax.persistence.*;
import java.util.List;

@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    private String text;
    private int upVotes;
    private int downVotes;

    // Many comments can belong to one post
    @ManyToOne
    private Post post;

    // Many comments can belong to one user
    @ManyToOne
    private User user;

    // A comment can have one or more comments
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;


    public Comment(){}


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }

    public void setText(String text ) { this.text = text; }

    public int getUpVotes() { return upVotes; }

    public void setUpVotes(int upVotes) { this.upVotes = upVotes; }

    public int getDownVotes() { return downVotes; }

    public void setDownVotes(int downVotes) { this.downVotes = downVotes; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public void setPost(Post post) { this.post = post; }

    public Post getPost() { return post; }

    public List<Comment> getComments() { return comments; }

    public void setComments(List<Comment> comments) { this.comments = comments; }
}
