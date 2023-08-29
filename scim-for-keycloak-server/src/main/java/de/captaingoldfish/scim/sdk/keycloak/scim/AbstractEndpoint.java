package de.captaingoldfish.scim.sdk.keycloak.scim;

import org.keycloak.models.KeycloakSession;

import de.captaingoldfish.scim.sdk.server.endpoints.ResourceEndpoint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Pascal Knueppel
 * @since 27.07.2020
 */
@Slf4j
public abstract class AbstractEndpoint
{

  /**
   * the current request context
   */
  @Getter(AccessLevel.PROTECTED)
  private final KeycloakSession keycloakSession;

  public AbstractEndpoint(KeycloakSession keycloakSession)
  {
    this.keycloakSession = keycloakSession;
  }

}
