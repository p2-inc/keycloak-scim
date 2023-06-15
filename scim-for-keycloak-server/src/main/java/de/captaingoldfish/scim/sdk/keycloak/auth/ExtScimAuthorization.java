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
import org.keycloak.services.resources.admin.AdminAuth;

import de.captaingoldfish.scim.sdk.keycloak.audit.ScimAdminEventBuilder;
import de.captaingoldfish.scim.sdk.server.endpoints.authorize.Authorization;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


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
  private final KeycloakSession keycloakSession;

  public ExtScimAuthorization(KeycloakSession keycloakSession) {
    super(null, null);
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
    //todo check the component model for the correct bearer token for this id
    return true;
  }

  @Override
  public String getRealm() {
    return keycloakSession.getContext().getRealm().getName();
  }
}
