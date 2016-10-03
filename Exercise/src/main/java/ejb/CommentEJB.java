package ejb;

import entity.Comment;
import entity.Post;
import entity.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class CommentEJB {

    // Dependency injection: The container will add it
    @PersistenceContext
    private EntityManager em;


    public CommentEJB() {}


    public Comment add(User user, Post post, String text) {
        Comment comment = new Comment();

        if (post != null) {
            comment.setAuthor(user);
            comment.setText(text);
            comment.setPost(post);

            em.persist(comment);

            post.getComments().add(comment);
            em.merge(post);

            return comment;
        }
        return null;
    }

    public Comment addTo(User user, Comment comment, String text) {
        Comment com = new Comment();

        if (comment != null) {
            com.setAuthor(user);
            com.setPost(comment.getPost());
            com.setText(text);

            em.persist(com);

            // Update comment
            comment.getComments().add(com);
            em.merge(comment);

            return comment;
        }
        return null;
    }

    public Long delete(long commentId) {
        Query query = em.createQuery("delete from Comment c where c.id = :id");
        query.setParameter("id", commentId);

        return (long) query.executeUpdate();
    }

    public List<Comment> getAllComments() {
        Query query = em.createNamedQuery(Comment.GET_ALL_COMMENTS);

        return query.getResultList();
    }

    public List<Comment> getCommentsOnComment(long commentId) {
        Query query = em.createNamedQuery(Comment.GET_COMMENTS_TO_COMMENTS);
        query.setParameter("commentId", commentId);

        return query.getResultList();
    }
}
