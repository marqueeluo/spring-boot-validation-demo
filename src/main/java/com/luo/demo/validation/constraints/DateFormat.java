package com.luo.demo.validation.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

/**
 * The annotated {@code CharSequence} must match date format.
 * The default date format is "yyyy-MM-dd".
 * Can override with property "format".
 * see {@link java.time.format.DateTimeFormatter}.
 * <p>
 * Accepts {@code CharSequence}. {@code null} elements are considered valid.
 *
 * @author luo
 * @date 2021-09-05
 */
@Documented
@Constraint(validatedBy = DateFormatValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ANNOTATION_TYPE,})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFormat {
    String message() default "日期格式不正确";

    String format() default "yyyy-MM-dd";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}