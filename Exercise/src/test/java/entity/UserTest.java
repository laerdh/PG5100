package entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.*;
import javax.validation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


@Ignore
public class UserTest {
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
    public void testEmptyUser() throws Exception {
        User user = new User();

        assertTrue(hasViolations(user));
        assertFalse(persistInTransaction(user));
    }

    @Test
    public void testValidUser() throws Exception {
        User user = TestDataProvider.getValidUser();

        assertFalse(hasViolations(user));
        assertTrue(persistInTransaction(user));
    }

    @Test
    public void testUserWithName() throws Exception {
        User user = TestDataProvider.getValidUser();

        assertTrue(persistInTransaction(user));

        User found = em.find(User.class, user.getId());

        assertNotNull(found.getId());
        assertEquals(user.getName(), found.getName());
    }

    @Test
    public void testNoName() throws Exception {
        User user = TestDataProvider.getValidUser();
        user.setName("");

        assertTrue(hasViolations(user));
        assertFalse(persistInTransaction(user));
    }

    @Test
    public void testLongFirstName() throws Exception {
        User user = TestDataProvider.getValidUser();
        user.setName(new String(new char[1_000]));

        assertTrue(hasViolations(user));
        assertFalse(persistInTransaction(user));
    }

    @Test
    public void testLongSurname() throws Exception {
        User user = TestDataProvider.getValidUser();
        user.setName(new String(new char[1_000]));

        assertTrue(hasViolations(user));
        assertFalse(persistInTransaction(user));
    }

