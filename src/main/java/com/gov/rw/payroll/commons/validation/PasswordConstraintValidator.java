package com.gov.rw.payroll.commons.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;
import java.util.Arrays;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password cannot be null").addConstraintViolation();
            return false;
        }

        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                // Length between 8 and 30 characters
                new LengthRule(8, 30),
                // At least one uppercase letter
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // At least one lowercase letter
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // At least one digit
                new CharacterRule(EnglishCharacterData.Digit, 1),
                // At least one special character
                new CharacterRule(EnglishCharacterData.Special, 1),
                // No whitespace
                new WhitespaceRule(),
                // No repeated characters more than 3 times (e.g., "aaaa")
                new RepeatCharacterRegexRule(4)
        ));

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "Password must be 8-30 characters, include at least one uppercase letter, one lowercase letter, one digit, one special character, and no whitespace or sequences like 'abc', '123', or 'qwe'."
        ).addConstraintViolation();

        return false;
    }
}