package ejb;

import entity.Post;
import entity.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Stateless
public class UserEJB {

    // Dependency injection: The container will add it
    @PersistenceContext
    private EntityManager em;


    public UserEJB() {}


    public Long registerNewUser(String name, String surname, @NotNull String email) {
        if (isRegistered(email)) {
            return null;
        }

        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setDateOfRegistration(new Date());

        em.persist(user);
        return user.getId();
    }

    public boolean createPost(long userId, String text) {
        User u = em.find(User.class, userId);

        if (u != null) {
            Post post = new Post();
            post.setText(text);
            post.setCreatedAt(new Date());

            u.getPosts().add(post);

            em.persist(post);
            return true;
        }
        return false;
    }

    public int getTotalPosts(long userId) {
        Query query = em.createQuery("select u.posts.size from User u where u.id = :userId");
        query.setParameter("userId", userId);
        int n = (int) query.getSingleResult();

        return n;
    }

    public boolean isRegistered(@NotNull String email) {
        Query query = em.createQuery("select u from User u where u.email = :email");
        query.setParameter("email", email);

        User user;
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            user = null;
        }

        return user != null;
    }

    public long getNumberOfUsers() {
        Query query = em.createNamedQuery(User.GET_TOTAL_USERS);
        long n = (Long) query.getSingleResult();

        return n;
    }

    public List<String> getUserCountries() {
        Query query = em.createNamedQuery(User.GET_USER_COUNTRIES);
        List<String> data = query.getResultList();

        return data;
    }
}
