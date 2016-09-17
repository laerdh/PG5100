import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class PostTest {
    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("DB");
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
        User user = getValidUser();

        Post post = new Post();
        post.setText("Test post");
        post.setCreatedAt(new Date());
        post.setAuthor(user);

        assertFalse(hasViolations(post));
        assertTrue(persistInTransaction(user, post));
    }

    @Test
    public void testCreateSeveralPosts() throws Exception {
        User user = getValidUser();
        int nbOfPosts = 5;

        assertTrue(persistInTransaction(user));

        for (int i = 0; i < nbOfPosts; i++) {
            Post post = new Post();
            post.setText("Test post " + i);
            post.setCreatedAt(new Date());
            post.setAuthor(user);

            assertTrue(persistInTransaction(post));
        }

        Query query = em.createQuery("SELECT p FROM Post p");
        List<Post> postsFound = query.getResultList();

        // Assert that posts found are equal to number of posts
        assertEquals(postsFound.size(), nbOfPosts);
    }

    @Test
    public void testAddPostToUser() throws Exception {
        User user = getValidUser();

        Post post = new Post();
        post.setText("Test post");
        post.setCreatedAt(new Date());
        post.setAuthor(user);


        assertTrue(persistInTransaction(user, post));
    }

    @Test
    public void testAddEmptyComment() throws Exception {
        User user = getValidUser();

        Post post = new Post();
        post.setText("Test post");
        post.setAuthor(user);

        Comment comment = new Comment();

        post.setComments(new ArrayList<>());
        post.getComments().add(comment);

        assertFalse(hasViolations(post));
        assertTrue(persistInTransaction(user, post));
    }

    @Test
    public void testAddComment() throws Exception {
        // Arrange
        User user = getValidUser();
        Post post = new Post();
        post.setText("This is a test");
        post.setAuthor(user);

        Comment comment = new Comment();
        comment.setText("This is a comment");

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
        User user = getValidUser();
        Post post = new Post();
        post.setText("Test post");
        post.setAuthor(user);

        List<Comment> comments = new ArrayList<>();
        int nbOfComments = 5;

        for (int i = 0; i < nbOfComments; i++) {
            Comment comment = new Comment();
            comment.setText("This is comment #" + i);

            comments.add(comment);
        }

        post.setComments(comments);

        assertTrue(persistInTransaction(user, post));

        // Assert that persisted number of comments equals number of comments
        Post postFound = em.find(Post.class, post.getId());
        assertEquals(postFound.getComments().size(), nbOfComments);
    }

    // Tests for named queries

    @Test
    public void testShouldGetAllPosts() throws Exception {
        int nbOfPosts = 20;
        User user = getValidUser();
        List<Post> posts = getCollection(nbOfPosts, Post.class);

        assertTrue(persistInTransaction(user));

        for (Post p : posts) {
            p.setText("Test post");
            p.setAuthor(user);
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
        User user = getValidUser();
        List<Post> posts = getCollection(nbOfPosts, Post.class);

        assertTrue(persistInTransaction(user));

        for (Post p : posts) {
            p.setText("Test post");
            p.setAuthor(user);
            assertTrue(persistInTransaction(p));
        }


        // Act
        Query query = em.createNamedQuery(Post.GET_TOTAL_POSTS);
        Long countPosts = (long) query.getSingleResult();


        // Assert
        assertEquals(countPosts.intValue(), nbOfPosts);
    }

    @Test
    public void testShouldGetTotalPostsPerCountry() throws Exception {
        // Arrange
        User user1 = getValidUser();
        user1.getAddress().setCountry("Norway");
        User user2 = getValidUser();
        user2.getAddress().setCountry("Sweden");

        Post p1 = new Post();
        p1.setText("Test post 1");
        p1.setAuthor(user1);

        Post p2 = new Post();
        p2.setText("Test post 2");
        p2.setAuthor(user1);

        Post p3 = new Post();
        p3.setText("Test post 3");
        p3.setAuthor(user2);

        assertTrue(persistInTransaction(user1, user2, p1, p2, p3));


        // Act
        Query query = em.createNamedQuery(Post.GET_TOTAL_POSTS_PER_COUNTRY);
        query.setParameter("country", "Norway");
        Long count = (long) query.getSingleResult();


        // Assert
        assertEquals(2, count.intValue());
    }

    // Helper methods

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

    private <T> List<T> getCollection(int size, Class<T> type) {
        List<T> data = new ArrayList<T>();

        for (int i = 0; i < size; i++) {
            try {
                data.add(type.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {

            }
        }
        return data;
    }

    private <T> boolean hasViolations(T obj) {
        Set<ConstraintViolation<T>> violations = validator.validate(obj);

        for (ConstraintViolation<T> cv : violations) {
            System.out.println("VIOLATION: " + cv.toString());
        }

        return violations.size() > 0;
    }

    private User getValidUser() {
        User user = new User();
        user.setName("Test");
        user.setSurname("Tester");
        user.setEmail("test@test.com");
        user.setDateOfRegistration(Date.from(
                LocalDate.of(2010,1,1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        user.setAddress(new Address());

        return user;
    }
}