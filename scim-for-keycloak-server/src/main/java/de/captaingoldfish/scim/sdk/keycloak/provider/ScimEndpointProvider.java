package de.captaingoldfish.scim.sdk.keycloak.provider;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

import de.captaingoldfish.scim.sdk.keycloak.auth.Authentication;
import de.captaingoldfish.scim.sdk.keycloak.scim.ScimEndpoint;
import lombok.extern.slf4j.Slf4j;


/**
 * author Pascal Knueppel <br>
 * created at: 04.02.2020 <br>
 * <br>
 * this class is the base rest provider that is instantiated by keycloak through the
 * {@link ScimEndpointProviderFactory}
 */
@Slf4j
public class ScimEndpointProvider implements RealmResourceProvider
{

  /**
   * the keycloak session holds information about the current authentication and the realm that we are currently
   * in
   */
  private final KeycloakSession keycloakSession;

  /**
   * standard constructor
   */
  public ScimEndpointProvider(KeycloakSession keycloakSession)
  {
    this.keycloakSession = keycloakSession;
  }

  /**
   * @return the REST-resource endpoint holder.
   */
  @Override
  public Object getResource()
  {
    log.info("getResource()");
    return new ScimEndpoint(keycloakSession, new Authentication());
  }

  @Override
  public void close()
  {
    // nothing to do here
  }
}
