package de.captaingoldfish.scim.sdk.keycloak.auth;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.ws.rs.NotAuthorizedException;

import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.component.ComponentModel;
import org.keycloak.services.resources.admin.AdminAuth;

import de.captaingoldfish.scim.sdk.keycloak.audit.ScimAdminEventBuilder;
import de.captaingoldfish.scim.sdk.server.endpoints.authorize.Authorization;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static de.captaingoldfish.scim.sdk.keycloak.provider.ConfigurationProperties.*;


/**
 * author Pascal Knueppel <br>
 * created at: 05.02.2020 <br>
 * <br>
 * this class is simply used within this example to pass the keycloak session into the resource handlers
 */
@Slf4j
@Data
public class ExtScimAuthorization extends ScimAuthorization
{
  private final String id;
  private final ComponentModel model;
  private final KeycloakSession keycloakSession;

  public ExtScimAuthorization(KeycloakSession keycloakSession, String id, ComponentModel model) {
    super(keycloakSession);
    this.id = id;
    this.model = model;
    this.keycloakSession = keycloakSession;
  }

  @Override
  public String getClientId() {
    return "[unknown]";
  }

  @Override
  public Set<String> getClientRoles() {
    return Collections.emptySet();
  }

  @Override
  public boolean authenticate(Map<String, String> httpHeaders, Map<String, String> queryParams) {
    String token = model.get(BEARER_TOKEN_PROPERTY);
    String header = httpHeaders.get("Authorization");
    if (header != null && header.toLowerCase().startsWith("bearer ")) {
      header = header.substring(7);
    }
    return (token != null && token.equals(header));
  }

  @Override
  public String getRealm() {
    return keycloakSession.getContext().getRealm().getName();
  }
}
