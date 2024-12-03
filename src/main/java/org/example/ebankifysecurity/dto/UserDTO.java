
package org.example.ebankifysecurity.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.example.ebankifysecurity.annotation.UniqueEmail;
import org.example.ebankifysecurity.model.User;
import org.example.ebankifysecurity.model.role_enum;

@Data
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be at most 100 characters")
    @UniqueEmail  // Custom unique email validator
    private String email;

    @NotNull(message = "Password is required")
//    @Min(value = 6, message = "Password must be at least 6")
    private String password;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;

    @NotNull(message = "Monthly income is required")
    @PositiveOrZero(message = "Monthly income must be zero or positive")
    private Double monthlyIncome;

    @NotNull(message = "Credit score is required")
    @Min(value = 300, message = "Credit score must be at least 300")
    @Max(value = 850, message = "Credit score must be at most 850")
    private Integer creditScore;

    @NotNull(message = "Role is required")
    private role_enum role;

    // Convert methods fromEntity and toEntity
    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .age(user.getAge())
                .monthlyIncome(user.getMonthlyIncome())
                .creditScore(user.getCreditScore())
                .role(user.getRole())
                .build();
    }

    // Method for converting from DTO to User entity
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setAge(this.age);
        user.setMonthlyIncome(this.monthlyIncome);
        user.setCreditScore(this.creditScore);
        user.setRole(this.role);
        return user;
    }
}
