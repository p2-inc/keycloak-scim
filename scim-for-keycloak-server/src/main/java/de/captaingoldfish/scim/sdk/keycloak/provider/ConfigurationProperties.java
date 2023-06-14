package de.captaingoldfish.scim.sdk.keycloak.provider;

import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

import static org.keycloak.provider.ProviderConfigProperty.*;

public final class ConfigurationProperties {

  public static final String PROVIDER_NAME = "User migration using a REST client";
  public static final String URL_PROPERTY = "URL";
  public static final String BEARER_TOKEN_PROPERTY = "BEARER_TOKEN";

  private static ProviderConfigProperty getUrl() {
    ProviderConfigProperty test = new ProviderConfigProperty(URL_PROPERTY,
        "Generated URL",
        "SCIMv2 URL to be given to provider SCIM client",
        STRING_TYPE, null);
    test.setReadOnly(true);
    return test;
  }

  private static ProviderConfigProperty getBearerToken() {
    ProviderConfigProperty test = new ProviderConfigProperty(BEARER_TOKEN_PROPERTY,
                                                             "Bearer Token",
                                                             "Bearer token to be given to provider SCIM client. Will be generated if empty.",
                                                             STRING_TYPE, null);
    return test;
  }
  
    private static final List<ProviderConfigProperty> PROPERTIES = List.of(
        /*
        new ProviderConfigProperty(URI_PROPERTY,
                                   "Rest client URI (required)",
                                   "URI of the legacy system endpoints",
                                   STRING_TYPE, null),
        new ProviderConfigProperty(API_TOKEN_ENABLED_PROPERTY,
                                   "Rest client Bearer token auth enabled",
                                   "Enables Bearer token authentication for legacy user service",
                                   BOOLEAN_TYPE, false),
        new ProviderConfigProperty(API_TOKEN_PROPERTY,
                                   "Rest client Bearer token",
                                   "Bearer token",
                                   PASSWORD, null),
        new ProviderConfigProperty(GROUP_MAP_PROPERTY,
                                   "Legacy group conversion",
                                   "Group conversion in the format 'legacyGroup:newGroup'",
                                   MULTIVALUED_STRING_TYPE, null),
        */
        getUrl(), getBearerToken());

    private ConfigurationProperties() {
    }

    public static List<ProviderConfigProperty> getConfigProperties() {
        return PROPERTIES;
    }
}
