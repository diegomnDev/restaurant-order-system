package gz.dmndev.restaurant.common.security.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecurityUser {
  private final String id;
  private final String username;
  private final String email;
  private final String name;
  private final List<String> roles;
  private final Map<String, Object> claims;

  public boolean hasRole(String role) {
    return roles != null && roles.contains(role);
  }

  public static SecurityUser anonymous() {
    return SecurityUser.builder()
        .id("anonymous")
        .username("anonymous")
        .email("anonymous")
        .name("Anonymous User")
        .roles(Collections.singletonList("ROLE_ANONYMOUS"))
        .claims(Collections.emptyMap())
        .build();
  }
}
