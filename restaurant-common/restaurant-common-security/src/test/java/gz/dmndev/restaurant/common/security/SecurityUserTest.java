package gz.dmndev.restaurant.common.security;

import static org.junit.jupiter.api.Assertions.*;

import gz.dmndev.restaurant.common.security.model.SecurityUser;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SecurityUserTest {

  @Test
  void shouldCreateSecurityUser() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("custom_claim", "value");

    SecurityUser user =
        SecurityUser.builder()
            .id("123")
            .username("testuser")
            .email("test@example.com")
            .name("Test User")
            .roles(Arrays.asList("admin", "user"))
            .claims(claims)
            .build();

    assertEquals("123", user.getId());
    assertEquals("testuser", user.getUsername());
    assertEquals("test@example.com", user.getEmail());
    assertEquals("Test User", user.getName());
    assertEquals(2, user.getRoles().size());
    assertTrue(user.getRoles().contains("admin"));
    assertTrue(user.getRoles().contains("user"));
    assertEquals(claims, user.getClaims());
  }

  @Test
  void shouldCreateAnonymousUser() {
    SecurityUser anonymousUser = SecurityUser.anonymous();

    assertEquals("anonymous", anonymousUser.getId());
    assertEquals("anonymous", anonymousUser.getUsername());
    assertEquals("anonymous", anonymousUser.getEmail());
    assertEquals("Anonymous User", anonymousUser.getName());
    assertTrue(anonymousUser.getRoles().contains("ROLE_ANONYMOUS"));
    assertEquals(Collections.emptyMap(), anonymousUser.getClaims());
  }

  @Test
  void shouldCheckIfUserHasRole() {
    SecurityUser user =
        SecurityUser.builder().id("123").roles(Arrays.asList("admin", "user")).build();

    assertTrue(user.hasRole("admin"));
    assertTrue(user.hasRole("user"));
    assertFalse(user.hasRole("manager"));

    // También debe manejar null
    SecurityUser userWithoutRoles = SecurityUser.builder().id("123").build();

    assertFalse(userWithoutRoles.hasRole("admin"));
  }
}
