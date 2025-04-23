package gz.dmndev.restaurant.commonsecurity.jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class KeycloakJwtAuthenticationConverter extends JwtAuthenticationConverter {

  private final JwtGrantedAuthoritiesConverter defaultConverter =
      new JwtGrantedAuthoritiesConverter();

  public KeycloakJwtAuthenticationConverter() {
    setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
    setPrincipalClaimName("preferred_username");
  }

  private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    return Stream.concat(defaultConverter.convert(jwt).stream(), extractKeycloakRoles(jwt).stream())
        .collect(Collectors.toSet());
  }

  @SuppressWarnings("unchecked")
  private Collection<GrantedAuthority> extractKeycloakRoles(Jwt jwt) {
    Map<String, Object> claims = jwt.getClaims();
    Map<String, Object> realmAccess =
        (Map<String, Object>) claims.getOrDefault("realm_access", Collections.emptyMap());
    Collection<String> roles =
        (Collection<String>) realmAccess.getOrDefault("roles", Collections.emptyList());

    return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
        .collect(Collectors.toSet());
  }
}
