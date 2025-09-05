package com.babypal.security.services;

import com.babypal.models.AppRole;
import com.babypal.models.Role;
import com.babypal.models.User;
import com.babypal.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User enabledUser;
    private User disabledUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setRoleName(AppRole.ROLE_USER);

        enabledUser = User.builder()
                .userId(1L)
                .userName("testuser")
                .email("test@example.com")
                .password("password")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .role(userRole)
                .build();

        disabledUser = User.builder()
                .userId(2L)
                .userName("disableduser")
                .email("disabled@example.com")
                .password("password")
                .enabled(false)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .role(userRole)
                .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExistsAndEnabled() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(enabledUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_ShouldThrowDisabledException_WhenUserExistsButDisabled() {
        when(userRepository.findByUserName("disableduser")).thenReturn(Optional.of(disabledUser));

        DisabledException exception = assertThrows(DisabledException.class, 
            () -> userDetailsService.loadUserByUsername("disableduser"));

        assertEquals("User account is disabled", exception.getMessage());
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername("nonexistentuser"));

        assertTrue(exception.getMessage().contains("User Not Found with username: nonexistentuser"));
    }
}