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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScimConfigProviderFactory implements UserStorageProviderFactory<ScimConfigProvider> {
  
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
  public String getHelpText() {
    return "SCIM v2";
  }

  @Override
  public void onCreate​(KeycloakSession session, RealmModel realm, ComponentModel model) {
    log.info("ScimConfigProviderFactory onCreate");
    ConfigurationProperties config = new ConfigurationProperties(model);

    config.setScimUrl(getScimUrl(session, realm, model));

    updateBearerToken(model);
    
    realm.updateComponent(model);
  }

  @Override
  public void onUpdate​(KeycloakSession session, RealmModel realm, ComponentModel oldModel, ComponentModel newModel) {
    log.info("ScimConfigProviderFactory onUpdate");
    boolean doUpdate = updateBearerToken(newModel);

    ConfigurationProperties oldConfig = new ConfigurationProperties(oldModel);
    ConfigurationProperties newConfig = new ConfigurationProperties(newModel);

    if (!oldConfig.getScimUrl().equals(newConfig.getScimUrl())) {
      newConfig.setScimUrl(oldConfig.getScimUrl());
      doUpdate = true;
    }
    
    if (doUpdate) {
      realm.updateComponent(newModel);
    }
  }

  String getScimUrl(KeycloakSession session, RealmModel realm, ComponentModel model) {
    String authServerUrl = session.getContext().getAuthServerUrl().toString();
    if (!authServerUrl.endsWith("/"))
      authServerUrl = authServerUrl + "/";
    String url = String.format("%srealms/%s/scim/%s/v2", authServerUrl, realm.getName(), model.getId());
    log.debug("Setting SCIM url to {}", url);
    return url;
  }

  boolean updateBearerToken(ComponentModel model) {
    ConfigurationProperties config = new ConfigurationProperties(model);
    String bearerToken = config.getBearerToken();
    if (Strings.isNullOrEmpty(bearerToken) || config.isRegenerateBearerToken()) {
      log.info("Bearer token refresh requested");
      config.setBearerToken(KeycloakModelUtils.generateCodeSecret());
      config.setRegenerateBearerToken(false);
      return true;
    }
    return false;
  }
}
