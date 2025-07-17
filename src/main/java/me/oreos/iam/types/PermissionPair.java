package me.oreos.iam.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PermissionPair {
    Integer actionId;
    Integer resourceTypeId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PermissionPair that = (PermissionPair) o;
        return actionId.equals(that.actionId) && resourceTypeId.equals(that.resourceTypeId);
    }

    @Override
    public int hashCode() {
        return 31 * actionId.hashCode() + resourceTypeId.hashCode();
    }
}
