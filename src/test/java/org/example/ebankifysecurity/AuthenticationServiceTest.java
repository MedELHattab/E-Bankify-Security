package org.example.ebankifysecurity;

import jakarta.mail.MessagingException;
import org.example.ebankifysecurity.dto.LoginUserDto;
import org.example.ebankifysecurity.dto.RegisterUserDto;
import org.example.ebankifysecurity.dto.VerifyUserDto;
import org.example.ebankifysecurity.model.User;
import org.example.ebankifysecurity.repository.UserRepository;
import org.example.ebankifysecurity.service.AuthenticationService;
import org.example.ebankifysecurity.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterUserDto registerUserDto;
    private LoginUserDto loginUserDto;
    private VerifyUserDto verifyUserDto;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registerUserDto = new RegisterUserDto();
        registerUserDto.setName("John Doe");
        registerUserDto.setEmail("johndoe@example.com");
        registerUserDto.setPassword("password123");
        registerUserDto.setAge(30);
        registerUserDto.setMonthlyIncome(5000.0);
        registerUserDto.setCreditScore(700);

        loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("johndoe@example.com");
        loginUserDto.setPassword("password123");

        verifyUserDto = new VerifyUserDto();
        verifyUserDto.setEmail("johndoe@example.com");
        verifyUserDto.setVerificationCode("123456");

        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe@example.com")
                .password("encodedPassword")
                .verificationCode("123456")
                .verificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15))
                .enabled(false)
                .build();
    }

    @Test
    void testSignup_ShouldReturnUser() throws MessagingException {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(registerUserDto.getPassword())).thenReturn("encodedPassword");

        // Act
        User createdUser = authenticationService.signup(registerUserDto);

        // Assert
        assertNotNull(createdUser);
        assertEquals("John Doe", createdUser.getName());
        assertEquals("johndoe@example.com", createdUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendVerificationEmail(eq(user.getEmail()), anyString(), anyString());
    }

    @Test
    void testAuthenticate_ShouldReturnUser_WhenAuthenticated() {
        // Arrange
        when(userRepository.findByEmail(loginUserDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginUserDto.getPassword(), user.getPassword())).thenReturn(true);

        // Act
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        // Assert
        assertNotNull(authenticatedUser);
        assertEquals("John Doe", authenticatedUser.getName());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void testAuthenticate_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(loginUserDto.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.authenticate(loginUserDto));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testVerifyUser_ShouldEnableUser_WhenCodeIsCorrect() {
        // Arrange
        when(userRepository.findByEmail(verifyUserDto.getEmail())).thenReturn(Optional.of(user));

        // Act
        authenticationService.verifyUser(verifyUserDto);

        // Assert
        assertTrue(user.isEnabled());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testVerifyUser_ShouldThrowException_WhenVerificationCodeIsIncorrect() {
        // Arrange
        verifyUserDto.setVerificationCode("wrongCode");
        when(userRepository.findByEmail(verifyUserDto.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.verifyUser(verifyUserDto));
        assertEquals("Invalid verification code", exception.getMessage());
    }

    @Test
    void testVerifyUser_ShouldThrowException_WhenVerificationCodeHasExpired() {
        // Arrange
        user.setVerificationCodeExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByEmail(verifyUserDto.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.verifyUser(verifyUserDto));
        assertEquals("Verification code has expired", exception.getMessage());
    }


    @Test
    void testResendVerificationCode_ShouldThrowException_WhenUserAlreadyVerified() {
        // Arrange
        user.setEnabled(true);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.resendVerificationCode(user.getEmail()));
        assertEquals("Account is already verified", exception.getMessage());
    }

    @Test
    void testResendVerificationCode_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.resendVerificationCode(user.getEmail()));
        assertEquals("User not found", exception.getMessage());
    }
}
