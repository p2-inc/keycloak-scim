package de.captaingoldfish.scim.sdk.keycloak.provider;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.policy.PasswordPolicyManagerProvider;
import org.keycloak.policy.PolicyError;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ScimConfigProvider implements UserStorageProvider {

  private static final Logger LOG = Logger.getLogger(ScimConfigProvider.class);
  private final KeycloakSession session;
  private final ComponentModel model;
  
  public ScimConfigProvider(KeycloakSession session, ComponentModel model) {
    this.session = session;
    this.model = model;
  }
  
  @Override
  public void close() {
  }
}
