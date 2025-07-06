package me.oreos.iam.entities.enums;

public enum EnforcementScopeEnum {
    OWN("own"),
    GROUP("group");

    private String value;

    EnforcementScopeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
