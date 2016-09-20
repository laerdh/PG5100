package jpa;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 1, max = 1024)
    private String text;

    private int upVotes;
    private int downVotes;

    // Many comments can belong to one post
    @ManyToOne
    private Post post;

    // Many comments can belong to one user
    @NotNull
    @ManyToOne
    private User author;

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

    public User getAuthor() { return author; }

    public void setAuthor(User user) { this.author = user; }

    public void setPost(Post post) { this.post = post; }

    public Post getPost() { return post; }

    public List<Comment> getComments() { return comments; }

    public void setComments(List<Comment> comments) { this.comments = comments; }
}
