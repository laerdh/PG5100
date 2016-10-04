package no.westerdals.pg5100.backend.ejb;

import no.westerdals.pg5100.backend.entity.Address;
import no.westerdals.pg5100.backend.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.security.SecureRandom;

@Stateless
public class UserEJB {

    @PersistenceContext
    private EntityManager em;


    public UserEJB() {}


    public boolean createUser(String email, String password, String firstName, String middleName, String lastName, String country) {
        if ((email == null || email.isEmpty()) && (password == null || password.isEmpty())) {
            return false;
        }

        User user = getUser(email);
        if (user != null) {
            computeHash(password, getSalt());

            return false;
        }

        User u = new User();
        String salt = getSalt();
        String hash = computeHash(password, salt);

        u.setEmail(email);
        u.setSalt(salt);
        u.setHash(hash);
        u.setFirstName(firstName);
        u.setMiddleName(middleName);
        u.setLastName(lastName);

        Address address = new Address();
        address.setCountry(country);
        u.setAddress(address);

        em.persist(u);

        return true;
    }

    public boolean login(String username, String password) {
        if ((username == null || username.isEmpty()) && (password == null || password.isEmpty())) {
            return false;
        }

        User user = getUser(username);
        if (user == null) {
            computeHash(password, getSalt());
            return false;
        }

        String hash = computeHash(password, user.getSalt());

        return hash.equals(user.getHash());
    }

    public User getUser(String email) {
        return em.find(User.class, email);
    }

    @NotNull
    protected String computeHash(String password, String salt) {
        String combined = password + salt;

        String hash = DigestUtils.sha256Hex(combined);

        return hash;
    }

    @NotNull
    protected String getSalt(){
        SecureRandom random = new SecureRandom();
        int bitsPerChar = 5;
        int twoPowerOfBits = 32; // 2^5
        int n = 26;
        assert n * bitsPerChar >= 128;

        String salt = new BigInteger(n * bitsPerChar, random).toString(twoPowerOfBits);
        return salt;
    }
}
