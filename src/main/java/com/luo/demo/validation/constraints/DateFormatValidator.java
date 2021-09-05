package com.luo.demo.validation.constraints;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.format.DateTimeFormatter;

/**
 * Date Format validator
 *
 * @author luohq
 * @date 2021-09-05
 */
public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {

    private String format;

    @Override
    public void initialize(DateFormat dateFormat) {
        this.format = dateFormat.format();
    }

    @Override
    public boolean isValid(String dateStr, ConstraintValidatorContext cxt) {
        if (!StringUtils.hasText(dateStr)) {
            return true;
        }
        try {
            DateTimeFormatter.ofPattern(this.format).parse(dateStr);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }
}