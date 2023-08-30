package de.captaingoldfish.scim.sdk.keycloak.provider;

import static org.keycloak.provider.ProviderConfigProperty.*;

import java.util.List;
import org.keycloak.component.ComponentModel;
import org.keycloak.provider.ProviderConfigProperty;

public final class ConfigurationProperties {

  public static final String PROVIDER_NAME = "User migration using a REST client";
  public static final String ENABLED_PROPERTY = "ENABLED";
  public static final String URL_PROPERTY = "URL";
  public static final String BEARER_TOKEN_PROPERTY = "BEARER_TOKEN";
  public static final String REGENERATE_BEARER_TOKEN_PROPERTY = "REGENERATE_BEARER_TOKEN";
  public static final String FILTER_SUPPORTED_PROPERTY = "FILTER_SUPPORTED";
  public static final String FILTER_MAX_RESULTS_PROPERTY = "FILTER_MAX_RESULTS";
  public static final String SORT_SUPPORTED_PROPERTY = "SORT_SUPPORTED";
  public static final String PATCH_SUPPORTED_PROPERTY = "PATCH_SUPPORTED";
  public static final String ETAG_SUPPORTED_PROPERTY = "ETAG_SUPPORTED";
  public static final String CHANGE_PASSWORD_SUPPORTED_PROPERTY = "CHANGE_PASSWORD_SUPPORTED";
  public static final String BULK_SUPPORTED_PROPERTY = "BULK_SUPPORTED";
  public static final String BULK_MAX_OPERATIONS_PROPERTY = "BULK_MAX_OPERATIONS";
  public static final String BULK_MAX_PAYLOAD_SIZE_PROPERTY = "BULK_MAX_PAYLOAD_SIZE";

  private static ProviderConfigProperty getUrlProperty() {
    ProviderConfigProperty test =
        new ProviderConfigProperty(
            URL_PROPERTY,
            "Generated URL",
            "SCIMv2 URL to be given to provider SCIM client",
            STRING_TYPE,
            null);
    test.setReadOnly(true);
    return test;
  }

  private static ProviderConfigProperty getBearerTokenProperty() {
    ProviderConfigProperty test =
        new ProviderConfigProperty(
            BEARER_TOKEN_PROPERTY,
            "Bearer Token",
            "Bearer token to be given to provider SCIM client. Will be generated if empty or if Regenerate Bearer Token is enabled.",
            STRING_TYPE,
            null);
    test.setReadOnly(true);
    return test;
  }

  private static final List<ProviderConfigProperty> PROPERTIES =
      List.of(
          getUrlProperty(),
          getBearerTokenProperty(),
          new ProviderConfigProperty(
              REGENERATE_BEARER_TOKEN_PROPERTY,
              "Regenerate Bearer Token",
              "Clear previous bearer token and create a new one. WARNING! The previous token will stop working.",
              BOOLEAN_TYPE,
              "false"),
          new ProviderConfigProperty(
              FILTER_SUPPORTED_PROPERTY,
              "Filter Supported",
              "Filter Supported",
              BOOLEAN_TYPE,
              "true"),
          new ProviderConfigProperty(
              FILTER_MAX_RESULTS_PROPERTY,
              "Filter Max Results",
              "Filter Max Results",
              STRING_TYPE,
              "50"),
          new ProviderConfigProperty(
              SORT_SUPPORTED_PROPERTY, "Sort Supported", "Sort Supported", BOOLEAN_TYPE, "true"),
          new ProviderConfigProperty(
              PATCH_SUPPORTED_PROPERTY, "Patch Supported", "Patch Supported", BOOLEAN_TYPE, "true"),
          new ProviderConfigProperty(
              ETAG_SUPPORTED_PROPERTY, "Etag Supported", "Etag Supported", BOOLEAN_TYPE, "true"),
          new ProviderConfigProperty(
              CHANGE_PASSWORD_SUPPORTED_PROPERTY,
              "Change Password Supported",
              "Change Password Supported",
              BOOLEAN_TYPE,
              "false"),
          new ProviderConfigProperty(
              BULK_SUPPORTED_PROPERTY, "Bulk Supported", "Bulk Supported", BOOLEAN_TYPE, "true"),
          new ProviderConfigProperty(
              BULK_MAX_OPERATIONS_PROPERTY,
              "Bulk Max Operations",
              "Bulk Max Operations",
              STRING_TYPE,
              "15"),
          new ProviderConfigProperty(
              BULK_MAX_PAYLOAD_SIZE_PROPERTY,
              "Bulk Max Payload Size",
              "Bulk Max Payload Size",
              STRING_TYPE,
              "" + 2 * 1024 * 1024));

