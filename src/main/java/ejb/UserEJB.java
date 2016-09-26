package ejb;

import jpa.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Stateless
public class UserEJB {

    // Dependency injection: The container will add it
    @PersistenceContext
    private EntityManager em;


    public UserEJB() {}


    public User registerNewUser(String name, String surname, @NotNull String email) {
        if (isRegistered(email)) {
            return null;
        }

        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setDateOfRegistration(new Date());

        em.persist(user);
        return user;
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
}
