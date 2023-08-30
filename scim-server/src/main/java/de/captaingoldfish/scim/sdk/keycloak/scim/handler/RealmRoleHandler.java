package de.captaingoldfish.scim.sdk.keycloak.scim.handler;

import de.captaingoldfish.scim.sdk.common.constants.AttributeNames;
import de.captaingoldfish.scim.sdk.common.constants.ResourceTypeNames;
import de.captaingoldfish.scim.sdk.common.constants.enums.SortOrder;
import de.captaingoldfish.scim.sdk.common.exceptions.ConflictException;
import de.captaingoldfish.scim.sdk.common.exceptions.InternalServerException;
import de.captaingoldfish.scim.sdk.common.exceptions.ResourceNotFoundException;
import de.captaingoldfish.scim.sdk.common.resources.complex.Meta;
import de.captaingoldfish.scim.sdk.common.schemas.SchemaAttribute;
import de.captaingoldfish.scim.sdk.keycloak.audit.ScimAdminEventBuilder;
import de.captaingoldfish.scim.sdk.keycloak.custom.resources.ChildRole;
import de.captaingoldfish.scim.sdk.keycloak.custom.resources.RealmRole;
import de.captaingoldfish.scim.sdk.keycloak.custom.resources.RoleAssociate;
import de.captaingoldfish.scim.sdk.keycloak.scim.ScimKeycloakContext;
import de.captaingoldfish.scim.sdk.keycloak.services.RoleService;
import de.captaingoldfish.scim.sdk.server.endpoints.Context;
import de.captaingoldfish.scim.sdk.server.endpoints.ResourceHandler;
import de.captaingoldfish.scim.sdk.server.filter.FilterNode;
import de.captaingoldfish.scim.sdk.server.response.PartialListResponse;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RoleMapperModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;

/**
 * the SCIM handler implementation for roles
 *
 * @author Pascal Knueppel
 * @since 16.08.2020
 */
@Slf4j
public class RealmRoleHandler extends ResourceHandler<RealmRole> {

  /**
   * maps an associate into a SCIM representation
   *
   * @param roleMapperModel the associated resource
   * @return the SCIM representation of a resource reference
   */
  protected static RoleAssociate toAssociate(RoleMapperModel roleMapperModel) {
    String id;
    String type;
    String displayName;
    if (roleMapperModel instanceof UserModel) {
      id = ((UserModel) roleMapperModel).getId();
      type = ResourceTypeNames.USER;
      displayName = ((UserModel) roleMapperModel).getUsername();
    } else if (roleMapperModel instanceof GroupModel) {
      id = ((GroupModel) roleMapperModel).getId();
      type = ResourceTypeNames.GROUPS;
      displayName = ((GroupModel) roleMapperModel).getName();
    } else {
      throw new InternalServerException(
          "could not resolve role member of type '" + roleMapperModel.getClass() + "'");
    }
    return RoleAssociate.builder().type(type).display(displayName).value(id).build();
  }

  /** maps the given role into a {@link ChildRole} object */
  protected static ChildRole toChildRole(RoleModel roleModel) {
    boolean isClientRole = roleModel.isClientRole();
    String type = isClientRole ? null : RealmRole.RESOURCE_NAME;
    return ChildRole.builder()
        .type(type)
        .value(roleModel.getId())
        .display(roleModel.getName())
        .clientRole(isClientRole)
        .build();
  }

  /** {@inheritDoc} */
  @Override
  public RealmRole createResource(RealmRole resource, Context context) {
    KeycloakSession keycloakSession = ((ScimKeycloakContext) context).getKeycloakSession();
    RoleModel roleModel =
        keycloakSession
            .roles()
            .getRealmRole(keycloakSession.getContext().getRealm(), resource.getName());
    if (roleModel != null) {
      throw new ConflictException(
          "role with name '" + roleModel.getName() + "' does already exist");
    }
    RoleModel newRoleModel = new RoleService(keycloakSession).createNewRole(resource);
    RealmRole realmRole = toScimRole(keycloakSession, newRoleModel);
    {
      ScimAdminEventBuilder adminEventAuditer =
          ((ScimKeycloakContext) context).getAdminEventAuditer();
      adminEventAuditer.createEvent(
          OperationType.CREATE,
          ResourceType.REALM_ROLE,
          String.format("roles/%s", newRoleModel.getId()),
          realmRole);
    }
    log.debug("Created realm role with name: {}", realmRole.getName());
    return realmRole;
  }

