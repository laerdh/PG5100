package ejb;

import entity.Comment;
import entity.Post;
import entity.User;
import entity.UserTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.validation.ConstraintViolationException;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class CommentEJBTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, UserEJB.class.getPackage())
                .addPackages(true, User.class.getPackage())
                .addPackages(true, UserEJBTest.class.getPackage())
                .addPackages(true, UserTest.class.getPackage())
                .addAsResource("META-INF/persistence.xml");
    }

    @EJB
    private CommentEJB comment;

    @EJB
    private UserEJB user;

    @EJB
    private PostEJB post;

    @EJB
    private DeleterEJB deleter;

    private User u;
    private Post p;

    @Before
    public void initTestData() throws Exception {
        u = user.create("U1", "U1", "u1@test.com");
        p = post.create(u, "Test post");
    }

    @After
    public void emptyDatabase() throws Exception {
        deleter.deleteEntities(Comment.class);
        deleter.deleteEntities(Post.class);
        deleter.deleteEntities(User.class);
    }

    @Test
    public void testCreateValidComment() throws Exception {
        Comment c = comment.add(u, p, "Test comment");
        assertNotNull(c);
        assertNotNull(c.getId());
    }

    @Test
    public void testShouldNotCreateCommentWithNull() throws Exception {
        try {
            Comment c = comment.add(u, p, null);
            fail("Should throw ConstraintViolationException");
        } catch (EJBException e) {
            assertTrue(isConstraintViolation(e));
        }
    }

    @Test
    public void testShouldNotCreateCommentWithEmptyText() throws Exception {
        try {
            Comment c = comment.add(u, p, "");
            fail("Should throw ConstraintViolationException");
        } catch (EJBException e) {
            assertTrue(isConstraintViolation(e));
        }
    }

    @Test
    public void testShouldReturnNullWhenPostDontExist() throws Exception {
        Comment c = comment.add(u, null, "Test comment");
        assertNull(c);
    }

    @Test
    public void testShouldDeleteComment() throws Exception {
        Comment c = comment.add(u, p, "Test comment");
        assertNotNull(c);

        long actual = comment.delete(c.getId());
        int expected = 1;

        // Assert that 1 comment is deleted
        assertEquals(expected, actual);
    }

    @Test
    public void testShouldGetAllComments() throws Exception {
        int nbOfComments = 10;

        for (int i = 0; i < nbOfComments; i++) {
            Comment c = comment.add(u, p, "Test comment");
        }

        List<Comment> comments = comment.getAllComments();

        assertEquals(nbOfComments, comments.size());
    }

    @Test
    public void testShouldGetCommentsOnComment() throws Exception {
        int nbOfComments = 10;
        Comment c = comment.add(u, p, "Test comment");

        for (int i = 0; i < nbOfComments; i++) {
            Comment com = comment.addTo(u, c, "Test" + i);
            assertNotNull(com);
        }

        List<Comment> comments = comment.getCommentsOnComment(c.getId());

        // TODO
    }

    private boolean isConstraintViolation(Exception ex) {
        Throwable t = ex.getCause();

        while ((t != null) && !(t instanceof ConstraintViolationException)) {
            t = t.getCause();
        }
        return t instanceof ConstraintViolationException;
    }
}