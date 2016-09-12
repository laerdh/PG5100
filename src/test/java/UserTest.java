import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class UserTest {
    private EntityManagerFactory factory = Persistence.createEntityManagerFactory("DB");
    private EntityManager em;

    private boolean persistInTransaction(Object... obj) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            for (Object o : obj) {
                em.persist(o);
            }
            tx.commit();
        } catch (Exception e) {
            System.out.println("FAILED TRANSACTION");
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
    public void testEmptyUser() throws Exception {
        User user = new User();

        assertTrue(persistInTransaction(user));

        User found = em.find(User.class, user.getId());

        assertNotNull(found.getId());
    }

    @Test
    public void testUserWithName() throws Exception {
        User user = new User();
        user.setName("Tester");

        assertTrue(persistInTransaction(user));

        User found = em.find(User.class, user.getId());

        assertNotNull(found.getId());
        assertEquals(user.getName(), found.getName());
    }

    @Test
    public void testUserWithAddress() throws Exception {
        User user = new User();
        Address address = new Address();

        address.setCountry("Norway");
        address.setCity("Oslo");
        address.setPostcode((long) 484);
        user.setName("Tester");
        user.setAddress(address);

        assertTrue(persistInTransaction(user, address));
    }

    @Test
    public void testUserAddPost() throws Exception {
        User user = new User();
        user.setName("Tester");

        Post post = new Post();
        post.setText("This post is for test-purposes");
        post.setCreatedAt(new Date());
        post.setAuthor(user);

        assertTrue(persistInTransaction(user, post));
    }

    @Test
    public void testUserAddsSeveralPosts() throws Exception {
        User user = new User();
        user.setName("Tester");

        assertTrue(persistInTransaction(user));

        int nbOfPost = 5;
        for (int i = 0; i < nbOfPost; i++) {
            Post post = new Post();
            post.setText("Post" + i + " - for test-purposes");
            post.setCreatedAt(new Date());
            post.setAuthor(user);

            assertTrue(persistInTransaction(post));
        }

        Query query = em.createNamedQuery(Post.GET_ALL);
        List<Post> postsFound = query.getResultList();

        // Assert that number of found posts are equal to nbOfPost
        assertEquals(postsFound.size(), nbOfPost);
    }

    // TESTS FOR NAMED QUERIES

    @Test
    public void testShouldGetUserByCountry() throws Exception {
        User user = new User();
        Address address = new Address();
        address.setCountry("Norway");
        user.setName("Tester");
        user.setAddress(address);

        assertTrue(persistInTransaction(user, address));

        Query query = em.createNamedQuery(User.GET_BY_COUNTRY);
        query.setParameter("country", "Norway");
        User found = (User) query.getSingleResult();

        // Assert that country are the same
        assertEquals(user.getAddress().getCountry(), found.getAddress().getCountry());
    }
}