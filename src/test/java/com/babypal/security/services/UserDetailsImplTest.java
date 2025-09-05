package com.babypal.security.services;

import com.babypal.models.AppRole;
import com.babypal.models.Role;
import com.babypal.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    private User enabledUser;
    private User disabledUser;
    private User expiredUser;
    private User lockedUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setRoleName(AppRole.ROLE_USER);

        enabledUser = User.builder()
                .userId(1L)
                .userName("enableduser")
                .email("enabled@example.com")
                .password("password")
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
                .email("disabled@example.com")
                .password("password")
                .enabled(false)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .isTwoFactorEnabled(false)
                .role(userRole)
                .build();

        expiredUser = User.builder()
                .userId(3L)
                .userName("expireduser")
                .email("expired@example.com")
                .password("password")
                .enabled(true)
                .accountNonExpired(false)
                .accountNonLocked(true)
                .credentialsNonExpired(false)
                .isTwoFactorEnabled(true)
                .role(userRole)
                .build();

        lockedUser = User.builder()
                .userId(4L)
                .userName("lockeduser")
                .email("locked@example.com")
                .password("password")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(false)
                .credentialsNonExpired(true)
                .isTwoFactorEnabled(false)
                .role(userRole)
                .build();
    }

    @Test
    void build_ShouldReturnUserDetailsWithCorrectEnabledStatus_WhenUserIsEnabled() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(enabledUser);

        assertNotNull(userDetails);
        assertEquals("enableduser", userDetails.getUsername());
        assertEquals("enabled@example.com", userDetails.getEmail());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertFalse(userDetails.is2faEnabled());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void build_ShouldReturnUserDetailsWithCorrectDisabledStatus_WhenUserIsDisabled() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(disabledUser);

        assertNotNull(userDetails);
        assertEquals("disableduser", userDetails.getUsername());
        assertFalse(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void build_ShouldReturnUserDetailsWithCorrectAccountStatus_WhenUserHasVariousRestrictions() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(expiredUser);

        assertNotNull(userDetails);
        assertEquals("expireduser", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        assertFalse(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertFalse(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.is2faEnabled());
    }

    @Test
    void build_ShouldReturnUserDetailsWithCorrectLockedStatus_WhenUserIsLocked() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(lockedUser);

        assertNotNull(userDetails);
        assertEquals("lockeduser", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertFalse(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertFalse(userDetails.is2faEnabled());
    }
}