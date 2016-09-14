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

        Query query = em.createNamedQuery(Post.GET_TOTAL_POSTS);
        Long postsFound = (Long) query.getSingleResult();

        assertEquals(postsFound.intValue(), nbOfPost);
    }


    // Tests for named queries


    @Test
    public void testShouldGetAllUsers() throws Exception {
        // ARRANGE
        int nbOfUsers = 5;

        for (int i = 0; i < nbOfUsers; i++) {
            User user = new User();
            user.setName("User" + i);

            assertTrue(persistInTransaction(user));
        }


        // ACT
        Query query = em.createNamedQuery(User.GET_ALL_USERS);
        List<User> usersFound = query.getResultList();


        // ASSERT
        // ...that number of users found equals nbOfUsers
        assertEquals(usersFound.size(), nbOfUsers);
    }

    @Test
    public void testShouldGetTotalUsers() throws Exception {
        // ARRANGE
        int nbOfUsers = 10;

        for (int i = 0; i < nbOfUsers; i++) {
            User user = new User();
            user.setName("User" + i);

            assertTrue(persistInTransaction(user));
        }


        // ACT
        Query query = em.createNamedQuery(User.GET_TOTAL_USERS);
        Long usersFound = (Long) query.getSingleResult();


        // ASSERT
        // ...that total users matches nbOfUsers
        assertEquals(usersFound.intValue(), nbOfUsers);
    }

    @Test
    public void testShouldGetTotalUsersPerCountry() throws Exception {
        // ARRANGE
        User user = new User();
        Address address = new Address();
        address.setCountry("Norway");
        user.setName("Tester");
        user.setAddress(address);

        assertTrue(persistInTransaction(user, address));


        // ACT
        Query query = em.createNamedQuery(User.GET_TOTAL_USERS_PER_COUNTRY);
        query.setParameter("country", "Norway");
        Long usersFound = (Long) query.getSingleResult();


        // ASSERT
        // ...that country are the same
        assertEquals(usersFound.intValue(), 1);
    }

    @Test
    public void testShouldGetTopPoster() throws Exception {
        // ARRANGE
        User user1 = new User();
        user1.setName("User1");

        User user2 = new User();
        user2.setName("User2");

        List<Post> user1Posts = getCollection(10, Post.class);
        List<Post> user2Posts = getCollection(50, Post.class);

        persistListInTransaction(user1Posts);
        persistListInTransaction(user2Posts);

        user1.setPosts(user1Posts);
        user2.setPosts(user2Posts);

        assertTrue(persistInTransaction(user1, user2));


        // ACT
        TypedQuery<User> query = em.createNamedQuery(User.TOP_X_POSTERS, User.class);
        query.setMaxResults(1);
        User userFound = query.getSingleResult();


        // ASSERT
        // ...that User2 have the most posts (20)
        assertEquals(user2.getName(), userFound.getName());
    }

    @Test
    public void testShouldGetTopCommenter() throws Exception {
        // ARRANGE
        User user1 = new User();
        user1.setName("User1");

        User user2 = new User();
        user2.setName("User2");

        List<Comment> user1Comments = getCollection(500, Comment.class);
        List<Comment> user2Comments = getCollection(100, Comment.class);

        persistListInTransaction(user1Comments);
        persistListInTransaction(user2Comments);

        user1.setComments(user1Comments);
        user2.setComments(user2Comments);

        assertTrue(persistInTransaction(user1, user2));


        // ACT
        TypedQuery<User> query = em.createNamedQuery(User.TOP_X_COMMENTERS, User.class);
        query.setMaxResults(1);
        User userFound = query.getSingleResult();


        // ASSERT
        // ...that User1 have the most comments (500)
        assertEquals(user1.getName(), userFound.getName());
    }


    // Helper methods


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

    private <T> void persistListInTransaction(List<T> list) {
        for (T t : list) {
            persistInTransaction(t);
        }
    }

    private <T> List<T> getCollection(int size, Class<T> type) {
        List<T> list = new ArrayList<>();

        try {
            for (int i = 0; i < size; i++) {
                list.add(type.newInstance());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            System.out.println("FAILED");
            fail();
        }

        return list;
    }

}