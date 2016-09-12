import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class PostTest {
    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("DB");
    private EntityManager em;

    private boolean persistInTransaction(Object... obj) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            for(Object o : obj) {
                em.persist(o);
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            return false;
        }
        return true;
    }

    @Before
    public void setUp() throws Exception {
        em = factory.createEntityManager();
    }

    @After
    public void tearDown() throws Exception {
        em.close();
        factory.close();
    }

    @Test
    public void testCreateEmptyPost() throws Exception {
        Post post = new Post();

        assertTrue(persistInTransaction(post));
    }

    @Test
    public void testCreatePostWithData() throws Exception {
        Post post = new Post();
        post.setText("This post is for testing");
        post.setCreatedAt(new Date());

        assertTrue(persistInTransaction(post));
    }

    @Test
    public void testCreateSeveralPosts() throws Exception {
        int nbOfPosts = 5;
        for (int i = 0; i < nbOfPosts; i++) {
            Post post = new Post();
            post.setText("Test-post " + i);
            post.setCreatedAt(new Date());

            assertTrue(persistInTransaction(post));
        }

        Query query = em.createQuery("SELECT p FROM Post p");
        List<Post> postsFound = query.getResultList();

        // Assert that posts found are equal to number of posts
        assertEquals(postsFound.size(), nbOfPosts);
    }

    @Test
    public void testAddPostToUser() throws Exception {
        User user = new User();
        user.setName("Lars");

        Post post = new Post();
        post.setText("This post is for testing");
        post.setCreatedAt(new Date());
        post.setAuthor(user);

        assertTrue(persistInTransaction(user, post));
    }

    @Test
    public void testAddEmptyComment() throws Exception {
        Post post = new Post();
        Comment comment = new Comment();

        post.setComments(new ArrayList<>());
        post.getComments().add(comment);

        assertTrue(persistInTransaction(post));
    }

    @Test
    public void testAddComment() throws Exception {
        Post post = new Post();
        Comment comment = new Comment();
        comment.setText("This is a comment");

        post.setComments(new ArrayList<>());
        post.getComments().add(comment);

        assertTrue(persistInTransaction(post));

        // Assert that persisted number of comments equals 1
        Post postFound = em.find(Post.class, post.getId());
        assertEquals(postFound.getComments().size(), 1);
    }

    @Test
    public void testAddSeveralComments() throws Exception {
        Post post = new Post();
        List<Comment> comments = new ArrayList<>();
        int nbOfComments = 5;

        for (int i = 0; i < nbOfComments; i++) {
            Comment comment = new Comment();
            comment.setText("This is comment #" + i);

            comments.add(comment);
        }

        post.setComments(comments);

        assertTrue(persistInTransaction(post));

        // Assert that persisted number of comments equals number of comments
        Post postFound = em.find(Post.class, post.getId());
        assertEquals(postFound.getComments().size(), nbOfComments);
    }
}