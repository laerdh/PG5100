package entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.Assert.*;


@Ignore
public class CommentTest {
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
    public void testCreateEmptyCommentShouldFail() throws Exception {
        Comment comment = new Comment();

        assertTrue(hasViolations(comment));
        assertFalse(persistInTransaction(comment));
    }

    @Test
    public void testCreateCommentWithoutAuthorShouldFail() throws Exception {
        Comment comment = TestDataProvider.getValidComment();
        comment.setAuthor(null);

        assertTrue(hasViolations(comment));
        assertFalse(persistInTransaction(comment));
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