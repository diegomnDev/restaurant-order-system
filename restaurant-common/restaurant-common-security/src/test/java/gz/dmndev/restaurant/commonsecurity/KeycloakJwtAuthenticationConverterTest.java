package gz.dmndev.restaurant.commonsecurity;

import static org.junit.jupiter.api.Assertions.*;

import gz.dmndev.restaurant.commonsecurity.jwt.KeycloakJwtAuthenticationConverter;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class KeycloakJwtAuthenticationConverterTest {

  private KeycloakJwtAuthenticationConverter converter;
  private Jwt jwt;

  @BeforeEach
  void setUp() {
    converter = new KeycloakJwtAuthenticationConverter();

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
    realmAccess.put("roles", Collections.singletonList("user"));
    claims.put("realm_access", realmAccess);

    jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(300), headers, claims);
  }

  @Test
  void shouldExtractKeycloakRoles() {
    JwtAuthenticationToken authToken = (JwtAuthenticationToken) converter.convert(jwt);

    assertNotNull(authToken);
    assertEquals("testuser", authToken.getName());

    Collection<String> authorities =
        authToken.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    // Debe contener el rol de Keycloak con el prefijo "ROLE_"
    assertTrue(authorities.contains("ROLE_USER"));
  }

  @Test
  void shouldHandleJwtWithoutRoles() {
    Map<String, Object> headers = new HashMap<>();
    headers.put("alg", "RS256");

    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", "1234567890");
    claims.put("preferred_username", "testuser");

    Jwt jwtWithoutRoles =
        new Jwt("token", Instant.now(), Instant.now().plusSeconds(300), headers, claims);

    JwtAuthenticationToken authToken = (JwtAuthenticationToken) converter.convert(jwtWithoutRoles);

    assertNotNull(authToken);
    assertEquals("testuser", authToken.getName());

    // No debería fallar si faltan roles, simplemente no tendría esas autoridades
    assertNotNull(authToken.getAuthorities());
  }
}
