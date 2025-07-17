package me.oreos.iam.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;

import me.oreos.iam.entities.models.UserPermissionModel;
import me.oreos.iam.services.utils.Helper;
import me.oreos.iam.types.PermissionPair;

@Repository
public class CustomRepository {
    @Autowired
    private EntityManager entityManager;

    public List<UserPermissionModel> findUserPermissions(Integer userId, List<PermissionPair> permissionPairs) {
        String tupleList = formatPermissionPairs(permissionPairs);

        String sql = """
                SELECT perm.id AS id,
                       rt.code AS resourceType,
                       a.code AS action,
                       rt.id AS resourceTypeId,
                       a.id AS actionId,
                       rp.scope AS effectiveScope,
                       rp.resource_id AS resourceId
                FROM permissions perm
                JOIN role_permissions rp ON rp.permission_id = perm.id
                JOIN roles r ON rp.role_id = r.id
                JOIN user_roles ur ON ur.role_id = r.id
                JOIN users u ON ur.user_id = u.id
                JOIN actions a ON perm.action_id = a.id
                JOIN resource_types rt ON perm.resource_type_id = rt.id
                WHERE u.id = :userId
                  AND (a.id, rt.id) IN (""" + tupleList + ")"

                + " UNION "

                + """
                        SELECT perm.id AS id,
                               rt.code AS resourceType,
                               a.code AS action,
                               rt.id AS resourceTypeId,
                               a.id AS actionId,
                               pp.scope AS effectiveScope,
                               pp.resource_id AS resourceId
                        FROM permissions perm
                        JOIN policy_permissions pp ON pp.permission_id = perm.id
                        JOIN policies p ON pp.policy_id = p.id
                        JOIN user_policies up ON up.policy_id = p.id
                        JOIN users u ON up.user_id = u.id
                        JOIN actions a ON perm.action_id = a.id
                        JOIN resource_types rt ON perm.resource_type_id = rt.id
                        WHERE u.id = :userId
                          AND (a.id, rt.id) IN (""" + tupleList + ")";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId", userId);

        List<Object[]> resultList = query.getResultList();

        // You can map manually to List<Map<String, Object>>
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : resultList) {
            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("id", row[0]);
            rowMap.put("resourceType", row[1]);
            rowMap.put("action", row[2]);
            rowMap.put("resourceTypeId", row[3]);
            rowMap.put("actionId", row[4]);
            rowMap.put("effectiveScope", row[5]);
            rowMap.put("resourceId", row[6]);
            result.add(rowMap);
        }

        return Helper.mapToModel(result, new TypeReference<List<UserPermissionModel>>() {
        });
    }

    private String formatPermissionPairs(List<PermissionPair> permissionPairs) {
        return permissionPairs.stream()
                .map(pair -> "(" + pair.getActionId() + ", " + pair.getResourceTypeId() + ")")
                .collect(Collectors.joining(", "));
    }

}
