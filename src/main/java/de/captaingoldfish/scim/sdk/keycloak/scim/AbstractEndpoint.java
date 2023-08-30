package de.captaingoldfish.scim.sdk.keycloak.scim;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.KeycloakSession;

/**
 * @author Pascal Knueppel
 * @since 27.07.2020
 */
@Slf4j
public abstract class AbstractEndpoint {

  /** the current request context */
  @Getter(AccessLevel.PROTECTED)
  private final KeycloakSession keycloakSession;

  public AbstractEndpoint(KeycloakSession keycloakSession) {
    this.keycloakSession = keycloakSession;
  }
}
