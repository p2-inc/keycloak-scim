package de.captaingoldfish.scim.sdk.keycloak.provider;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProvider;

public class ScimConfigProvider implements UserStorageProvider {

  private static final Logger LOG = Logger.getLogger(ScimConfigProvider.class);
  private final KeycloakSession session;
  private final ComponentModel model;

  public ScimConfigProvider(KeycloakSession session, ComponentModel model) {
    this.session = session;
    this.model = model;
  }

  @Override
  public void close() {}
}
