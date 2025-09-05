package com.babypal.security;

import com.babypal.models.AppRole;
import com.babypal.models.Role;
import com.babypal.models.User;
import com.babypal.repositories.UserRepository;
import com.babypal.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisabledUserAuthenticationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private DaoAuthenticationProvider authenticationProvider;
    private PasswordEncoder passwordEncoder;
    private User enabledUser;
    private User disabledUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        userRole = new Role();
        userRole.setRoleName(AppRole.ROLE_USER);

        enabledUser = User.builder()
                .userId(1L)
                .userName("enableduser")
                .email("enabled@test.com")
                .password(passwordEncoder.encode("password123"))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .isTwoFactorEnabled(false)
                .role(userRole)
                .build();

        disabledUser = User.builder()
                .userId(2L)
                .userName("disableduser")
                .email("disabled@test.com")
                .password(passwordEncoder.encode("password123"))
                .enabled(false)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .isTwoFactorEnabled(false)
                .role(userRole)
                .build();
    }

    @Test
    void authenticateEnabledUser_ShouldSucceed_WhenCredentialsAreValid() {
        when(userRepository.findByUserName("enableduser")).thenReturn(Optional.of(enabledUser));

        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken("enableduser", "password123");

        assertDoesNotThrow(() -> {
            authenticationProvider.authenticate(authToken);
        });

        UserDetails userDetails = userDetailsService.loadUserByUsername("enableduser");
        assertTrue(userDetails.isEnabled());
        assertEquals("enableduser", userDetails.getUsername());
    }

    @Test
    void authenticateDisabledUser_ShouldThrowDisabledException_EvenWithValidCredentials() {
        when(userRepository.findByUserName("disableduser")).thenReturn(Optional.of(disabledUser));

        DisabledException exception = assertThrows(DisabledException.class, 
            () -> userDetailsService.loadUserByUsername("disableduser"));

        assertEquals("User account is disabled", exception.getMessage());
    }

    @Test
    void disabledUserCannotAuthenticate_ThroughAuthenticationProvider() {
        when(userRepository.findByUserName("disableduser")).thenReturn(Optional.of(disabledUser));

        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken("disableduser", "password123");

        InternalAuthenticationServiceException exception = assertThrows(InternalAuthenticationServiceException.class, () -> {
            authenticationProvider.authenticate(authToken);
        });

        assertTrue(exception.getCause() instanceof DisabledException);
        assertEquals("User account is disabled", exception.getCause().getMessage());
    }
}