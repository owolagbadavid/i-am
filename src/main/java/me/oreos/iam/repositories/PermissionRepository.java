package me.oreos.iam.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;


@Repository
public interface PermissionRepository extends BaseRepository<me.oreos.iam.entities.Permission, Integer> {

  @Query(value = """
      SELECT
          perm.id AS id,
          rt.code AS "resourceType",
          a.code AS "action",
          rt.id AS "resourceTypeId",
          a.id AS "actionId",
          rp.scope AS "effectiveScope",
          rp.resource_id AS "resourceId"
      FROM permissions perm
      JOIN role_permissions rp ON rp.permission_id = perm.id
      JOIN roles r ON rp.role_id = r.id
      JOIN user_roles ur ON ur.role_id = r.id
      JOIN users u ON ur.user_id = u.id
      JOIN actions a ON perm.action_id = a.id
      JOIN resource_types rt ON perm.resource_type_id = rt.id
      WHERE u.id = :userId
        AND a.id = :actionId
        AND rt.id = :resourceTypeId

      UNION

      SELECT
          perm.id AS id,
          rt.code AS "resourceType",
          a.code AS "action",
          rt.id AS "resourceTypeId",
          a.id AS "actionId",
          pp.scope AS "effectiveScope",
          pp.resource_id AS "resourceId"
      FROM permissions perm
      JOIN policy_permissions pp ON pp.permission_id = perm.id
      JOIN policies p ON pp.policy_id = p.id
      JOIN user_policies up ON up.policy_id = p.id
      JOIN users u ON up.user_id = u.id
      JOIN actions a ON perm.action_id = a.id
      JOIN resource_types rt ON perm.resource_type_id = rt.id
      WHERE u.id = :userId
        AND a.id = :actionId
        AND rt.id = :resourceTypeId
      """, nativeQuery = true)
  List<Map<String, Object>> findUserPermissions(
      @Param("userId") Integer userId,
      @Param("actionId") Integer actionId,
      @Param("resourceTypeId") Integer resourceTypeId);

  @Query(value = """
      SELECT
          perm.id AS id,
          rt.code AS "resourceType",
          a.code AS "action",
          rt.id AS "resourceTypeId",
          a.id AS "actionId",
          rp.scope AS "enforcementScope"
      FROM permissions perm
      JOIN resource_permissions rp ON rp.permission_id = perm.id
      JOIN resources r ON rp.resource_id = r.id
      JOIN actions a ON perm.action_id = a.id
      JOIN resource_types rt ON perm.resource_type_id = rt.id
      WHERE r.id = :resourceId
      """, nativeQuery = true)
  List<Map<String, Object>> findResourcePermissions(@Param("resourceId") Integer resourceId);
}
