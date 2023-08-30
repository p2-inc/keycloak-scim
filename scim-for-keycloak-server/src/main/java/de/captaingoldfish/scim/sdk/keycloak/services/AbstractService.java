package de.captaingoldfish.scim.sdk.keycloak.services;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;

/**
 * @author Pascal Knueppel
 * @since 02.08.2020
 */
public class AbstractService {

  /** the current request context */
  @Getter(AccessLevel.PROTECTED)
  private final KeycloakSession keycloakSession;

  public AbstractService(KeycloakSession keycloakSession) {
    this.keycloakSession = keycloakSession;
  }

  protected EntityManager getEntityManager() {
    return getKeycloakSession().getProvider(JpaConnectionProvider.class).getEntityManager();
  }
}