  public static List<ProviderConfigProperty> getConfigProperties() {
    return PROPERTIES;
  }

  private final ComponentModel model;

  public ConfigurationProperties(ComponentModel model) {
    this.model = model;
  }

  // enabled comes from UserStorageProviderSpi.commonConfig
  public boolean isEnabled() {
    return model.get(ENABLED_PROPERTY, true);
  }
  
  public String getScimUrl() {
    return model.get(URL_PROPERTY);
  }

  public void setScimUrl(String scimUrl) {
    model.put(URL_PROPERTY, scimUrl);
  }

  public String getBearerToken() {
    return model.get(BEARER_TOKEN_PROPERTY);
  }

  public void setBearerToken(String bearerToken) {
    model.put(BEARER_TOKEN_PROPERTY, bearerToken);
  }

  public boolean isRegenerateBearerToken() {
    return model.get(REGENERATE_BEARER_TOKEN_PROPERTY, false);
  }

  public void setRegenerateBearerToken(boolean regenerateBearerToken) {
    model.put(REGENERATE_BEARER_TOKEN_PROPERTY, regenerateBearerToken);
  }

  public boolean isFilterSupported() {
    return model.get(FILTER_SUPPORTED_PROPERTY, true);
  }

  public void setFilterSupported(boolean filterSupported) {
    model.put(FILTER_SUPPORTED_PROPERTY, filterSupported);
  }

  public int getFilterMaxResults() {
    return model.get(FILTER_MAX_RESULTS_PROPERTY, 50);
  }

  public void setFilterMaxResults(int filterMaxResults) {
    model.put(FILTER_MAX_RESULTS_PROPERTY, filterMaxResults);
  }

  public boolean isSortSupported() {
    return model.get(SORT_SUPPORTED_PROPERTY, true);
  }

  public void setSortSupported(boolean sortSupported) {
    model.put(SORT_SUPPORTED_PROPERTY, sortSupported);
  }

  public boolean isPatchSupported() {
    return model.get(PATCH_SUPPORTED_PROPERTY, true);
  }

  public void setPatchSupported(boolean patchSupported) {
    model.put(PATCH_SUPPORTED_PROPERTY, patchSupported);
  }

  public boolean isEtagSupported() {
    return model.get(ETAG_SUPPORTED_PROPERTY, true);
  }

  public void setEtagSupported(boolean etagSupported) {
    model.put(ETAG_SUPPORTED_PROPERTY, etagSupported);
  }

  public boolean isChangePasswordSupported() {
    return model.get(CHANGE_PASSWORD_SUPPORTED_PROPERTY, false);
  }

  public void setChangePasswordSupported(boolean changePasswordSupported) {
    model.put(CHANGE_PASSWORD_SUPPORTED_PROPERTY, changePasswordSupported);
  }

  public boolean isBulkSupported() {
    return model.get(BULK_SUPPORTED_PROPERTY, true);
  }

  public void setBulkSupported(boolean bulkSupported) {
    model.put(BULK_SUPPORTED_PROPERTY, bulkSupported);
  }

  public int getBulkMaxOperations() {
    return model.get(BULK_MAX_OPERATIONS_PROPERTY, 15);
  }

  public void setBulkMaxOperations(int bulkMaxOperations) {
    model.put(BULK_MAX_OPERATIONS_PROPERTY, bulkMaxOperations);
  }

  public long getBulkMaxPayloadSize() {
    return model.get(BULK_MAX_PAYLOAD_SIZE_PROPERTY, Long.MAX_VALUE);
  }

  public void setBulkMaxPayloadSize(int bulkMaxPayloadSize) {
    model.put(BULK_MAX_PAYLOAD_SIZE_PROPERTY, 2 * 1024 * 1024);
  }
}
