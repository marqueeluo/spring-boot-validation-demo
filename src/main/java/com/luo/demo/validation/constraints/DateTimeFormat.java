package com.luo.demo.validation.constraints;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The annotated {@code CharSequence} must match date time format.
 * The default date format is "yyyy-MM-dd HH:mm:ss".
 * Can override with property "format".
 * see {@link java.time.format.DateTimeFormatter}.
 * <p>
 * Accepts {@code CharSequence}. {@code null} elements are considered valid.
 *
 * @author luo
 * @date 2021-09-05
 */
@Documented
@Constraint(validatedBy = {})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@DateFormat
public @interface DateTimeFormat {
    /**
     * @return an overrider DateFormat.message
     */
    @OverridesAttribute(constraint = DateFormat.class, name = "message") String message() default "日期时间格式不正确";

    /**
     * @return an additional format the annotated dateTimeStr must match. The default is "yyyy-MM-dd HH:mm:ss"
     */
    @OverridesAttribute(constraint = DateFormat.class, name = "format") String format() default "yyyy-MM-dd HH:mm:ss";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}