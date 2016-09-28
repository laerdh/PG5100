package ejb;

import entity.Post;
import entity.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class PostEJB {

    @PersistenceContext
    private EntityManager em;


    public PostEJB() {}


    public Post create(User user, String text) {
        Post post = new Post();
        post.setText(text);
        post.setAuthor(user);
        post.setCreatedAt(new Date());

        if (user != null) {
            post.setAuthor(user);

            em.persist(post);

            user.getPosts().add(post);
            em.merge(user);
            return post;
        }
        return null;
    }

    public Long delete(long postId) {
        Query query = em.createQuery("delete from Post p where p.id = :id");
        query.setParameter("id", postId);

        return (long) query.executeUpdate();
    }

    public List<Post> getAllPosts() {
        Query query = em.createNamedQuery(Post.GET_ALL_POSTS);
        return query.getResultList();
    }

    public long getTotalPosts() {
        Query query = em.createNamedQuery(Post.GET_TOTAL_POSTS);
        return (Long) query.getSingleResult();
    }
}
