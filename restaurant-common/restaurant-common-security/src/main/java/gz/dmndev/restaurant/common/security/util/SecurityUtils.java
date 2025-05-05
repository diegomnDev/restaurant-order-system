package gz.dmndev.restaurant.common.security.util;

import gz.dmndev.restaurant.common.security.model.SecurityUser;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class SecurityUtils {

  private SecurityUtils() {
    // Utility class
  }

  public static SecurityUser getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return SecurityUser.anonymous();
    }

    if (authentication instanceof JwtAuthenticationToken) {
      return extractUserFromJwt((JwtAuthenticationToken) authentication);
    }

    // Fallback for other authentication types
    return SecurityUser.builder()
        .id(authentication.getName())
        .username(authentication.getName())
        .name(authentication.getName())
        .roles(
            authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
        .claims(Collections.emptyMap())
        .build();
  }

  @SuppressWarnings("unchecked")
  private static SecurityUser extractUserFromJwt(JwtAuthenticationToken jwtAuthentication) {
    Map<String, Object> claims = jwtAuthentication.getToken().getClaims();

    List<String> roles = Collections.emptyList();
    if (claims.containsKey("realm_access")) {
      Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
      if (realmAccess.containsKey("roles")) {
        roles = (List<String>) realmAccess.get("roles");
      }
    }

    String userId = (String) claims.getOrDefault("sub", "");
    String username = (String) claims.getOrDefault("preferred_username", "");
    String email = (String) claims.getOrDefault("email", "");
    String name = (String) claims.getOrDefault("name", "");

    return SecurityUser.builder()
        .id(userId)
        .username(username)
        .email(email)
        .name(name)
        .roles(roles)
        .claims(claims)
        .build();
  }

  public static String getCurrentUserId() {
    return getCurrentUser().getId();
  }

  public static boolean hasRole(String role) {
    return getCurrentUser().hasRole(role);
  }
}