  /** {@inheritDoc} */
  @Override
  public RealmRole getResource(
      String id,
      List<SchemaAttribute> attributes,
      List<SchemaAttribute> excludedAttributes,
      Context context) {
    KeycloakSession keycloakSession = ((ScimKeycloakContext) context).getKeycloakSession();
    return new RoleService(keycloakSession)
        .getRoleModel(id)
        .map(roleModel -> toScimRole(keycloakSession, roleModel))
        .orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public PartialListResponse<RealmRole> listResources(
      long startIndex,
      int count,
      FilterNode filter,
      SchemaAttribute sortBy,
      SortOrder sortOrder,
      List<SchemaAttribute> attributes,
      List<SchemaAttribute> excludedAttributes,
      Context context) {
    KeycloakSession keycloakSession = ((ScimKeycloakContext) context).getKeycloakSession();
    List<RealmRole> roleModelList =
        keycloakSession
            .roles()
            .getRealmRolesStream(keycloakSession.getContext().getRealm())
            .map(roleModel -> toScimRole(keycloakSession, roleModel))
            .collect(Collectors.toList());
    return PartialListResponse.<RealmRole>builder()
        .totalResults(roleModelList.size())
        .resources(roleModelList)
        .build();
  }

  /** {@inheritDoc} */
  @Override
  public RealmRole updateResource(RealmRole resourceToUpdate, Context context) {
    KeycloakSession keycloakSession = ((ScimKeycloakContext) context).getKeycloakSession();
    RoleModel roleModel = new RoleService(keycloakSession).updateRole(resourceToUpdate);
    RealmRole realmRole = toScimRole(keycloakSession, roleModel);
    {
      ScimAdminEventBuilder adminEventAuditer =
          ((ScimKeycloakContext) context).getAdminEventAuditer();
      adminEventAuditer.createEvent(
          OperationType.UPDATE,
          ResourceType.REALM_ROLE,
          String.format("roles/%s", roleModel.getId()),
          realmRole);
    }
    log.debug("Updated realm role with name: {}", realmRole.getName());
    return realmRole;
  }

  /** {@inheritDoc} */
  @Override
  public void deleteResource(String id, Context context) {
    KeycloakSession keycloakSession = ((ScimKeycloakContext) context).getKeycloakSession();
    Optional<RoleModel> optionalRoleModel = new RoleService(keycloakSession).getRoleModel(id);
    if (!optionalRoleModel.isPresent()) {
      throw new ResourceNotFoundException("resource with id '" + id + "' does not exist");
    }
    keycloakSession.roles().removeRole(optionalRoleModel.get());
    {
      ScimAdminEventBuilder adminEventAuditer =
          ((ScimKeycloakContext) context).getAdminEventAuditer();
      adminEventAuditer.createEvent(
          OperationType.DELETE,
          ResourceType.REALM_ROLE,
          String.format("roles/%s", optionalRoleModel.get().getId()),
          RealmRole.builder()
              .id(optionalRoleModel.get().getId())
              .name(optionalRoleModel.get().getName())
              .build());
    }
    log.debug("Deleted realm role with name: {}", optionalRoleModel.get().getName());
  }

  /**
   * translates the given keycloak role to its SCIM representation
   *
   * @param keycloakSession to access the associates of the given role
   * @param roleModel the role that should be translated
   * @return the SCIM representation of the role
   */
  private RealmRole toScimRole(KeycloakSession keycloakSession, RoleModel roleModel) {
    Instant created =
        Optional.ofNullable(roleModel.getFirstAttribute(AttributeNames.RFC7643.CREATED))
            .map(Instant::parse)
            .orElse(Instant.now());
    Instant lastModified =
        Optional.ofNullable(roleModel.getFirstAttribute(AttributeNames.RFC7643.LAST_MODIFIED))
            .map(Instant::parse)
            .orElse(created);
    Meta meta = Meta.builder().created(created).lastModified(lastModified).build();

    RoleService roleService = new RoleService(keycloakSession);
    List<RoleAssociate> associates =
        roleService.getAssociatedMembers(roleModel).stream()
            .map(RealmRoleHandler::toAssociate)
            .collect(Collectors.toList());
    List<ChildRole> children =
        roleModel
            .getCompositesStream()
            .map(RealmRoleHandler::toChildRole)
            .collect(Collectors.toList());

    RealmRole role =
        RealmRole.builder()
            .id(roleModel.getId())
            .externalId(roleModel.getFirstAttribute(AttributeNames.RFC7643.EXTERNAL_ID))
            .name(roleModel.getName())
            .description(roleModel.getDescription())
            .associates(associates)
            .children(children)
            .meta(meta)
            .build();
    return role;
  }
}
