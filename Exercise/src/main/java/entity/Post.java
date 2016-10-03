package entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = Post.GET_ALL_POSTS, query =
                "select p from Post p"),
        @NamedQuery(name = Post.GET_ALL_POST_COMMENTS, query =
                "select c from Comment c where c.post.id = :postId"),
        @NamedQuery(name = Post.GET_TOTAL_POSTS, query =
                "select count(p) from Post p")
})
public class Post {

    // Constants for named queries names
    public static final String GET_ALL_POSTS = "entity.Post.GET_ALL_POSTS";
    public static final String GET_ALL_POST_COMMENTS = "entity.Post.GET_ALL_COMMENTS";
    public static final String GET_TOTAL_POSTS = "entity.Post.TOTAL_POSTS";


    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 2, max = 1024)
    private String text;

    private int upVotes;
    private int downVotes;

    @NotNull
    @ManyToOne
    private User author;

    // One post can have many comments
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;


    public Post(){}


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public void setAuthor(User author) { this.author = author; }

    public User getAuthor() { return author; }

    public Date getCreatedAt() { return createdAt; }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public List<Comment> getComments() {
        if (comments == null) {
            return new ArrayList<>();
        }
        return comments;
    }

    public void setComments(List<Comment> comments) { this.comments = comments; }

    public int getUpVotes() { return upVotes; }

    public void setUpVotes(int upVotes) { this.upVotes = upVotes; }

    public int getDownVotes() { return downVotes; }

    public void setDownVotes(int downVotes) { this.downVotes = downVotes; }
}
