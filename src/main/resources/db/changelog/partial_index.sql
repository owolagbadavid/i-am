-- Partial Indexes
-- CREATE UNIQUE INDEX uq_policy_permission_active ON policy_permissions (policy_id, permission_id) WHERE deleted_on IS NULL;
-- CREATE UNIQUE INDEX uq_role_permission_active ON role_permissions (role_id, permission_id) WHERE deleted_on IS NULL;

CREATE UNIQUE INDEX uq_user_policy_active ON user_policies (user_id, policy_id) WHERE deleted_on IS NULL;

CREATE UNIQUE INDEX uq_resource_permission_active ON resource_permissions (resource_id, permission_id) WHERE deleted_on IS NULL;

CREATE UNIQUE INDEX uq_user_group_active ON user_groups (user_id, group_id) WHERE deleted_on IS NULL;

CREATE UNIQUE INDEX uq_user_role_active ON user_roles (user_id, role_id) WHERE deleted_on IS NULL;

CREATE UNIQUE INDEX uq_permission_active ON permissions (resource_type_id, action_id) WHERE deleted_on IS NULL;