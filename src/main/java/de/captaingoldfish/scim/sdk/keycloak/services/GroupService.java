package de.captaingoldfish.scim.sdk.keycloak.services;

import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.jpa.GroupAdapter;
import org.keycloak.models.jpa.entities.GroupEntity;

/**
 * @author Pascal Knueppel
 * @since 27.08.2020
 */
@Slf4j
public class GroupService extends AbstractService {

  public GroupService(KeycloakSession keycloakSession) {
    super(keycloakSession);
  }

  /**
   * tries to find a group by its name
   *
   * @param name the name of the group
   * @return the group or an empty if no group with the given name does exist
   */
  public Optional<GroupModel> getGroupByName(String name) {
    RealmModel realmModel = getKeycloakSession().getContext().getRealm();

    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<GroupEntity> criteriaQuery = criteriaBuilder.createQuery(GroupEntity.class);
    Root<GroupEntity> root = criteriaQuery.from(GroupEntity.class);
    criteriaQuery.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get("name"), name),
            criteriaBuilder.equal(root.get("realm"), realmModel.getId())));
    try {
      GroupEntity groupEntity = entityManager.createQuery(criteriaQuery).getSingleResult();
      return Optional.of(new GroupAdapter(realmModel, entityManager, groupEntity));
    } catch (NoResultException ex) {
      log.debug(ex.getMessage(), ex);
      return Optional.empty();
    }
  }
}
