package com.babypal.config;

import com.babypal.models.AppRole;
import com.babypal.models.Role;
import com.babypal.models.User;
import com.babypal.repositories.RoleRepository;
import com.babypal.security.jwt.JwtUtils;
import com.babypal.security.services.UserDetailsImpl;
import com.babypal.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtUtils jwtUtils;

    @Autowired
    RoleRepository roleRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    String username;
    String idAttributeKey;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        
        // Use arrays to work around lambda final variable constraints
        final String[] emailHolder = {""};
        final String[] usernameHolder = {""};
        
        if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())
                || "google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();
            
            // Safely extract attributes with null checks
            Object emailObj = attributes.get("email");
            Object nameObj = attributes.get("name");
            
            String rawEmail = (emailObj != null) ? emailObj.toString() : "";
            String name = (nameObj != null) ? nameObj.toString() : "";
            if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                Object loginObj = attributes.get("login");
                username = (loginObj != null) ? loginObj.toString() : "";
                idAttributeKey = "id";
            } else if ("google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                username = rawEmail.contains("@") ? rawEmail.split("@")[0] : rawEmail;
                idAttributeKey = "sub";
            } else {
                username = "";
                idAttributeKey = "id";
            }
            
            // Handle case where email is empty (common with GitHub private emails)
            if (rawEmail.isEmpty()) {
                // For GitHub, we can use the username as a fallback identifier
                // Create a synthetic email using the username
                if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                    emailHolder[0] = username + "@github.local"; // Use a synthetic email
                } else {
                    throw new RuntimeException("Email is required for OAuth authentication");
                }
            } else {
                emailHolder[0] = rawEmail;
            }
            
            // Store username in method-level variable
            usernameHolder[0] = username;
            
            System.out.println("HELLO OAUTH: " + emailHolder[0] + " : " + name + " : " + usernameHolder[0]);

            userService.findByEmail(emailHolder[0])
                    .ifPresentOrElse(user -> {
                        System.out.println("EXISTING USER FOUND: " + user.getEmail() + " with role: " + user.getRole().getRoleName());
                        // Update OAuth login method for existing user if not already set
                        if (user.getSignUpMethod() == null || !user.getSignUpMethod().equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                            user.setSignUpMethod(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                            userService.registerUser(user); // Save the updated signup method
                        }
                        
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name())),
                                attributes,
                                idAttributeKey);
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name())),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    }, () -> {
                        System.out.println("CREATING NEW USER: " + emailHolder[0] + " / " + usernameHolder[0]);
                        // Create new user with default ROLE_USER
                        User newUser = new User();
                        Optional<Role> userRole = roleRepository.findByRoleName(AppRole.ROLE_USER);
                        if (userRole.isPresent()) {
                            newUser.setRole(userRole.get());
                            System.out.println("ASSIGNED ROLE: " + userRole.get().getRoleName());
                        } else {
                            throw new RuntimeException("Default role not found");
                        }
                        newUser.setEmail(emailHolder[0]);
                        newUser.setUserName(usernameHolder[0]);
                        newUser.setSignUpMethod(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                        User savedUser = userService.registerUser(newUser);
                        System.out.println("NEW USER CREATED WITH ID: " + savedUser.getUserId());
                        
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(newUser.getRole().getRoleName().name())),
                                attributes,
                                idAttributeKey);
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority(newUser.getRole().getRoleName().name())),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    });
        }
        this.setAlwaysUseDefaultTargetUrl(true);

        // JWT TOKEN LOGIC
        System.out.println("OAuth2LoginSuccessHandler JWT Generation: " + usernameHolder[0] + " : " + emailHolder[0]);

        // Get user (should exist now after provisioning logic above)
        // Use the same email variable that was used for user provisioning above
        User user = userService.findByEmail(emailHolder[0]).orElseThrow(
                () -> new RuntimeException("User not found after OAuth provisioning"));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getRoleName().name()));

        // Create UserDetailsImpl instance using the actual user data from database
        UserDetailsImpl userDetails = new UserDetailsImpl(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                user.isTwoFactorEnabled(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                authorities);

        // Generate JWT token
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // Redirect to the frontend with the JWT token
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("token", jwtToken)
                .build().toUriString();
        this.setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}