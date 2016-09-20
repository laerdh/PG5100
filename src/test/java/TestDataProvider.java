import jpa.Address;
import jpa.Comment;
import jpa.Post;
import jpa.User;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TestDataProvider {
    // jpa.User settings
    private static final String TESTER_NAME = "Tester";
    private static final String TESTER_SURNAME = "Test";
    private static final String TESTER_EMAIL = "test@test.com";

    // jpa.Post settings
    private static final String POST_TEXT = "Test post";

    // jpa.Comment settings
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
