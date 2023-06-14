package de.captaingoldfish.scim.sdk.keycloak.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.storage.UserStorageProviderFactory;
import org.jboss.logging.Logger;
import java.util.List;

public class ScimConfigProviderFactory implements UserStorageProviderFactory<ScimConfigProvider> {
    private static final Logger LOG = Logger.getLogger(ScimConfigProviderFactory.class);

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ConfigurationProperties.getConfigProperties();
    }

    @Override
    public ScimConfigProvider create(KeycloakSession session, ComponentModel model) {
      return new ScimConfigProvider(session, model);
    }

  @Override
    public String getId() {
        return "ext-scim-config";
    }

  @Override
  public void onCreate​(KeycloakSession session, RealmModel realm, ComponentModel model) {
    String authServerUrl = session.getContext().getAuthServerUrl().toString();
    if (!authServerUrl.endsWith("/")) authServerUrl = authServerUrl + "/";
    String url = String.format("%srealms/%s/scim/%s/v2", authServerUrl, realm.getName(), model.getId());
    model.put(ConfigurationProperties.URL_PROPERTY, url);
    //    model.getConfig().put("TEST_PROPERTY", List.of(String.format("https://foo.com/%s/scim/v2", model.getId())));

    String bearerToken = model.get(ConfigurationProperties.BEARER_TOKEN_PROPERTY);
    if (Strings.isNullOrEmpty(bearerToken)) {
      bearerToken = KeycloakModelUtils.generateId();
    }
    model.put(ConfigurationProperties.BEARER_TOKEN_PROPERTY, bearerToken);
    
    realm.updateComponent(model);
  }

}
