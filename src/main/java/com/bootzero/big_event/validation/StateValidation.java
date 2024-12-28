package com.bootzero.big_event.validation;

import com.bootzero.big_event.anno.State;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

/**
 * ClassName: StateValidation
 * Package: com.bootzero.big_event.validation
 * Description:
 *
 */
public class StateValidation implements ConstraintValidator<State,String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtils.hasLength(s)) {
            return false;
        }
        return s.equals("已发布") || s.equals("草稿");
    }
}