    @Test
    public void testRegistrationInFuture() throws Exception {
        User user = TestDataProvider.getValidUser();
        user.setDateOfRegistration(Date.from(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        assertTrue(hasViolations(user));
        assertFalse(persistInTransaction(user));
    }

    @Test
    public void testInvalidEmail() throws Exception {
        User user = TestDataProvider.getValidUser();
        user.setEmail("test..test@test.com");

        assertTrue(hasViolations(user));
        assertFalse(persistInTransaction(user));

        user.setEmail("test@test.t");

        assertTrue(hasViolations(user));
        assertFalse(persistInTransaction(user));

        user.setEmail("t@t");

        assertTrue(hasViolations(user));
        assertFalse(persistInTransaction(user));
    }

    @Test
    public void testValidEmail() throws Exception {
        User user = TestDataProvider.getValidUser();
        user.setEmail("Test@test.com");

        assertFalse(hasViolations(user));
        assertTrue(persistInTransaction(user));
    }

    @Test
    public void testUserWithAddress() throws Exception {
        User user = TestDataProvider.getValidUser();

        user.getAddress().setCountry("Norway");
        user.getAddress().setCity("Oslo");
        user.getAddress().setPostcode((long) 484);

        assertTrue(persistInTransaction(user));
    }

    @Test
    public void testUserAddPost() throws Exception {
        User user = TestDataProvider.getValidUser();

        Post post = TestDataProvider.getValidPost();
        user.getPosts().add(post);

        assertTrue(persistInTransaction(user, post));
    }

    @Test
    public void testUserAddsSeveralPosts() throws Exception {
        // Arrange
        User user = TestDataProvider.getValidUser();

        assertTrue(persistInTransaction(user));

        int nbOfPost = 5;
        for (int i = 0; i < nbOfPost; i++) {
            Post post = TestDataProvider.getValidPost();
            user.getPosts().add(post);

            assertTrue(persistInTransaction(post));
        }


        // Act
        Query query = em.createNamedQuery(Post.GET_TOTAL_POSTS);
        Long postsFound = (Long) query.getSingleResult();


        // Assert
        assertEquals(postsFound.intValue(), nbOfPost);
    }

    @Test
    public void testShouldGetAllUsers() throws Exception {
        // Arrange
        int nbOfUsers = 5;

        for (int i = 0; i < nbOfUsers; i++) {
            User user = TestDataProvider.getValidUser();
            user.setName("User" + i);

            assertTrue(persistInTransaction(user));
        }


        // Act
        Query query = em.createNamedQuery(User.GET_ALL_USERS);
        List<User> usersFound = query.getResultList();


        // Assert
        // ...that number of users found equals nbOfUsers
        assertEquals(usersFound.size(), nbOfUsers);
    }

    @Test
    public void testShouldGetTotalUsers() throws Exception {
        // Arrange
        int nbOfUsers = 10;

        for (int i = 0; i < nbOfUsers; i++) {
            User user = TestDataProvider.getValidUser();
            user.setName("User" + i);

            assertTrue(persistInTransaction(user));
        }


        // Act
        Query query = em.createNamedQuery(User.GET_TOTAL_USERS);
        Long usersFound = (Long) query.getSingleResult();


        // Assert
        // ...that total users matches nbOfUsers
        assertEquals(usersFound.intValue(), nbOfUsers);
    }

    @Test
    public void testShouldGetUserCountries() throws Exception {
        // Arrange
        User user1 = TestDataProvider.getValidUser();
        user1.getAddress().setCountry("Norway");

        User user2 = TestDataProvider.getValidUser();
        user2.getAddress().setCountry("France");

        assertTrue(persistInTransaction(user1, user2));


        // Act
        Query query = em.createNamedQuery(User.GET_USER_COUNTRIES);
        List<String> countriesFound = query.getResultList();


        // Assert
        // ...that the countries match whats persisted to db
        assertTrue(countriesFound.contains(user1.getAddress().getCountry()));
        assertTrue(countriesFound.contains(user2.getAddress().getCountry()));
    }

    @Test
    public void testShouldGetTotalUsersPerCountry() throws Exception {
        // Arrange
        User user = TestDataProvider.getValidUser();
        user.getAddress().setCountry("Norway");

        assertTrue(persistInTransaction(user));


        // Act
        Query query = em.createNamedQuery(User.GET_TOTAL_USERS_PER_COUNTRY);
        query.setParameter("country", "Norway");
        Long usersFound = (Long) query.getSingleResult();


        // Assert
        // ...that country are the same
        assertEquals(usersFound.intValue(), 1);
    }

    @Test
    public void testShouldGetTopPoster() throws Exception {
        // Arrange
        User user1 = TestDataProvider.getValidUser();
        user1.setName("User1");

        User user2 = TestDataProvider.getValidUser();
        user2.setName("User2");

        List<Post> user1Posts = TestDataProvider.getCollection(10, Post.class)
                .stream()
                .peek(p -> p.setText("Test post"))
                .collect(Collectors.toList());

        List<Post> user2Posts = TestDataProvider.getCollection(50, Post.class)
                .stream()
                .peek(p -> p.setText("Test post2"))
                .collect(Collectors.toList());

        user1.setPosts(user1Posts);
        user2.setPosts(user2Posts);

        assertTrue(persistInTransaction(user1, user2));

        persistListInTransaction(user1Posts);
        persistListInTransaction(user2Posts);


        // Act
        TypedQuery<User> query = em.createNamedQuery(User.TOP_X_POSTERS, User.class);
        query.setMaxResults(1);
        User userFound = query.getSingleResult();


        // Assert
        // ...that User2 have the most posts (20)
        assertEquals(user2.getName(), userFound.getName());
    }

    @Test
    public void testShouldGetTopCommenter() throws Exception {
        // Arrange
        User user1 = TestDataProvider.getValidUser();
        user1.setName("User1");

        User user2 = TestDataProvider.getValidUser();
        user2.setName("User2");

        assertTrue(persistInTransaction(user1, user2));

        List<Comment> user1Comments = TestDataProvider.getCollection(500, Comment.class)
                .stream()
                .peek(c -> c.setText("Test comment"))
                .collect(Collectors.toList());

        List<Comment> user2Comments = TestDataProvider.getCollection(100, Comment.class)
                .stream()
                .peek(c -> c.setText("Test comment2"))
                .collect(Collectors.toList());

        persistListInTransaction(user1Comments);
        persistListInTransaction(user2Comments);

        user1.setComments(user1Comments);
        user2.setComments(user2Comments);

        assertTrue(persistInTransaction(user1, user2));


        // Act
        TypedQuery<User> query = em.createNamedQuery(User.TOP_X_COMMENTERS, User.class);
        query.setMaxResults(1);
        User userFound = query.getSingleResult();


        // Assert
        // ...that User1 have the most comments (500)
        assertEquals(user1.getName(), userFound.getName());
    }

    private <T> boolean hasViolations(T obj) {
        Set<ConstraintViolation<T>> violations = validator.validate(obj);

        for (ConstraintViolation<T> cv : violations) {
            System.out.println("VIOLATION: " + cv.toString());
        }

        return violations.size() > 0;
    }

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
}