package de.captaingoldfish.scim.sdk.keycloak.scim;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import de.captaingoldfish.scim.sdk.common.resources.ServiceProvider;
import de.captaingoldfish.scim.sdk.keycloak.entities.ScimResourceTypeEntity;
import de.captaingoldfish.scim.sdk.keycloak.scim.handler.GroupHandler;
import de.captaingoldfish.scim.sdk.keycloak.scim.handler.UserHandler;
import de.captaingoldfish.scim.sdk.keycloak.services.ScimResourceTypeService;
import de.captaingoldfish.scim.sdk.keycloak.services.ScimServiceProviderService;
import de.captaingoldfish.scim.sdk.server.endpoints.ResourceEndpoint;
import de.captaingoldfish.scim.sdk.server.endpoints.base.GroupEndpointDefinition;
import de.captaingoldfish.scim.sdk.server.endpoints.base.UserEndpointDefinition;
import de.captaingoldfish.scim.sdk.server.schemas.ResourceType;
import de.captaingoldfish.scim.sdk.server.schemas.custom.ResourceTypeFeatures;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * @author Pascal Knueppel
 * @since 04.02.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScimConfiguration
{

  public static ResourceEndpoint getScimEndpoint(KeycloakSession keycloakSession, String componentId) {
    ComponentModel model = keycloakSession.getContext().getRealm().getComponent(componentId);
    return getScimEndpoint(keycloakSession, model);
  }

  public static ResourceEndpoint getScimEndpoint(KeycloakSession keycloakSession, ComponentModel model) {
    return createNewResourceEndpoint(keycloakSession, model);
  }

  /**
   * creates a new resource endpoint for the current realm
   */
  private static ResourceEndpoint createNewResourceEndpoint(KeycloakSession keycloakSession, ComponentModel model)
  {
    ScimServiceProviderService scimServiceProviderService = new ScimServiceProviderService(keycloakSession, model);
    ServiceProvider serviceProvider = scimServiceProviderService.getServiceProvider();
    ResourceEndpoint resourceEndpoint = new ResourceEndpoint(serviceProvider);

    ScimResourceTypeService resourceTypeService = new ScimResourceTypeService(keycloakSession, model);

    ResourceType userResourceType = resourceEndpoint.registerEndpoint(new UserEndpointDefinition(new UserHandler()));
    userResourceType.setFeatures(ResourceTypeFeatures.builder().autoFiltering(true).autoSorting(true).build());
    ScimResourceTypeEntity userResourceTypeEntity = resourceTypeService.getOrCreateResourceTypeEntry(userResourceType);
    resourceTypeService.updateResourceType(userResourceType, userResourceTypeEntity);

    ResourceType groupResourceType = resourceEndpoint.registerEndpoint(new GroupEndpointDefinition(new GroupHandler()));
    groupResourceType.setFeatures(ResourceTypeFeatures.builder().autoFiltering(true).autoSorting(true).build());
    ScimResourceTypeEntity groupResourceTypeEntity = resourceTypeService.getOrCreateResourceTypeEntry(groupResourceType);
    resourceTypeService.updateResourceType(groupResourceType, groupResourceTypeEntity);

    /*
    ResourceType roleResourceType = resourceEndpoint.registerEndpoint(new RoleEndpointDefinition(new RealmRoleHandler()));
    roleResourceType.setFeatures(ResourceTypeFeatures.builder().autoFiltering(true).autoSorting(true).build());
    ScimResourceTypeEntity roleResourceTypeEntity = resourceTypeService.getOrCreateResourceTypeEntry(roleResourceType);
    resourceTypeService.updateResourceType(roleResourceType, roleResourceTypeEntity);
    */
    return resourceEndpoint;
  }

}
