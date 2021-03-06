package entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


@Ignore
public class PostTest {
    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("DB-Test");
    private EntityManager em;

    private ValidatorFactory valFactory;
    private Validator validator;

    @Before
    public void setUp() throws Exception {
        em = factory.createEntityManager();

        valFactory = Validation.buildDefaultValidatorFactory();
        validator = valFactory.getValidator();
    }

    @After
    public void tearDown() throws Exception {
        em.close();
        factory.close();
        valFactory.close();
    }

    @Test
    public void testCreateEmptyPost() throws Exception {
        Post post = new Post();

        assertTrue(hasViolations(post));
        assertFalse(persistInTransaction(post));
    }

    @Test
    public void testCreatePostWithData() throws Exception {
        User user = TestDataProvider.getValidUser();

        Post post = TestDataProvider.getValidPost();

        assertFalse(hasViolations(post));
        assertTrue(persistInTransaction(user, post));
    }

    @Test
    public void testCreateSeveralPosts() throws Exception {
        User user = TestDataProvider.getValidUser();
        int nbOfPosts = 5;

        assertTrue(persistInTransaction(user));

        for (int i = 0; i < nbOfPosts; i++) {
            Post post = TestDataProvider.getValidPost();

            assertTrue(persistInTransaction(post));
        }

        Query query = em.createQuery("SELECT p FROM Post p");
        List<Post> postsFound = query.getResultList();

        // Assert that posts found are equal to number of posts
        assertEquals(postsFound.size(), nbOfPosts);
    }

    @Test
    public void testAddPostToUser() throws Exception {
        User user = TestDataProvider.getValidUser();

        Post post = TestDataProvider.getValidPost();

        assertTrue(persistInTransaction(user, post));
    }

    @Test
    public void testAddEmptyComment() throws Exception {
        User user = TestDataProvider.getValidUser();

        Post post = TestDataProvider.getValidPost();

        Comment comment = TestDataProvider.getValidComment();
        comment.setAuthor(user);

        post.setComments(new ArrayList<>());
        post.getComments().add(comment);

        assertFalse(hasViolations(post));
        assertTrue(persistInTransaction(user, post));
    }

    @Test
    public void testAddComment() throws Exception {
        // Arrange
        User user = TestDataProvider.getValidUser();
        Post post = TestDataProvider.getValidPost();

        Comment comment = TestDataProvider.getValidComment();
        comment.setAuthor(user);

        post.setComments(new ArrayList<>());
        post.getComments().add(comment);


        // Assert no violations and possible to persist
        assertFalse(hasViolations(post));
        assertTrue(persistInTransaction(user, post));


        // Assert that persisted number of comments equals 1
        Post postFound = em.find(Post.class, post.getId());
        assertEquals(postFound.getComments().size(), 1);
    }

    @Test
    public void testAddSeveralComments() throws Exception {
        User user = TestDataProvider.getValidUser();
        Post post = TestDataProvider.getValidPost();

        List<Comment> comments = new ArrayList<>();
        int nbOfComments = 5;

        for (int i = 0; i < nbOfComments; i++) {
            Comment comment = TestDataProvider.getValidComment();
            comment.setAuthor(user);
            comments.add(comment);
        }

        post.setComments(comments);

        assertTrue(persistInTransaction(user, post));

        // Assert that persisted number of comments equals number of comments
        Post postFound = em.find(Post.class, post.getId());
        assertEquals(postFound.getComments().size(), nbOfComments);
    }

    @Test
    public void testShouldGetAllPosts() throws Exception {
        int nbOfPosts = 20;
        User user = TestDataProvider.getValidUser();

        List<Post> posts = TestDataProvider.getCollection(nbOfPosts, Post.class);

        assertTrue(persistInTransaction(user));

        for (Post p : posts) {
            p.setText("Test post");
            assertTrue(persistInTransaction(p));
        }

        Query query = em.createNamedQuery(Post.GET_ALL_POSTS);
        List<Post> postsFound = query.getResultList();

        assertEquals(postsFound.size(), nbOfPosts);
    }

    @Test
    public void testShouldGetTotalPosts() throws Exception {
        // Arrange
        int nbOfPosts = 20;
        User user = TestDataProvider.getValidUser();
        List<Post> posts = TestDataProvider.getCollection(nbOfPosts, Post.class);

        assertTrue(persistInTransaction(user));

        for (Post p : posts) {
            p.setText("Test post");
            assertTrue(persistInTransaction(p));
        }


        // Act
        Query query = em.createNamedQuery(Post.GET_TOTAL_POSTS);
        Long countPosts = (long) query.getSingleResult();


        // Assert
        assertEquals(countPosts.intValue(), nbOfPosts);
    }

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

    private <T> boolean hasViolations(T obj) {
        Set<ConstraintViolation<T>> violations = validator.validate(obj);

        for (ConstraintViolation<T> cv : violations) {
            System.out.println("VIOLATION: " + cv.toString());
        }

        return violations.size() > 0;
    }
}