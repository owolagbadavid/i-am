package me.oreos.iam.entities.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.oreos.iam.entities.enums.EffectiveScopeEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPermissionModel {
    private Integer id;
    private String resourceType;
    private String action;
    private Integer resourceTypeId;
    private Integer actionId;
    private EffectiveScopeEnum effectiveScope;
}
