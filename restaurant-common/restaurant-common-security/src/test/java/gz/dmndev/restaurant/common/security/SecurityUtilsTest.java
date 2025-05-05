package gz.dmndev.restaurant.common.security;

import static org.junit.jupiter.api.Assertions.*;

import gz.dmndev.restaurant.common.security.model.SecurityUser;
import gz.dmndev.restaurant.common.security.util.SecurityUtils;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class SecurityUtilsTest {

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldGetCurrentUserFromJwtAuthentication() {
    // Preparar un JWT que simula la estructura de Keycloak
    Map<String, Object> headers = new HashMap<>();
    headers.put("alg", "RS256");

    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", "1234567890");
    claims.put("preferred_username", "testuser");
    claims.put("email", "test@example.com");
    claims.put("name", "Test User");

    // Simulación de la estructura específica de Keycloak para roles
    Map<String, Object> realmAccess = new HashMap<>();
    realmAccess.put("roles", Arrays.asList("user", "admin"));
    claims.put("realm_access", realmAccess);

    Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(300), headers, claims);

    JwtAuthenticationToken authentication =
        new JwtAuthenticationToken(
            jwt,
            Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")),
            "testuser");

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Probar SecurityUtils
    SecurityUser user = SecurityUtils.getCurrentUser();

    assertEquals("1234567890", user.getId());
    assertEquals("testuser", user.getUsername());
    assertEquals("test@example.com", user.getEmail());
    assertEquals("Test User", user.getName());
    assertTrue(user.getRoles().contains("user"));
    assertTrue(user.getRoles().contains("admin"));
    assertEquals(claims, user.getClaims());

    // Probar los métodos auxiliares
    assertEquals("1234567890", SecurityUtils.getCurrentUserId());
    assertTrue(SecurityUtils.hasRole("user"));
    assertTrue(SecurityUtils.hasRole("admin"));
    assertFalse(SecurityUtils.hasRole("manager"));
  }

  @Test
  void shouldReturnAnonymousUserWhenNotAuthenticated() {
    SecurityUser user = SecurityUtils.getCurrentUser();

    assertEquals("anonymous", user.getId());
    assertEquals("anonymous", user.getUsername());
    assertTrue(user.hasRole("ROLE_ANONYMOUS"));
  }
}
