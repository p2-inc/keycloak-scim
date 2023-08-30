package de.captaingoldfish.scim.sdk.keycloak.auth;

import de.captaingoldfish.scim.sdk.keycloak.audit.ScimAdminEventBuilder;
import de.captaingoldfish.scim.sdk.server.endpoints.authorize.Authorization;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.resources.admin.AdminAuth;

/**
 * author Pascal Knueppel <br>
 * created at: 05.02.2020 <br>
 * <br>
 * this class is simply used within this example to pass the keycloak session into the resource
 * handlers
 */
@Slf4j
@Data
public abstract class ScimAuthorization implements Authorization {

  /** the keycloak session that is passed to the resource endpoints */
  private final KeycloakSession keycloakSession;

  /** the current authentication of the client / user */
  private AdminAuth authResult;

  /**
   * used to inform the current {@link
   * de.captaingoldfish.scim.sdk.keycloak.scim.ScimKeycloakContext} object that an {@link
   * ScimAdminEventBuilder} object can be created if called
   */
  @Setter private Consumer<AdminAuth> adminAuthConsumer;

  public ScimAuthorization(KeycloakSession keycloakSession) {
    this.keycloakSession = keycloakSession;
  }

  /** only used for dedicated error messages */
  @Override
  public String getClientId() {
    return Optional.ofNullable(authResult)
        .map(AdminAuth::getClient)
        .map(ClientModel::getClientId)
        .orElse("[unknown]");
  }

  /** this can be used if authorization on endpoint level is desirable */
  @Override
  public Set<String> getClientRoles() {
    return Optional.ofNullable(authResult)
        .map(AdminAuth::getUser)
        .map(UserModel::getRealmRoleMappingsStream)
        .map(realmRoles -> realmRoles.map(RoleModel::getName).collect(Collectors.toSet()))
        .orElse(Collections.emptySet());
  }

  /** authenticates the user */
  @Override
  public abstract boolean authenticate(
      Map<String, String> httpHeaders, Map<String, String> queryParams);

  /** {@inheritDoc} */
  @Override
  public String getRealm() {
    return keycloakSession.getContext().getRealm().getName();
  }
}
