package softuni.exam.util;

import org.springframework.stereotype.Component;

import jakarta.validation.Validator;

import jakarta.validation.Validation;

@Component
public class ValidationUtilImpl implements ValidationUtil {
    private final Validator validator ;

    public ValidationUtilImpl() {
        this.validator = Validation.buildDefaultValidatorFactory().
                getValidator();
    }

    @Override
    public <E> boolean isValid(E entity) {
        return this.validator.validate(entity).isEmpty();
    }
}
