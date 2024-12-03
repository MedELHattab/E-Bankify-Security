package org.example.ebankifysecurity.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.ebankifysecurity.annotation.UniqueEmail;
import org.example.ebankifysecurity.repository.UserManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private UserManagementRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return true; // @NotNull will handle null checks if applied on the field
        }  
        return !userRepository.existsByEmail(email);
    }
}
