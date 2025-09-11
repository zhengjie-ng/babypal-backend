package com.babypal.controllers;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.babypal.models.AppRole;
import com.babypal.models.Role;
import com.babypal.models.User;
import com.babypal.repositories.RoleRepository;
import com.babypal.repositories.UserRepository;
import com.babypal.security.jwt.JwtUtils;
import com.babypal.security.request.LoginRequest;
import com.babypal.security.request.SignupRequest;
import com.babypal.security.response.LoginResponse;
import com.babypal.security.response.MessageResponse;
import com.babypal.security.response.UserInfoResponse;
import com.babypal.security.services.UserDetailsImpl;
import com.babypal.services.LogService;
import com.babypal.services.TotpService;
import com.babypal.services.UserService;
import com.babypal.util.AuthUtil;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserService userService;

    @Autowired
    LogService logService;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    TotpService totpService;

    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            // Try to get user ID for failed login, but it might not exist
            User user = userRepository.findByUserName(loginRequest.getUsername()).orElse(null);
            Long userId = user != null ? user.getUserId() : null;
            logService.logSignInFailure(loginRequest.getUsername(), userId);
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        // set the authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // Collect roles from the UserDetails
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Get user entity for logging
        User user = userService.findByUsername(userDetails.getUsername());

        // Log successful sign in
        logService.logSignInSuccess(userDetails.getUsername(), user.getUserId());

        // Prepare the response body, now including the JWT token directly in the body
        LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken);

        // Return the response entity with the JWT token included in the response body
        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Role role;

        if (strRoles == null || strRoles.isEmpty()) {
            role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } else {
            String roleStr = strRoles.iterator().next();
            if (roleStr.equals("admin")) {
                role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            } else {
                role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            }
        }

        // Set default account settings for all new users
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.of(2125, 12, 31));
        user.setAccountExpiryDate(LocalDate.of(2125, 12, 31));
        user.setTwoFactorEnabled(false);
        user.setSignUpMethod("email");
        user.setRole(role);
        User savedUser = userRepository.save(user);

        // Log successful sign up
        logService.logSignUp(savedUser.getUserName(), savedUser.getUserId());

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.isTwoFactorEnabled(),
                roles);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/username")
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsername() : "";
    }

    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            userService.generatePasswordResetToken(email);
            return ResponseEntity.ok(new MessageResponse(
                    "Password reset token generated and email sent if the email exists in our system."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new MessageResponse("Password has been reset successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User user = userService.findByUsername(userDetails.getUsername());
            logService.logSignOut(userDetails.getUsername(), user.getUserId());
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(new MessageResponse("You've been signed out successfully"));
    }

    // 2FA Authentication
    @PostMapping("/enable-2fa")
    public ResponseEntity<String> enable2FA() {
        Long userId = authUtil.loggedInUserId();
        GoogleAuthenticatorKey secret = userService.generate2FASecret(userId);
        String qrCodeUrl = totpService.getQrCodeUrl(secret,
                userService.getUserById(userId).getUserName());
        
        // Note: Actual enabling happens in verify-2fa endpoint
        return ResponseEntity.ok(qrCodeUrl);
    }

    @PostMapping("/disable-2fa")
    public ResponseEntity<String> disable2FA() {
        Long userId = authUtil.loggedInUserId();
        User user = authUtil.loggedInUser();
        userService.disable2FA(userId);
        
        // Log 2FA disable
        logService.logTwoFactorDisable(user.getUserName(), userId);
        
        return ResponseEntity.ok("2FA disabled");
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<String> verify2FA(@RequestParam int code) {
        Long userId = authUtil.loggedInUserId();
        User user = authUtil.loggedInUser();
        boolean isValid = userService.validate2FACode(userId, code);
        
        // Log 2FA verification attempt
        logService.logTwoFactorVerification(user.getUserName(), userId, isValid);
        
        if (isValid) {
            userService.enable2FA(userId);
            // Log 2FA enable after successful verification
            logService.logTwoFactorEnable(user.getUserName(), userId);
            return ResponseEntity.ok("2FA Verified");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid 2FA Code");
        }
    }

    @GetMapping("/user/2fa-status")
    public ResponseEntity<?> get2FAStatus() {
        User user = authUtil.loggedInUser();
        if (user != null) {
            return ResponseEntity.ok().body(Map.of("is2faEnabled", user.isTwoFactorEnabled()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }

    @PostMapping("/public/verify-2fa-login")
    public ResponseEntity<String> verify2FALogin(@RequestParam int code,
            @RequestParam String jwtToken) {
        String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        User user = userService.findByUsername(username);
        boolean isValid = userService.validate2FACode(user.getUserId(), code);
        
        // Log 2FA login verification attempt
        logService.logTwoFactorVerification(username, user.getUserId(), isValid);
        
        if (isValid) {
            return ResponseEntity.ok("2FA Verified");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid 2FA Code");
        }
    }

    @PostMapping("/update-credentials")
    public ResponseEntity<?> updateCredentials(
            @RequestParam String token,
            @RequestParam String newEmail,
            @RequestParam String newPassword) {
        try {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Invalid token"));
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("User not found"));
            }

            // Check if email is already taken by another user
            if (userRepository.existsByEmail(newEmail)) {
                User existingUser = userRepository.findByEmail(newEmail).orElse(null);
                if (existingUser != null && !existingUser.getUserId().equals(user.getUserId())) {
                    return ResponseEntity.badRequest()
                            .body(new MessageResponse("Email is already in use by another user"));
                }
            }

            // Update email
            userService.updateEmail(user.getUserId(), newEmail);

            // Update password (encode it first)
            String encodedPassword = encoder.encode(newPassword);
            userService.updatePassword(user.getUserId(), encodedPassword);

            // Update credentials expiry date to current date + 1 year
            LocalDate newExpiryDate = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDate().plusYears(1);
            userService.updateCredentialsExpiryDate(user.getUserId(), newExpiryDate);

            // Log credential update
            logService.logCredentialsUpdate(username, user.getUserId());

            return ResponseEntity.ok(new MessageResponse("Credentials updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to update credentials: " + e.getMessage()));
        }
    }

}