package no.westerdals.pg5100.backend.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@NotNull
@Constraint(validatedBy = CountryValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.ANNOTATION_TYPE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Country {

    String message() default "Invalid country";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
