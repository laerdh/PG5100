package entity;

import entity.Address;
import entity.Comment;
import entity.Post;
import entity.User;
import org.junit.Ignore;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


@Ignore
public class TestDataProvider {
    // entity.User settings
    private static final String TESTER_NAME = "Tester";
    private static final String TESTER_SURNAME = "Test";
    private static final String TESTER_EMAIL = "test@test.com";

    // entity.Post settings
    private static final String POST_TEXT = "Test post";

    // entity.Comment settings
    private static final String COMMENT_TEXT = "Test comment";


    static User getValidUser() {
        User user = new User();

        user.setName(TESTER_NAME);
        user.setSurname(TESTER_SURNAME);
        user.setEmail(TESTER_EMAIL);
        user.setAddress(new Address());
        user.setDateOfRegistration(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        return user;
    }

    static Post getValidPost() {
        Post post = new Post();
        post.setCreatedAt(Date.from(LocalDate.of(2010, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        post.setText(POST_TEXT);

        return post;
    }

    static Comment getValidComment() {
        Comment comment = new Comment();
        comment.setText(COMMENT_TEXT);

        return comment;
    }

    static <T> List<T> getCollection(int size, Class<T> type) throws InstantiationException, IllegalAccessException{
        List<T> data = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            data.add(type.newInstance());
        }

        return data;
    }
}
